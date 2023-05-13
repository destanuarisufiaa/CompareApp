package com.compare.compareapp

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.compare.compareapp.databinding.ActivityUpdateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_update.*
import kotlinx.android.synthetic.main.activity_upload.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private lateinit var updateeJudul : EditText
    private lateinit var updateeHarga : EditText
    private lateinit var updateeDesc : EditText
    private lateinit var updateeImage : ImageView
    private lateinit var buttonUpdate : Button
    private lateinit var UID : TextView
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var currentPhotoPath : String
    var imageURL = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle("EaTrain-App Admin")

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
                // Ambil gambar menggunakan kamera
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Pastikan ada aplikasi kamera yang dapat menangani intent ini
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        // Buat file gambar sementara untuk menyimpan hasil kamera
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            // Error saat membuat file
                            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT)
                                .show()
                            null
                        }
                        // Jika file berhasil dibuat, lanjutkan mengambil gambar dari kamera
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this, "com.compare.compareapp.fileprovider", it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, 10)
                        }
                    }
                }
            }

            else if (items[item] == "Choose from Library") {
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
            //crop
            //  path?.let { startCrop(it) }
            val destinationUri = Uri.fromFile(File(cacheDir, "IMG_" + System.currentTimeMillis()))
            val options = UCrop.Options()
            options.setCompressionQuality(80)
            options.setToolbarTitle(getString(R.string.app_name))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarWidgetColor(Color.WHITE)
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
            UCrop.of(path!!, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(720,720)
                .withOptions(options)
                .start(this)
        }

        //kamera
        if (requestCode == 10 && resultCode == RESULT_OK) {
            val imageUri = Uri.fromFile(File(currentPhotoPath))
            val path = Uri.fromFile(File(cacheDir, "IMG_" + System.currentTimeMillis()))
            //crop
            val options = UCrop.Options()
            options.setCompressionQuality(80)
            options.setToolbarTitle(getString(R.string.app_name))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarWidgetColor(Color.WHITE)
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
            UCrop.of(imageUri!!, path)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(720,720)
                .withOptions(options)
                .start(this)

        }

        //menangkap hasil cropping dan update imageview
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            try {
                val inputStream = contentResolver.openInputStream(resultUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                updateImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Toast.makeText(this, cropError?.message, Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Membuat nama file
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Simpan path file di variabel global
            currentPhotoPath = absolutePath
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


