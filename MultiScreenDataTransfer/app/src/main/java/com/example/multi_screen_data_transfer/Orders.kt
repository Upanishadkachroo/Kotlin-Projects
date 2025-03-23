package com.example.multi_screen_data_transfer

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Orders : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val OrderOfCustomers=intent.getStringExtra(MainActivity.KEY)

        val t1=findViewById<TextView>(R.id.textView1)

        t1.text="Orders placed are" + OrderOfCustomers.toString()

    }
}