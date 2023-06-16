package com.compare.compareapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.compare.compareapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //mengambil nilai “direct” dari intent
        val bundle = intent.getStringExtra("direct")
        //jika bernilai “true”
        if (bundle == "true")
        {
            //penggantian fragment dengan fragment riwayat
            replaceFragment(riwayat())
        }
        //jika salah penggantian dengan fragment update produk
        else replaceFragment(update_produk())

        //judul action bar
        supportActionBar?.setTitle("EaTrain-App Admin")

        //pemilihan item pada bottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.update_produk -> replaceFragment(update_produk())
                R.id.riwayat -> replaceFragment(riwayat())
                R.id.profile -> replaceFragment(user_admin())
                else ->{

                }
            }

            true
        }

    }

    //fungsi replace fragment pada fragment riwayat
    //menggantikan fragment saat ini dengan fragment baru (riwayat)
    private fun replaceFragment(fragment: riwayat){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
    //fungsi replace fragment pada fragment update_produk
    private fun replaceFragment(fragment: update_produk) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
    //fungsi replace fragment pada fragment profile admin
    private fun replaceFragment(fragment: user_admin) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}