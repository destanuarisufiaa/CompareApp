package com.compare.compareapp

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.compare.compareapp.databinding.ActivityUpdateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_update.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private lateinit var updateeJudul : EditText
    private lateinit var updateeHarga : EditText
    private lateinit var updateeDesc : EditText
    private lateinit var updateeImage : ImageView
    private lateinit var buttonUpdate : Button
    private lateinit var UID : TextView
    private lateinit var binding: ActivityUpdateBinding
    var imageURL = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //cek permission upload gambar
        updateImage.isEnabled = true

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            )!= PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }else{
            updateImage.isEnabled = true
        }

        updateImage.setOnClickListener {
            selectImage()
        }

        //inisialisasi layout
        updateeJudul = findViewById(R.id.updateJudulMenu)
        updateeHarga = findViewById(R.id.updateHargaMenu)
        updateeDesc = findViewById(R.id.updateDesc)
        updateeImage = findViewById(R.id.updateImage)
        buttonUpdate = findViewById(R.id.updateButton)
        UID = findViewById(R.id.UID)

        //mengambil deskripsi menu yang akan diedit dari detail
        val bundle = intent.extras
        if (bundle != null) {
            val judulupdate = bundle!!.getString("namaMenu")
            val hargaupdate = bundle!!.getString("Harga")
            val deskripsi = bundle!!.getString("Desc")
            val docID = bundle!!.getString("docID")

            //memasukkan deskripsi menu ke dalam edittext
            UID.text = docID
            updateeJudul.setText(judulupdate)
            updateeHarga.setText(hargaupdate)
            updateeDesc.setText(deskripsi)
            imageURL = bundle.getString("Foto")!!
            Glide.with(this).load(bundle?.getString("Foto")).into(updateeImage)
        }

        buttonUpdate.setOnClickListener {
            uploadData()
        }

    }

    private fun selectImage() {
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
                    updateImage.post { updateImage.setImageBitmap(bitmap) }
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
                updateImage.post { updateImage.setImageBitmap(bitmap) }
            }
            thread.start()
        }
    }
    private fun uploadData(){
        val edMenu = updateeJudul.text.toString().trim()
        val edHarga = updateeHarga.text.toString().trim()
        val edDesc = updateeDesc.text.toString().trim()

        updateImage.isDrawingCacheEnabled = true
        updateImage.buildDrawingCache()
        val bitmap = (updateImage.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        //UPLOAD
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

//        val currentDate : String = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        val storage = FirebaseStorage.getInstance()
        val reference = storage.getReference("images").child("IMG"+ Date().time +".jpeg")
        var uploadTask = reference.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { taskSnapshot ->
            if(taskSnapshot.metadata !=null){
                if(taskSnapshot.metadata!!.reference !=null){
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                        FirebaseStorage.getInstance().getReferenceFromUrl(imageURL).delete()
                        val bundle = intent.extras
                        val docID = bundle!!.getString("docID").toString().trim()
                        var editfoto = it.getResult().toString()
                        val dbupdate = FirebaseFirestore.getInstance()
                        val updateMenu = hashMapOf<String, Any>(
                            "namaMenu" to edMenu,
                            "Harga" to edHarga,
                            "Foto" to editfoto,
                            "Desc" to edDesc,
                        )
                        dbupdate.collection("Menu").document("$docID").update(updateMenu)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { exception ->
                                dialog.dismiss()
                                Toast.makeText(this, "Failed!, gagal $docID", Toast.LENGTH_SHORT).show()
                            }
                    }
                }else{
                    dialog.dismiss()
                    Toast.makeText(this, "Failed 1!", Toast.LENGTH_SHORT).show()
                }
            }else{
                dialog.dismiss()
                Toast.makeText(this, "Failed 2!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    }


