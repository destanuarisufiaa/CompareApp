package com.compare.compareapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.compare.compareapp.databinding.ActivityDetailBinding
import com.compare.compareapp.databinding.ActivityUploadBinding

class DetailActivity : AppCompatActivity() {

    var imageURL = ""
    private lateinit var binding:ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle !=null){
            binding.detailTittle.text = bundle.getString("namaMenu")
            binding.detailHarga.text = bundle.getString("Harga")
            imageURL = bundle.getString("Image")!!
            Glide.with(this).load(bundle.getString("Image")).into(binding.detailImage)
        }
    }
}