package com.compare.compareapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compare.compareapp.databinding.ActivityDetailBinding
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.recycler_item.*


class DetailActivity : AppCompatActivity() {

    //membuat variabel imageURL yang isinya kosong
    var imageURL = ""
    private lateinit var detailTittle : TextView
    private lateinit var detailHarga : TextView
    private lateinit var detailDesc : TextView
    private lateinit var detailImage : ImageView
    private lateinit var binding:ActivityDetailBinding
    private lateinit var deleteButton : FloatingActionButton
    private lateinit var editButton : FloatingActionButton
    private lateinit var docID:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //pada detailActivity terdapat actionBar dengan judul Eatrain-App Admin
        supportActionBar?.setTitle("EaTrain-App Admin")

        //mendefisinikan variabel tersebut ditampilan pada xml di masing-masing id
        detailTittle = findViewById(R.id.detailTittle)
        detailHarga = findViewById(R.id.detailHarga)
        detailDesc = findViewById(R.id.detailDesc)
        detailImage = findViewById(R.id.detailImage)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)

        //digunakan untuk mendapatkan atau mengambil data yang dikirimkan dari activity home (MyAdapter) melalui intent.
        val bundle = intent.extras
        if (bundle !=null){
            binding.detailTittle.text = bundle.getString("namaMenu")
            binding.detailHarga.text = bundle.getString("Harga")
            binding.detailDesc.text = bundle.getString("Desc")
            imageURL = bundle.getString("Image")!!
            docID= bundle.getString("docID")!!
            binding.documentID.text = docID
            // library Glide digunakan untuk menampilkan gambar ke dalam ImageView dengan ID detailImage
            Glide.with(this).load(bundle.getString("Image")).into(binding.detailImage)

        }
        //apabila tombol delete ditekan
        deleteButton.setOnClickListener {
            //mendapatkan instance dari firebase firestore yang dimakasukkan pada variabel db
            val db = FirebaseFirestore.getInstance()
            //inislisasi data atau dokumen yang akan diambil atau didapatkan dalam firestore
            db.collection("Menu").document("$docID")
                //menghapus data yang telah diambil
                .delete()
                //inisialisasi sukses
                .addOnSuccessListener {
                    //apabila sukses maka menghapus data url pada firebase storage untuk dihapus
                    FirebaseStorage.getInstance().getReferenceFromUrl(imageURL).delete()
                    //dan juga intent atau berpindah halaman pada MainActivity
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    //jika sukses jga akan menampilkan toast kalimat "Data berhasil di hapus"
                    Toast.makeText(this, "Data Berhasil Di Hapus!", Toast.LENGTH_SHORT).show()
                //inisialisasi gagal
                }.addOnFailureListener {
                    //jika gagal menampilkan toast kalimat "Data gagal di hapus"
                    Toast.makeText(this, "Data Gagal Di Hapus!", Toast.LENGTH_SHORT).show()
                }
        }
        //apabila button atau tombol edit button di klik
        editButton.setOnClickListener {
            //inisialisasi variabel untuk berpindah halaman ke activity update (edit)
            val intent = Intent(this,UpdateActivity::class.java)
                //putExtra digunakan untuk menambahkan data tambahan ke intent yang akan dikirim pada activity update
                .putExtra("namaMenu", detailTittle.text.toString())
                .putExtra("Harga", detailHarga.text.toString())
                .putExtra("Desc", detailDesc.text.toString())
                .putExtra("Foto", imageURL)
                .putExtra("docID", docID)
            //Setelah data tambahan berhasil ditambahkan, intent akan digunakan untuk memulai UpdateActivity dgn memanggil startActivity
            startActivity(intent)
        }
    }
}