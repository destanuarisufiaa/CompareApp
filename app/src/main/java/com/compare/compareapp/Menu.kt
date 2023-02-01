package com.compare.compareapp

import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.firestore.DocumentId

data class Menu(

    val namaMenu: String? = null,
    val Harga: String? = null,
    val Desc: String? = null,
    val Foto: String? = null,
    @DocumentId
    val docID: String = "",


//    @DocumentId
//    val docuID:DocumentId,
//    val docID:String? = docuID.toString()
)


