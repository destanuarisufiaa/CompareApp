package com.compare.compareapp

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.compare.compareapp.databinding.ActivityUploadBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.DateFormat
import java.util.Calendar


class uploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    var imageURL: String? = null
    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data!!.data
                binding.uploadImage.setImageURI(uri)
            }else {
                Toast.makeText(this@uploadActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }
        binding.uploadImage.setOnClickListener{
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.saveButton.setOnClickListener{
            val menu = binding.uploadJudulMenu.text.toString()
            val harga = binding.uploadHargaMenu.text.toString()
            uploadData(menu, harga)
            saveData()
        }
    }
    private fun saveData(){
        val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
            .child(uri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@uploadActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            imageURL = urlImage.toString()
            dialog.dismiss()
        }.addOnFailureListener{
            dialog.dismiss()
        }
    }
    private fun uploadData(menu: String, harga : String){
        val db = FirebaseFirestore.getInstance()
        val listMenu = hashMapOf<String, Any>(
            "Nama Menu" to menu,
            "Harga" to harga,
        )
        db.collection("Menu")
            .add(listMenu)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this@uploadActivity, "Success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@uploadActivity, "Failed!", Toast.LENGTH_SHORT).show()
            }
    }
}