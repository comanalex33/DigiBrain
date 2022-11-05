package com.dig.digibrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dig.digibrain.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            Toast.makeText(this, "Check login credentials", Toast.LENGTH_SHORT).show()
        }

        binding.registerButton.setOnClickListener {
            Toast.makeText(this, "Go to registration page", Toast.LENGTH_SHORT).show()
        }
    }
}