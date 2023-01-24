package com.compare.compareapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.compare.compareapp.databinding.FragmentUpdateProdukBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_update_produk.*
import java.util.*
import kotlin.collections.ArrayList

class update_produk : Fragment() {

    private lateinit var binding: FragmentUpdateProdukBinding
    private lateinit var MenuList : MutableList<Menu>
    private lateinit var adapter : MyAdapter
    private lateinit var searchView: SearchView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdateProdukBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.apply {
           layoutManager = LinearLayoutManager(context)

        }

        fetchData()

        val floatingfab1 = view.findViewById<FloatingActionButton>(R.id.btn_fab)

        floatingfab1.setOnClickListener {
            val intent = Intent(requireContext(), uploadActivity::class.java)
            startActivity(intent)
        }

    }

    private fun fetchData() {
        FirebaseFirestore.getInstance().collection("Menu")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val menu = documents.toObjects(Menu::class.java)
                    binding.recyclerView.adapter = context?.let { MyAdapter (it, menu) }
                }
            }
            .addOnFailureListener {

            }
    }

}
