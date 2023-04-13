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

    var imageURL = ""
    private lateinit var detailTittle : TextView
    private lateinit var detailHarga : TextView
    private lateinit var detailDesc : TextView
    private lateinit var detailImage : ImageView
    private lateinit var binding:ActivityDetailBinding
    private lateinit var deleteButton : FloatingActionButton
    private lateinit var editButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle("EaTrain-App Admin")

        detailTittle = findViewById(R.id.detailTittle)
        detailHarga = findViewById(R.id.detailHarga)
        detailDesc = findViewById(R.id.detailDesc)
        detailImage = findViewById(R.id.detailImage)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)


        val bundle = intent.extras
        if (bundle !=null){
            binding.detailTittle.text = bundle.getString("namaMenu")
            binding.detailHarga.text = bundle.getString("Harga")
            binding.detailDesc.text = bundle.getString("Desc")
            binding.IDdoc.text = bundle.getString("docID")
            imageURL = bundle.getString("Image")!!
            Glide.with(this).load(bundle.getString("Image")).into(binding.detailImage)
        }
        deleteButton.setOnClickListener {
            val bundle = intent.extras
            val docID = bundle!!.getString("docID").toString().trim()
            val db = FirebaseFirestore.getInstance()
            db.collection("Menu").document("$docID")
                .delete()
                .addOnSuccessListener {
                    FirebaseStorage.getInstance().getReferenceFromUrl(imageURL).delete()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Data Berhasil Di Hapus!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Data Gagal Di Hapus!", Toast.LENGTH_SHORT).show()
                }
        }
        editButton.setOnClickListener {
            val intent = Intent(this,UpdateActivity::class.java)
                .putExtra("namaMenu", detailTittle.text.toString())
                .putExtra("Harga", detailHarga.text.toString())
                .putExtra("Desc", detailDesc.text.toString())
                .putExtra("Foto", imageURL)
                .putExtra("docID", IDdoc.text.toString().trim())
            startActivity(intent)
        }
    }
}