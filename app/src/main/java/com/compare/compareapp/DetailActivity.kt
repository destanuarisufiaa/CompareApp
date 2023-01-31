package com.compare.compareapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.compare.compareapp.databinding.ActivityDetailBinding
import com.github.clans.fab.FloatingActionButton
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_detail.*
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

        detailTittle = findViewById(R.id.detailTittle)
        detailHarga = findViewById(R.id.detailHarga)
        detailDesc = findViewById(R.id.detailDesc)
        detailImage = findViewById(R.id.detailImage)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)


        val bundle = intent.extras
        if (bundle !=null){
//            binding.IDdoc.text = bundle.getString("docID")
            binding.detailTittle.text = bundle.getString("namaMenu")
            binding.detailHarga.text = bundle.getString("Harga")
            binding.detailDesc.text = bundle.getString("Desc")
            imageURL = bundle.getString("Image")!!
            Glide.with(this).load(bundle.getString("Image")).into(binding.detailImage)
        }
        deleteButton.setOnClickListener {
//            deleteData(id)
        }
        editButton.setOnClickListener {
            val intent = Intent(this,UpdateActivity::class.java)
                .putExtra("namaMenu", detailTittle.text.toString())
                .putExtra("Harga", detailTittle.text.toString())
                .putExtra("Desc", detailTittle.text.toString())
                .putExtra("Foto", imageURL)
            startActivity(intent)
        }
    }
}