package com.compare.compareapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.compare.compareapp.databinding.ActivityUserProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfile : AppCompatActivity() {

    private lateinit var ShowName : TextView
    private lateinit var ShowPhone : TextView
    private lateinit var ShowGender : TextView
    private lateinit var ShowEmail : TextView
    private lateinit var statuss : TextView

    private val db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        val userid = firebaseAuth.currentUser?.uid

        ShowName = findViewById(R.id.txt_nama)
        ShowPhone = findViewById(R.id.txt_nomor)
        ShowGender = findViewById(R.id.txt_gender)
        ShowEmail = findViewById(R.id.txt_email)
        statuss = findViewById(R.id.statuss)

        val docRef = db.collection("users").document(userid!!);
        docRef.get()
            .addOnSuccessListener { document ->
                if (document!=null) {
                    val name = document.getString("name")
                    val phone = document.getString("phone")
                    val gender = document.getString("gender")
                    val email = document.getString("email")

                    ShowName.text = "nama = $name"
                    ShowPhone.text = "phone = $phone"
                    ShowGender.text = "gender = $gender"
                    ShowEmail.text = "email = $email"
                    statuss.text = "Succes dapat data user $userid"
                }
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                statuss.text = "gagal membaca data user $userid"
            }


    }
}