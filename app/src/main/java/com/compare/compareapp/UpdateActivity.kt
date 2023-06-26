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

        //judul action bar
        supportActionBar?.setTitle("EaTrain-App Admin")

        //cek permission upload gambar
        //mengaktifkan tombol id UploadImage (ImageView) untuk dapat di klik
        updateImage.isEnabled = true

        //melakukan pemeriksaan izin untuk kamera apakah telah diaktifkan (diberikan)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
                //jika izin kamera belum diberikan
            )!= PackageManager.PERMISSION_GRANTED
        ){
            //maka meminta izin kamera dengan menggunakan "requestPermissions"
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }else{
            //jika izin telah diberikan, maka tidak perlu meminta izin.  dan tombol UploadImage dapat di klik untuk upload gambar
            updateImage.isEnabled = true
        }

        //jika id updateImage di klik, menjalankan fungsi selectImage
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

        //jika id buttonUpdate di klik, menjalankan fungsi uploadData
        buttonUpdate.setOnClickListener {
            uploadData()
        }

    }

    private fun selectImage() {
        //membuat array "items" dengan 3 pilihan
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        //membuat variabel "builder"
        val builder = android.app.AlertDialog.Builder(this)
        //dengan judul "EaTrain Admin"
        builder.setTitle(getString(R.string.app_name))
        //dan ikon aplikasi
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            //jika memilih opsi "Take Photo"
            if (items[item] == "Take Photo") {
                // Ambil gambar menggunakan kamera
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Pastikan ada aplikasi kamera yang dapat menangani intent ini
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        // Buat file gambar sementara untuk menyimpan hasil kamera
                        val photoFile: File? = try {
                            //membuat file gambar dengan fungsi createImageFile untuk penyimpanan gambar yang di capture
                            createImageFile()
                        } catch (ex: IOException) {
                            // Error saat membuat file
                            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT)
                                .show()
                            null
                        }
                        // Jika file berhasil dibuat, lanjutkan mengambil gambar dari kamera
                        photoFile?.also {
                            //memperoleh uri file yang akan digunakan untuk menyimpan hasil foto
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this, "com.compare.compareapp.fileprovider", it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, 10)
                        }
                    }
                }
            }
            //mengambil gambar dari galeri
            else if (items[item] == "Choose from Library") {
                //membuat variabel intent untuk memilih gambar dari galeri
                val intent = Intent(Intent.ACTION_PICK)
                //mengatur tipe intent, untuk membatasi bahwa yang dipilih hanya pada tipe gambar
                intent.type = "image/*"
                //memulai aktivitas selectImage dengan intent dan kode permintaan 20
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 20)
            //jika opsi yang dipilih cancel
            } else if (items[item] == "Cancel") {
                //dialog akan ditutup
                dialog.dismiss()
            }
        }
        builder.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //galeri
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            val path : Uri? = data.data
            //crop
            //  path?.let { startCrop(it) }
            //Membuat URI tujuan untuk menyimpan gambar hasil cropping
            val destinationUri = Uri.fromFile(File(cacheDir, "IMG_" + System.currentTimeMillis()))
            // Mengatur opsi-opsi untuk fitur cropping
            val options = UCrop.Options()
            options.setCompressionQuality(80)
            options.setToolbarTitle(getString(R.string.app_name))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarWidgetColor(Color.WHITE)
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
            //memulai aktivitas crop
            UCrop.of(path!!, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(720,720)
                .withOptions(options)
                .start(this)
        }

        //kamera
        if (requestCode == 10 && resultCode == RESULT_OK) {
            val imageUri = Uri.fromFile(File(currentPhotoPath))
            //Membuat URI tujuan untuk menyimpan gambar hasil cropping
            val path = Uri.fromFile(File(cacheDir, "IMG_" + System.currentTimeMillis()))
            //crop
            // Mengatur opsi-opsi untuk fitur cropping
            val options = UCrop.Options()
            options.setCompressionQuality(80)
            options.setToolbarTitle(getString(R.string.app_name))
            options.setStatusBarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setToolbarWidgetColor(Color.WHITE)
            options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.purple_700))
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
            //memulai aktivitas crop
            UCrop.of(imageUri!!, path)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(720,720)
                .withOptions(options)
                .start(this)

        }

        //menangkap hasil cropping dan update imageview
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //mendapatkan URI hasil cropping menggunakan “UCrop.getOutput(data!!)”
            val resultUri = UCrop.getOutput(data!!)
            try {
                //jika berhasil, membuka input stream dari URI hasil cropping dan mengonversinya menjadi objek bitmap
                val inputStream = contentResolver.openInputStream(resultUri!!)
                // Bitmap diatur sebagai gambar di ImageView
                val bitmap = BitmapFactory.decodeStream(inputStream)
                updateImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            //jika proses crop error mendapatkan pesan eror melalui toast
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
        //mengambil nilai teks dari input editText dan menghapus spasi di awal dan akhir string
        val edMenu = updateeJudul.text.toString().trim()
        val edHarga = updateeHarga.text.toString().trim()
        val edDesc = updateeDesc.text.toString().trim()

        updateImage.isDrawingCacheEnabled = true
        updateImage.buildDrawingCache()
        //Mengambil gambar dari ImageView updateImage dan dikonversi menjadi objek Bitmap
        val bitmap = (updateImage.drawable as BitmapDrawable).bitmap
        //Membuat objek untuk menampung data gambar yang diupload
        val baos = ByteArrayOutputStream()
        //Mengompresi gambar menjadi format JPEG dengan kualitas 100 dan menyimpannya dalam objek ByteArrayOutputStream.
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        //Mengambil data gambar yang sudah dikompresi dari objek ByteArrayOutputStream dan mengonversinya menjadi array byte.
        val data = baos.toByteArray()

        //UPLOAD
        //membuat progress dialoag (ikon loading)
        val builder = AlertDialog.Builder(this)
        //Mengatur dialog agar tidak dapat dibatalkan dengan menekan tombol back.
        builder.setCancelable(false)
        //Mengatur tampilan layout progres sebagai tampilan dialog.
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        //menampilkan dialog
        dialog.show()

//        val currentDate : String = DateFormat.getDateTimeInstance().format(Calendar.getInstance().time)
        //mendapatkan instance FirebaseStorage
        val storage = FirebaseStorage.getInstance()
        //inisialisasi untuk menyimpan gambar ke folder "images" dengan nama file yg telah ditentukan
        val reference = storage.getReference("images").child("IMG"+ Date().time +".jpeg")
        //mengunggah gambar ke firebaseStorage
        var uploadTask = reference.putBytes(data)
        //jika gagal saat mengunggah gambar ke firebaseStorage
        uploadTask.addOnFailureListener {
            //menampilkan toast failed
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
        //jika sukses
        }.addOnSuccessListener { taskSnapshot ->
            //Mengecek apakah metadata dari taskSnapshot tidak null.
            if(taskSnapshot.metadata !=null){
                //jika referensi metadata dari taskSnapshot tidak null
                if(taskSnapshot.metadata!!.reference !=null){
                    //mengambil URL unduhan file yang diunggah ke Firebase Storage.
                    //jika telah complete
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                        //Menghapus file gambar yang ada di Firebase Storage
                        FirebaseStorage.getInstance().getReferenceFromUrl(imageURL).delete()
                        val bundle = intent.extras
                        //inisialisasi variabel docID untuk mendapatkan docID pada intent sebelumnya
                        val docID = bundle!!.getString("docID").toString().trim()
                        //Mengambil URL hasil unduhan file dari Firebase Storage dan dikonversi menjadi string
                        var editfoto = it.getResult().toString()
                        val dbupdate = FirebaseFirestore.getInstance()
                        //membuat objek yang berisi data-data menu yang akan diperbaharui
                        val updateMenu = hashMapOf<String, Any>(
                            "namaMenu" to edMenu,
                            "Harga" to edHarga,
                            "Foto" to editfoto,
                            "Desc" to edDesc,
                        )
                        //melakukan update pada dokumen dengan id "docID" dari koleksi menu
                        dbupdate.collection("Menu").document("$docID").update(updateMenu)
                            //jika berhasil
                            .addOnSuccessListener { documentReference ->
                                //menampilkan toast sukses dan berpindah halaman ke mainActivity
                                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            //jika gagal
                            .addOnFailureListener { exception ->
                                dialog.dismiss()
                                //menampilkan pesan failed
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


