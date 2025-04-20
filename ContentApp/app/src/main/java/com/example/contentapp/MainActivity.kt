package com.example.contentapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.common.shared.Content
import com.google.ai.client.generativeai.type.Content

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sample data for the content
        val contentList = listOf(
            Content("Article 1", "This is the description for Article 1"),
            Content("Article 2", "This is the description for Article 2"),
            Content("Article 3", "This is the description for Article 3")
        )

        // Set up the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.contentRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ContentAdapter(contentList)
    }

}
