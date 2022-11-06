package com.dig.digibrain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.dig.digibrain.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)

        binding.loginButton.setOnClickListener {
            binding.username.setBackgroundResource(R.color.gray)
            binding.password.setBackgroundResource(R.color.gray)
            when {
                binding.username.text.toString() == "" -> {
                    binding.username.setBackgroundResource(R.drawable.red_border)
                    binding.usernameHolder.startAnimation(animation)
                }
                binding.password.text.toString() == "" -> {
                    binding.password.setBackgroundResource(R.drawable.red_border)
                    binding.passwordHolder.startAnimation(animation)
                }
                else -> {
                    Toast.makeText(this, "Account logged in", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}