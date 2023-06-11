package com.dig.digibrain.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivitySettingsBinding
import com.dig.digibrain.fragments.SettingsFragment
import com.dig.digibrain.services.SessionManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)

        supportFragmentManager
            .beginTransaction()
            .replace(binding.settingsContainer.id, SettingsFragment())
            .commit()

        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    fun isBindingSet(): Boolean {
        return this::binding.isInitialized
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        binding.settingsTitle.text = resources.getString(R.string.settings)
    }
}