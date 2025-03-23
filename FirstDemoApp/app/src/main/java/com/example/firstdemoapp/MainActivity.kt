package com.example.firstdemoapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnupload=findViewById<Button>(R.id.butnupload)
        val btndownload=findViewById<Button>(R.id.butndownload)

        btnupload.setOnClickListener{
            Toast.makeText(applicationContext, "Uploading..", Toast.LENGTH_SHORT).show()
        }

        btndownload.setOnClickListener{
            Toast.makeText(applicationContext, "Downloading", Toast.LENGTH_SHORT).show()
        }
    }
}