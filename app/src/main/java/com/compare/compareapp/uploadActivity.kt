package com.compare.compareapp

import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.compare.compareapp.databinding.ActivityUploadBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.Date


class uploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    var foto : String = ""
    var menu : String = ""
    var harga : String = ""
    var desc : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uploadImage.isEnabled = true

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )!= PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }else{
            uploadImage.isEnabled = true
        }

        binding.uploadImage.setOnClickListener {
            selectImage()
        }
        binding.saveButton.setOnClickListener {
            uploadData(menu, harga, desc, foto)
        }
    }

    private fun selectImage(){
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_name))
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            if (items[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 10)
            } else if (items[item] == "Choose from Library") {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 20)
            } else if (items[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            val path : Uri? = data.data
            val thread = Thread {
                try {
                    val inputStream = contentResolver.openInputStream(path!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    uploadImage.post { uploadImage.setImageBitmap(bitmap) }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            thread.start()
        }


        if (requestCode == 10 && resultCode == RESULT_OK) {
            val extras = data!!.extras
            val thread = Thread {
                val bitmap = extras!!["data"] as Bitmap?
                uploadImage.post { uploadImage.setImageBitmap(bitmap) }
            }
            thread.start()
        }
    }

    private fun uploadData(menu: String, harga : String, desc : String, foto: String){

        val menu = binding.uploadJudulMenu.text.toString()
        val harga = binding.uploadHargaMenu.text.toString()
        val desc = binding.uploadDesc.text.toString()

        uploadImage.isDrawingCacheEnabled = true
        uploadImage.buildDrawingCache()
        val bitmap = (uploadImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        //UPLOAD
        val builder = AlertDialog.Builder(this@uploadActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        val storage = FirebaseStorage.getInstance()
        val reference = storage.reference.child("Task Images")
        var uploadTask = reference.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            if(taskSnapshot.metadata !=null){
                if(taskSnapshot.metadata!!.reference !=null){
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                       var foto = it.getResult().toString()
                       saveData(menu, harga, desc, foto)
                    }
                }else{
                    dialog.dismiss()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }else{
                dialog.dismiss()
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveData(menu: String, harga : String, desc : String,  foto : String){
        val db = FirebaseFirestore.getInstance()
        val listMenu = hashMapOf<String, Any>(
            "namaMenu" to menu,
            "Harga" to harga,
            "Foto" to foto,
            "Desc" to desc,
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