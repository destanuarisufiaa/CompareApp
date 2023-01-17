package com.compare.compareapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.compare.compareapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(update_produk())

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

    private fun replaceFragment(fragment: riwayat){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
    private fun replaceFragment(fragment: update_produk) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    private fun replaceFragment(fragment: user_admin) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}