package com.example.android_lab04

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class GreetingActivity : AppCompatActivity() {
    lateinit var nameTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)
        val name = intent.getStringExtra("NAME")
        nameTextView = findViewById(R.id.nameTextView)
        nameTextView.setText("The name of the player is:\n $name")
    }
}