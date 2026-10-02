package com.keyboardx.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Main launcher activity for Keyboard X application.
 * Displays basic UI and settings for keyboard activation.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}