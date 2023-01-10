package com.compare.compareapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compare.compareapp.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfile : AppCompatActivity() {

    private var binding : ActivityUserProfileBinding? = null
    val fireStoreDatabase = FirebaseFirestore.getInstance()

    private lateinit var tvName : TextView
    private lateinit var tvNomor : TextView
    private lateinit var tvGender : TextView
    private lateinit var tvEmail : TextView
    private lateinit var statuss : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        tvName = findViewById(R.id.txt_nama)
        tvNomor = findViewById(R.id.txt_nomor)
        tvGender = findViewById(R.id.txt_gender)
        tvEmail = findViewById(R.id.txt_email)
        statuss = findViewById(R.id.statuss)

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        fireStoreDatabase.collection("users").document(userId)
            .get()
            .addOnSuccessListener {
               val name = it.data?.get("name")?.toString()
                val nomor = it.data?.get("phone")?.toString()
                val gender = it.data?.get("gender")?.toString()
                val email = it.data?.get("email")?.toString()

                statuss.text = "data didapatkan"

                tvName.text = name
                tvNomor.text = nomor
                tvGender.text = gender
                tvEmail.text = email

                }
            .addOnFailureListener {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                statuss.text = "data tidak ditemukan"
            }
    }
}