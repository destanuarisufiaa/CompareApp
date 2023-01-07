package com.compare.compareapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.compare.compareapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class register : AppCompatActivity() {

    lateinit var binding :ActivityRegisterBinding
    lateinit var auth : FirebaseAuth
    lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gender = findViewById<TextView>(R.id.txt_gender_register)
        val gender1 = findViewById<RadioGroup>(R.id.rg_gender1_register)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.tvToLogin.setOnClickListener{
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            val nama = binding.edtNamaRegister.text.toString()
            val phone = binding.edtNomorhpRegister.text.toString()

            val cekGenderRadioButtonId = gender1.checkedRadioButtonId
            val listGender = findViewById<RadioButton>(cekGenderRadioButtonId)

            val hasilGender = "${listGender.text}"
            gender.text = hasilGender


            //Validasi Email
            if (email.isEmpty()) {
                binding.edtEmailRegister.error = "Email Harus Di isi"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            //Validasi Email Tidak Sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailRegister.error = "Email Tidak Valid"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            //Validasi password
            if (password.isEmpty()) {
                binding.edtPasswordRegister.error = "Password Harus Diisi"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            //Validasi panjang password
            if (password.length < 6) {
                binding.edtPasswordRegister.error = "Password Minimal 6 Karakter"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            registerFirebase(email,password, nama, phone, hasilGender)
        }


    }

    private fun registerFirebase(email: String, password: String, nama: String, phone: String, hasilGender: String) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                    val hashMap = hashMapOf<String, Any>(
                        "email" to email,
                        "name" to nama,
                        "phone" to phone,
                        "gender" to hasilGender,
                    )
                    firestore.collection("users")
                        .add(hashMap)
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error adding document $exception")
                        }

                    val intent = Intent (this, login::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }

    }
}