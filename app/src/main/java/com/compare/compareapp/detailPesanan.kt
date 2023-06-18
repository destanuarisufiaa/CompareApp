package com.compare.compareapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.compare.compareapp.databinding.ActivityDetailPesananBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_pesanan.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class detailPesanan : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPesananBinding
    private lateinit var mEditTextInput: EditText
    private lateinit var mTextViewCountDown: TextView
    private lateinit var mButtonSet: Button
    private lateinit var mButtonStartPause: Button
    private lateinit var mButtonReset: Button
    private lateinit var hasilStatus:String
    var db = Firebase.firestore
    val countdownsRef = db.collection("countdowns")

    private var mCountDownTimer: CountDownTimer? = null
    private var mTimerRunning = false
    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0
    private var mEndTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //pada detailActivity terdapat actionBar dengan judul Eatrain-App Admin
        supportActionBar?.setTitle("EaTrain-App Admin")

        //untuk mengatur tampilan layout dari RecyclerView pada activity
        binding.recyclerViewItemRiwayat.apply {
            // menampilkan item yang disusun dalam daftar linear vertikal.
            layoutManager = LinearLayoutManager(context)
        }

        //mengambil nilai "status" dari Intent dan menyimpannya ke dalam variabel status.
        val status = intent.getStringExtra("status")
        // mengecek nilai dari status
        if (status.equals("PERSIAPAN")){
            binding.rgStatusPesanan.check(R.id.persiapan)
        }else if (status.equals("ANTAR")){
            binding.rgStatusPesanan.check(R.id.antar)
        }else if (status.equals("SELESAI")){
            binding.rgStatusPesanan.check(R.id.selesai)
        }else binding.rgStatusPesanan.clearCheck()

        fetchDataPesanan()

        //apabila button save pada update status ditekan maka akan memanggil fungsi status pesanan
        btn_updateStatus.setOnClickListener {
            val cekGenderRadioButtonId = binding.rgStatusPesanan.checkedRadioButtonId
            if (cekGenderRadioButtonId != -1) {
                val listStatus = findViewById<RadioButton>(cekGenderRadioButtonId)
                hasilStatus = "${listStatus.text}"
                val edStatus = hasilStatus.trim()
                statusPesanan(edStatus)
            } else {
                Toast.makeText(this, "Anda belum memilih status", Toast.LENGTH_SHORT).show()
            }
        }

        //inisialisasi untuk menghubungkan elemen UI pada file XML berdasarkan ID
        mEditTextInput = findViewById(R.id.edit_text_input)
        mTextViewCountDown = findViewById(R.id.text_view_countdown)
        mButtonSet = findViewById(R.id.button_set)
        mButtonStartPause = findViewById(R.id.button_start_pause)
        mButtonReset = findViewById(R.id.button_reset)

        //apabila button set ditekan
        mButtonSet.setOnClickListener {
            //mengambil teks dari mEditTextInput dan dimasukkan pda variabel input
            val input = mEditTextInput.text.toString()
            //jika inputannya kosong
            if (input.isEmpty()) {
                //maka akan menampilkan toast dengan teks dibawah
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show()
                // dan menghentikan eksekusi blok dengan return
                return@setOnClickListener
            }
            //mengkonversi nilai input menjadi tipe Long dan mengkonversi menit menjadi milidetik (mengkali dgn 6000)
            //lalu dimasukkan pada variabel milisInput
            val millisInput = input.toLongOrNull()?.times(60000)
            //memeriksa apakah nilai nya null atau 0
            if (millisInput == null || millisInput == 0L) {
                //jika iya menampilkan pesan toast di bawah
                Toast.makeText(this, "Please enter a positive number", Toast.LENGTH_SHORT).show()
                // dan menghentikan eksekusi blok dengan return
                return@setOnClickListener
            }
            //memanggil fungsi set time dengan isi milisInput untuk mengatur waktu pada timer
            setTime(millisInput)
            //kemudian mengosongkan teks pada mEditTextInput
            mEditTextInput.text.clear()
        }

        //apabila tommbol start/pause ditekan
        mButtonStartPause.setOnClickListener {
            //jika timer sedang berjalan
            if (mTimerRunning) {
                //memanggil fungsi pause untuk menjeda
                pauseTimer()
            //jika timer tidak berjalan / else
            } else {
                //maka memanggil fungsi start timer untuk memulai timer
                startTimer()
            }
        }

        //jika button reset ditekan
        mButtonReset.setOnClickListener {
            //memanggil fungsi reset timer untuk mengatur ulang timer
            resetTimer()
        }
    }

    private fun fetchDataPesanan() {

        val orderID = intent.getStringExtra("orderID").toString()
        val listRiwayatPesanan = mutableListOf<itemDataRiwayat>()
        val riwayatPesanan = FirebaseFirestore.getInstance().collection("pesanan").document("$orderID").collection("menu")
        riwayatPesanan.get()
            .addOnSuccessListener { documents ->
                for (document in documents)
                {

                    if (document.id != "total")
                    {
                        val pesananRiwayat = document.toObject(itemDataRiwayat::class.java)
                        listRiwayatPesanan.add(pesananRiwayat)
                    }
                    binding.recyclerViewItemRiwayat.adapter = itemRiwayatAdapter (this,listRiwayatPesanan)
                }
            }
    riwayatPesanan.document("total").get()
        .addOnSuccessListener {
            tv_totalForm.text = it.getString("total")
        }
    }

    private fun statusPesanan(edStatus:String) {

        //inisialisasi firestore dan menyimpan pada var dbUpdatePesanan
        val dbUpdatePesanan = FirebaseFirestore.getInstance()
        val bahanStatus = hashMapOf<String, Any>(
            "status" to edStatus,
        )
        //mendapatkan nilai orderID dari intent
        val orderID = intent.getStringExtra("orderID").toString()
        //mengupdate dokumen pada collection pesanan dengan ID OrderID
        dbUpdatePesanan.collection("pesanan").document(orderID).update(bahanStatus)
            //apabila perubahan berhasil dilakukan
            .addOnSuccessListener{ documentReference ->
                //maka menampilkan text "Sukses Perubahan Status"
                Toast.makeText(this, "Sukses Perubahan Status", Toast.LENGTH_SHORT).show()
                //dan berpindah halaman pada MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("direct", "true")
                startActivity(intent)
            //jika gagal
            }.addOnFailureListener {
                //menampilkan toast "Failed!,gagal"
                Toast.makeText(this, "Failed!, gagal", Toast.LENGTH_SHORT).show()
            }

    }
    //fungsi untuk ngeset waktu awal pada timer
    private fun setTime(milliseconds: Long) {
        //menetapkan nilai mStartTimeInMilis nilainya miliseconds
        mStartTimeInMillis = milliseconds
        //memanggil fungsi reset untuk mengatur ulang timer
        resetTimer()
        //manggil fungsi closeKeyboard untuk menyembunyikan keyboard jika terbuka
        closeKeyboard()
    }

    //fungsi untuk memulai timer
    private fun startTimer() {
        //menghitung mEndTime dengan
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis

        val countdownData = hashMapOf(
            "start_time" to System.currentTimeMillis(),
            "time_left" to mTimeLeftInMillis
        )
        countdownsRef.document().set(countdownData)

        mCountDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                updateCountDownText()
            }

            override fun onFinish() {
                mTimerRunning = false
                updateWatchInterface()
            }
        }.start()

        mTimerRunning = true
        updateWatchInterface()
    }

    private fun pauseTimer() {
        mCountDownTimer?.cancel()
        mTimerRunning = false
        updateWatchInterface()
    }

    private fun resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis
        updateCountDownText()
        updateWatchInterface()
    }

    private fun updateCountDownText() {
        val hours = (mTimeLeftInMillis / 1000) / 3600
        val minutes = ((mTimeLeftInMillis / 1000) % 3600) / 60
        val seconds = (mTimeLeftInMillis / 1000) % 60

        val timeLeftFormatted = if (hours > 0) {
            String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        mTextViewCountDown.text = timeLeftFormatted
    }

    private fun updateWatchInterface() {
        if (mTimerRunning) {
            mEditTextInput.visibility = View.INVISIBLE
            mButtonSet.visibility = View.INVISIBLE
            mButtonReset.visibility = View.INVISIBLE
            mButtonStartPause.text = "Pause"
        } else {
            mEditTextInput.visibility = View.VISIBLE
            mButtonSet.visibility = View.VISIBLE
            mButtonStartPause.text = "Start"

            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.visibility = View.INVISIBLE
            } else {
                mButtonStartPause.visibility = View.VISIBLE
            }

            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.visibility = View.VISIBLE
            } else {
                mButtonReset.visibility = View.INVISIBLE
            }
        }
    }

    private fun closeKeyboard() {
        val view: View? = currentFocus
        if (view != null) {
            val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onStop() {
        super.onStop()

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putLong("startTimeInMillis", mStartTimeInMillis)
        editor.putLong("millisLeft", mTimeLeftInMillis)
        editor.putBoolean("timerRunning", mTimerRunning)
        editor.putLong("endTime", mEndTime)

        editor.apply()

        mCountDownTimer?.cancel()
    }

    override fun onStart() {
        super.onStart()

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)

        countdownsRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val startTime = document.getLong("start_time")!!
                    val timeLeft = document.getLong("time_left")!!

                    // Menghitung waktu berakhir dari waktu mulai dan waktu tersisa
                    val endTime = startTime + timeLeft

                    // Mengecek apakah countdown sudah berakhir
                    if (System.currentTimeMillis() < endTime) {
                        mStartTimeInMillis = startTime
                        mTimeLeftInMillis = endTime - System.currentTimeMillis()
                        startTimer()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting countdowns", exception)
            }

        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000)
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis)
        mTimerRunning = prefs.getBoolean("timerRunning", false)

        updateCountDownText()
        updateWatchInterface()

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0)
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis()

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0
                mTimerRunning = false
                updateCountDownText()
                updateWatchInterface()
            } else {
                startTimer()
            }
        }
    }
}