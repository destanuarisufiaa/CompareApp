package com.compare.compareapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class user_admin : Fragment() {

    private val db = Firebase.firestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_admin, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        firebaseAuth = FirebaseAuth.getInstance()
        val userid = firebaseAuth.currentUser?.uid

        val ShowName = view.findViewById<TextView>(R.id.txt_namaAdmin)
        val ShowPhone = view.findViewById<TextView>(R.id.txt_nomorAdmin)
        val ShowGender = view.findViewById<TextView>(R.id.txt_genderAdmin)
        val ShowEmail = view.findViewById<TextView>(R.id.txt_emailAdmin)
        val statuss = view.findViewById<TextView>(R.id.statussAdmin)

        val docRef = db.collection("users").document(userid!!)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
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
    }


}