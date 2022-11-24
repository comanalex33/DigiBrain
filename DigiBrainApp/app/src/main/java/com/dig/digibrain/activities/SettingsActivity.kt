package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivitySettingsBinding
import com.dig.digibrain.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(binding.settingsContainer.id, SettingsFragment())
            .commit()

        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}