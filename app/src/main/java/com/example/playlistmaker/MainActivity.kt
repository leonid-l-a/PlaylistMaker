package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button2.setOnClickListener {
            startActivity(Intent(this, LibraryActivity::class.java))
        }

        button1.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        button3.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}