package com.compare.compareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.compare.compareapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class login : AppCompatActivity() {

    lateinit var binding :ActivityLoginBinding

    //inisalisasi firebase authentication
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //judul action bar
        supportActionBar?.setTitle("EaTrain-App Admin")

        //untuk mendapatkan instansi objek FirebaseAuth dalam Firebase Authentication
        auth = FirebaseAuth.getInstance()

        //button ganti password ditekan
        binding.GantiPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        //button login ditekan
        binding.btnLogin.setOnClickListener{
            val email = binding.edtEmailLogin.text.toString()
            val password = binding.edtPasswordLogin.text.toString()

            //Validasi Email
            if (email.isEmpty()) {
                binding.edtEmailLogin.error = "Email Harus Di isi"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            //Validasi Email Tidak Sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailLogin.error = "Email Tidak Valid"
                binding.edtEmailLogin.requestFocus()
                return@setOnClickListener
            }
            //Validasi password
            if (password.isEmpty()) {
                binding.edtPasswordLogin.error = "Password Harus Diisi"
                binding.edtPasswordLogin.requestFocus()
                return@setOnClickListener
            }
            //memanggil fungsi LoginFirebase
            LoginFirebase(email,password)
        }
    }

    //fungsi LoginFirebase
    private fun LoginFirebase(email: String, password: String) {
        //melakukan signIn menggunakan email dan password
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val db = FirebaseFirestore.getInstance()
                    //inisialisasi variabel cekdoc untuk collection profile
                    val cekDoc = db.collection("admin").document(uid!!).collection("Profil").document(uid)
                    cekDoc.get().addOnSuccessListener {
                        //seleksi kondisi nilai string email = email auth
                            if (it.getString("email") == auth.currentUser?.email){
                                val nama = it.getString("name")
                                Toast.makeText(this, "Selamat datang, $nama", Toast.LENGTH_SHORT).show()
                                //berpindah halaman pada mainActivity
                                val intent = Intent(this, MainActivity::class.java).also {
                                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(intent)
                            }
                            //jika email tidak sesuai
                            else
                            {
                                //keluar dari sesi otentikasi
                                auth.signOut()
                                Toast.makeText(this, "SILAHKAN LOGIN ULANG, DATA TIDAK ADA", Toast.LENGTH_SHORT).show()
                            }
                        }
                        //jika gagal melakukan pembacaan data
                            .addOnFailureListener(){
                            Toast.makeText(this, "GAGAL MEMBACA DATA. SILAHKAN LOGIN ULANG", Toast.LENGTH_SHORT).show()

                        }
                }
                //jika email atau password salah
                else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            val intent = Intent (this, MainActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }
}