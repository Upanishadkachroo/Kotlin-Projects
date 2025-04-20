package com.example.contentprovidingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var contentViewModel: ContentViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var contentAdapter: ContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contentViewModel = ViewModelProvider(this).get(ContentViewModel::class.java)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        contentViewModel.contentList.observe(this, Observer { contentList ->
            contentAdapter = ContentAdapter(contentList)
            recyclerView.adapter = contentAdapter
        })

        contentViewModel.loadContent()
    }
}
