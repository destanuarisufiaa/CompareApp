package com.compare.compareapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.compare.compareapp.databinding.FragmentRiwayatBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Locale.filter

class riwayat : Fragment() {

    private lateinit var binding: FragmentRiwayatBinding
    private lateinit var riwayatadapter : RiwayatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRiwayatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewPesanan.apply {
            layoutManager = LinearLayoutManager(context)

        }
        riwayatPesanan()

    }

    private fun riwayatPesanan() {
        val listPesananRiwayat = FirebaseFirestore.getInstance().collection("pesanan")
        listPesananRiwayat.whereNotEqualTo("status", "SELESAI").get()
            .addOnSuccessListener { documents ->
                for (document in documents)
                {
                    val riwayat = documents.toObjects(dataRiwayat::class.java)
                    riwayatadapter = context?.let { RiwayatAdapter(it, riwayat) }!!
                    binding.recyclerViewPesanan.adapter = riwayatadapter

                    binding.searchViewRiwayat.setOnQueryTextListener(object:androidx.appcompat.widget.SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            riwayatadapter.filter.filter(query)
                            return false
                        }

                    })

                }

            }

        }
    }

