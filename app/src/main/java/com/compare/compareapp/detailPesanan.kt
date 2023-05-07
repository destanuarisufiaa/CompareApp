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
    private lateinit var hasilStatus : String
    private lateinit var mEditTextInput: EditText
    private lateinit var mTextViewCountDown: TextView
    private lateinit var mButtonSet: Button
    private lateinit var mButtonStartPause: Button
    private lateinit var mButtonReset: Button
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

        supportActionBar?.setTitle("EaTrain-App Admin")

        binding.recyclerViewItemRiwayat.apply {
            layoutManager = LinearLayoutManager(context)
        }

        val status = intent.getStringExtra("status")
        if (status.equals("PERSIAPAN")){
            binding.rgStatusPesanan.check(R.id.persiapan)
        }else if (status.equals("ANTAR")){
            binding.rgStatusPesanan.check(R.id.antar)
        }else if (status.equals("SELESAI")){
            binding.rgStatusPesanan.check(R.id.persiapan)
        }

        fetchDataPesanan()

        btn_updateStatus.setOnClickListener {
            statusPesanan()
        }

        mEditTextInput = findViewById(R.id.edit_text_input)
        mTextViewCountDown = findViewById(R.id.text_view_countdown)
        mButtonSet = findViewById(R.id.button_set)
        mButtonStartPause = findViewById(R.id.button_start_pause)
        mButtonReset = findViewById(R.id.button_reset)

        mButtonSet.setOnClickListener {
            val input = mEditTextInput.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Field can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val millisInput = input.toLongOrNull()?.times(60000)
            if (millisInput == null || millisInput == 0L) {
                Toast.makeText(this, "Please enter a positive number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            setTime(millisInput)
            mEditTextInput.text.clear()
        }

        mButtonStartPause.setOnClickListener {
            if (mTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        mButtonReset.setOnClickListener {
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

    private fun statusPesanan() {
        val cekGenderRadioButtonId = rg_statusPesanan.checkedRadioButtonId
        val listStatus = findViewById<RadioButton>(cekGenderRadioButtonId)
        hasilStatus = "${listStatus.text}"

        val edStatus = hasilStatus.trim()

        val dbUpdatePesanan = FirebaseFirestore.getInstance()
        val bahanStatus = hashMapOf<String, Any>(
            "status" to edStatus,
        )
        val orderID = intent.getStringExtra("orderID").toString()
        dbUpdatePesanan.collection("pesanan").document(orderID).update(bahanStatus)
            .addOnSuccessListener{ documentReference ->
                Toast.makeText(this, "Sukses Perubahan Status", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("direct", "true")
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed!, gagal", Toast.LENGTH_SHORT).show()
            }

    }

    private fun setTime(milliseconds: Long) {
        mStartTimeInMillis = milliseconds
        resetTimer()
        closeKeyboard()
    }

    private fun startTimer() {
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