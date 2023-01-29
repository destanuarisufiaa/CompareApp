package com.compare.compareapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.compare.compareapp.databinding.ActivityDetailBinding
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    var imageURL = ""
//    var key : String = ""
    lateinit var db : FirebaseFirestore
    private lateinit var binding:ActivityDetailBinding
    private lateinit var deleteButton : FloatingActionButton
    private lateinit var editButton : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        deleteButton = findViewById(R.id.deleteButton)
        editButton = findViewById(R.id.editButton)

        val bundle = intent.extras
        if (bundle !=null){
            binding.detailTittle.text = bundle.getString("namaMenu")
            binding.detailHarga.text = bundle.getString("Harga")
            binding.detailDesc.text = bundle.getString("Desc")
            imageURL = bundle.getString("Image")!!
            Glide.with(this).load(bundle.getString("Image")).into(binding.detailImage)
        }
        deleteButton.setOnClickListener {
            deleteData()
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
    private fun deleteData (){
        db.collection("Menu").document()
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener {
                    e -> Log.w(TAG, "Error deleting document", e)
            }
        val storage = FirebaseStorage.getInstance()

        val storageReference = storage.getReferenceFromUrl(imageURL)
        storageReference.delete().addOnSuccessListener {

        }
    }
}