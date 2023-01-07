package com.compare.compareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.compare.compareapp.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
//    lateinit var binding: ActivityMainBinding
private lateinit var tombol : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        tombol = findViewById(R.id.tv_to_btn)
        tombol.setOnClickListener{
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }

    }
}