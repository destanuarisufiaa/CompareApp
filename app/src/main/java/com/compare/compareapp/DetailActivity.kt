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
    lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityDetailBinding
    private lateinit var updateJudulMenu : EditText
    private lateinit var updateHargaMenu : EditText
    private lateinit var updateDesc : EditText
    private lateinit var updateImage : ImageView
    private lateinit var deleteButton: FloatingActionButton
    private lateinit var editButton: FloatingActionButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateJudulMenu = findViewById(R.id.updateJudulMenu)
        updateHargaMenu = findViewById(R.id.updateHargaMenu)
        updateDesc = findViewById(R.id.updateDesc)
        updateImage = findViewById(R.id.updateImage)
        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)


        val bundle = intent.extras
        if (bundle != null) {
//            val id = IDdoc.text
            binding.detailTittle.text = bundle.getString("namaMenu")
            binding.detailHarga.text = bundle.getString("Harga")
            binding.detailDesc.text = bundle.getString("Desc")
            binding.IDdoc.text = bundle.getString("docID")
            imageURL = bundle.getString("Image")!!
            Glide.with(this).load(bundle.getString("Image")).into(binding.detailImage)
        }
        deleteButton.setOnClickListener {
            if (bundle != null) {
                bundle.getString("docID")
            }
        }
        editButton.setOnClickListener {
            val edJudul = updateJudulMenu.text.toString().trim()
            val edHarga = updateHargaMenu.text.toString().trim()
            val edDesc = updateDesc.text.toString().trim()
            val intent = Intent(this, UpdateActivity::class.java)
            startActivity(intent)

            val mapUpdate = hashMapOf<String, Any>(
                "namaMenu" to edJudul,
                "Harga" to edHarga,
                "Desc" to edDesc,
            )

            db.collection("Menu").document("").update(mapUpdate)
                .addOnSuccessListener {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun deleteData() {
        db.collection("Menu").document("docID")
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }
}