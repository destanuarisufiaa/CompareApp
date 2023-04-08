package com.compare.compareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.compare.compareapp.databinding.ActivityDetailPesananBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_pesanan.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class detailPesanan : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPesananBinding
    private lateinit var status : RadioGroup
    private lateinit var hasilStatus : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewItemRiwayat.apply {
            layoutManager = LinearLayoutManager(context)
        }

        val status = intent.getStringExtra("status")
        if (status.equals("PERSIAPAN")){
            binding.rgStatusPesanan.check(R.id.persiapan)
        }else if (status.equals("ANTAR")){
            binding.rgStatusPesanan.check(R.id.antar)
        }else if (status.equals("SELESAI")){
            binding.rgStatusPesanan.check(R.id.persiapan)
        }

        fetchDataPesanan()

        btn_updateStatus.setOnClickListener {
            statusPesanan()
        }
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

    private fun statusPesanan() {
        val cekGenderRadioButtonId = rg_statusPesanan.checkedRadioButtonId
        val listStatus = findViewById<RadioButton>(cekGenderRadioButtonId)
        hasilStatus = "${listStatus.text}"

        val edStatus = hasilStatus.trim()

        val dbUpdatePesanan = FirebaseFirestore.getInstance()
        val bahanStatus = hashMapOf<String, Any>(
            "status" to edStatus,
        )
        val orderID = intent.getStringExtra("orderID").toString()
        dbUpdatePesanan.collection("pesanan").document(orderID).update(bahanStatus)
            .addOnSuccessListener{ documentReference ->
                Toast.makeText(this, "Sukses Perubahan Status", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("direct", "true")
                startActivity(intent)
            }.addOnFailureListener {
                Toast.makeText(this, "Failed!, gagal", Toast.LENGTH_SHORT).show()
            }

    }

}