package com.compare.compareapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.compare.compareapp.databinding.ActivityDetailPesananBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_pesanan.*
import com.google.firebase.firestore.ktx.toObject


class detailPesanan : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPesananBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewItemRiwayat.apply {
            layoutManager = LinearLayoutManager(context)
        }

        fetchDataPesanan()
    }

    private fun fetchDataPesanan() {

        val orderID = intent.getStringExtra("orderID").toString()
        val listRiwayatPesanan = mutableListOf<itemDataRiwayat>()
        val riwayatPesanan = FirebaseFirestore.getInstance().collection("pesanan").document("$orderID").collection("menu")
        riwayatPesanan.get()
            .addOnSuccessListener { documents ->
                for (document in documents)
                {

                    if (document.id != "total")
                    {
                        val pesananRiwayat = document.toObject(itemDataRiwayat::class.java)
                        listRiwayatPesanan.add(pesananRiwayat)
                    }
                    binding.recyclerViewItemRiwayat.adapter = itemRiwayatAdapter (this,listRiwayatPesanan)
                }
            }
    riwayatPesanan.document("total").get()
        .addOnSuccessListener {
            tv_totalForm.text = it.getString("total")
        }
    }
}