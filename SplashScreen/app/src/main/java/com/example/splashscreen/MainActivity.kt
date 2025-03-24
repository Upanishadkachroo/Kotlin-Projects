package com.example.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        android.os.Handler().postDelayed({
            val intent=Intent(this, SignUp::class.java)
            startActivity(intent)
//            finish() //to get back to the previou screen
        }, 3000)
    }
}