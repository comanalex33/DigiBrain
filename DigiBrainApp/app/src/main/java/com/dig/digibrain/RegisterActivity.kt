package com.dig.digibrain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.dig.digibrain.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)

        binding.registerButton.setOnClickListener {
            binding.username.setBackgroundResource(R.color.gray)
            binding.email.setBackgroundResource(R.color.gray)
            binding.password.setBackgroundResource(R.color.gray)
            binding.password2.setBackgroundResource(R.color.gray)
            when {
                binding.username.text.toString() == "" -> {
                    binding.username.setBackgroundResource(R.drawable.red_border)
                    binding.usernameHolder.startAnimation(animation)
                }
                binding.email.text.toString() == "" -> {
                    binding.email.setBackgroundResource(R.drawable.red_border)
                    binding.emailHolder.startAnimation(animation)
                }
                binding.password.text.toString() == "" -> {
                    binding.password.setBackgroundResource(R.drawable.red_border)
                    binding.passwordHolder1.startAnimation(animation)
                }
                binding.password.text.toString() != binding.password2.text.toString() -> {
                    binding.password2.setBackgroundResource(R.drawable.red_border)
                    binding.passwordHolder2.startAnimation(animation)
                }
                else -> {
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}