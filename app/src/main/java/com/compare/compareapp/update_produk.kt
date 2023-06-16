package com.compare.compareapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.compare.compareapp.databinding.FragmentUpdateProdukBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_update_produk.*
import java.util.*
import kotlin.collections.ArrayList

class update_produk : Fragment() {

    private lateinit var binding: FragmentUpdateProdukBinding
    private lateinit var myAdapter : MyAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdateProdukBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //mengatur recyclerView menggunakan liner layout
        binding.recyclerView.apply {
           layoutManager = LinearLayoutManager(context)
        }
        //memanggil fungsi fetchData
        fetchData()

        //inisialisasi button floating
        val floatingfab1 = view.findViewById<FloatingActionButton>(R.id.btn_fab)

        //jika button floatingfab ditekan, pindah activity
        floatingfab1.setOnClickListener {
            val intent = Intent(requireContext(), uploadActivity::class.java)
            startActivity(intent)
        }

    }

    //fungsi fetchData
    private fun fetchData() {
        //mendapatkan objek firebasefirestore untuk mengakses koleksi “Menu”
        FirebaseFirestore.getInstance().collection("Menu")
            //mendapatkan data dari koleksi “Menu”
            .get()
            //jika sukses menampilkannya dalam RecyclerView
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val menu = documents.toObjects(Menu::class.java)
                    myAdapter = context?.let { MyAdapter (it, menu) }!!
                    binding.recyclerView.adapter = myAdapter

                    //searchView
                    binding.searchView.setOnQueryTextListener(object:androidx.appcompat.widget.SearchView.OnQueryTextListener{
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            myAdapter.filter.filter(query)
                            return false
                        }

                    })
                }
            }
            .addOnFailureListener {

            }
    }

}
