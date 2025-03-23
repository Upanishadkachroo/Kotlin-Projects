package com.example.multi_screen_data_transfer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    //creation key
    companion object{
        const val KEY="com.example.multi_screen_data_transfer.KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnorder=findViewById<Button>(R.id.button1)

        val Text1 = findViewById<EditText>(R.id.Text1)
        val Text2 = findViewById<EditText>(R.id.Text2)
        val Text3 = findViewById<EditText>(R.id.Text3)
        val Text4 = findViewById<EditText>(R.id.Text4)

        btnorder.setOnClickListener{
            val message=Text1.text.toString() + " " + Text2.text.toString() + " " + Text3.text.toString() + " " + Text4.text.toString()

            intent= Intent(this, Orders::class.java)
            intent.putExtra(KEY, message)
            startActivity(intent)
        }
    }
}