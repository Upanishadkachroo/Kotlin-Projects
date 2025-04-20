package com.example.interactiveapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btndark=findViewById<Button>(R.id.butndark)
        val btnlight=findViewById<Button>(R.id.butnlight)
        val layout=findViewById<Button>(R.id.linearlayout)

        btndark.setOnClickListener{
            //change to light mode
            layout.setBackgroundResource(R.color.black)
        }

        btnlight.setOnClickListener{
            //change to dark mode
            layout.setBackgroundResource(R.color.yellow)
        }
    }
}