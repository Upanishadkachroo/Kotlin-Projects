package com.example.explicitintent

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnintent2=findViewById<Button>(R.id.button2)


        btnintent2.setOnClickListener{
            intent= Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

}