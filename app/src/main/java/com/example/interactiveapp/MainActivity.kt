package com.example.interactiveapp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val drkbtn = findViewById<Button>(R.id.btnDark)
        val readbtn = findViewById<Button>(R.id.btnRead)
        val main = findViewById<LinearLayout>(R.id.main)

        drkbtn.setOnClickListener {
            main.setBackgroundResource(R.color.black)
        }

        readbtn.setOnClickListener {
            main.setBackgroundResource(R.color.ivory)
        }
    }
}