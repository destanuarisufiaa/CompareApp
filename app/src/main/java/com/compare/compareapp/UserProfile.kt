package com.compare.compareapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserProfile : AppCompatActivity() {

    private lateinit var tvName : TextView
    private lateinit var tvNomor : TextView
    private lateinit var tvGender : TextView
    private lateinit var tvEmail : TextView
    private lateinit var statuss : TextView

    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        tvName = findViewById(R.id.txt_nama)
        tvNomor = findViewById(R.id.txt_nomor)
        tvGender = findViewById(R.id.txt_gender)
        tvEmail = findViewById(R.id.txt_email)
        statuss = findViewById(R.id.statuss)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = db.collection("users").document(userId.toString())
        ref.get().addOnSuccessListener {
            if (it != null) {
                var nama = it.data?.get("name").toString()
                var phone = it.data?.get("phone").toString()
                var gender = it.data?.get("gender").toString()
                var email = it.data?.get("email").toString()

                tvName.setText(nama)
                tvNomor.setText(phone)
                tvGender.text = gender
                tvEmail.text = email
                statuss.text = "data didapatkan"

            }
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                statuss.text = "data tidak ditemukan"
            }
    }
}