package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dig.digibrain.databinding.ActivityProfileBinding
import com.dig.digibrain.databinding.ActivitySettingsBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}