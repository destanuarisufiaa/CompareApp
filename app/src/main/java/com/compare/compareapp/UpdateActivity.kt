package com.compare.compareapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_update.*
import org.checkerframework.checker.interning.qual.InternMethod

class UpdateActivity : AppCompatActivity() {

    private lateinit var updateImage : ImageView
    private lateinit var updateButton: Button
    private lateinit var updateJudulMenu : EditText
    private lateinit var updateHargaMenu : EditText
    private lateinit var updateDesc : EditText
    private lateinit var foto : String
    private lateinit var key : String
    private lateinit var oldImageURL : String
    private lateinit var uri: Uri
    lateinit var storageReference : StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        updateButton = findViewById(R.id.updateButton)
        updateJudulMenu = findViewById(R.id.updateJudulMenu)
        updateHargaMenu = findViewById(R.id.updateHargaMenu)
        updateDesc = findViewById(R.id.updateDesc)
        updateImage = findViewById(R.id.updateImage)

        val activityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                uri = data!!.data!!
                updateImage.setImageURI(uri)
            }else {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        }
        val bundle = intent.extras
        if (bundle !=null) {
            Glide.with(this).load(bundle.getString("Image")).into(updateImage)
            updateJudulMenu.setText(bundle.getString("namaMenu"))
            updateHargaMenu.setText(bundle.getString("Harga"))
            updateDesc.setText(bundle.getString("Desc"))
            key = bundle.getString("Key")!!
            oldImageURL = bundle.getString("Image")!!
        }
        FirebaseFirestore.getInstance().collection("Menu")

        updateImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        updateButton.setOnClickListener{
            saveData()
            val intent = Intent(this,update_produk::class.java)
            startActivity(intent)
        }
    }

    private fun saveData (){
        val menu = updateJudulMenu.text.toString()
        val harga = updateHargaMenu.text.toString()

        storageReference = FirebaseStorage.getInstance().getReference().child("Task Images")
            .child(uri!!.lastPathSegment!!)

        val builder = AlertDialog.Builder(this@UpdateActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
            val uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isComplete);
            val urlImage = uriTask.result
            foto = urlImage.toString()
            dialog.dismiss()
            updateData(menu, harga, foto)
            dialog.dismiss()
        }.addOnFailureListener{
            dialog.dismiss()
        }
    }
    private fun updateData(menu: String, harga : String, foto : String){

        val db = FirebaseFirestore.getInstance()
        val listMenu = hashMapOf<String, Any>(
            "namaMenu" to menu,
            "Harga" to harga,
            "Foto" to foto,
        )
        db.collection("Menu")
            .add(listMenu)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }
    }

}