package com.dig.digibrain.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityMainBinding
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.SettingsViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)
        setupViewModel()
        updateLanguage()

        setupUI()
        handleActivity()
    }

    private fun updateLanguage() {
        viewModel.getLanguages()
            .observe(this) {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.apply {
                                val preferences = applicationContext.getSharedPreferences("application", Context.MODE_PRIVATE)
                                val language = preferences.getString("language", "ro")
                                if(language != null) {
                                    for(lang in resource.data) {
                                        if(lang.code == language) {
                                            preferences.edit().putLong("languageId", lang.id).apply()
                                            break
                                        }
                                    }
                                }
                            }
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[SettingsViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()

        val language = applicationContext.getSharedPreferences("application", Context.MODE_PRIVATE)
            .getString("language", null)

        language?.apply {
            val metrics = resources.displayMetrics
            val configuration = resources.configuration
            configuration.setLocale(Locale(language))
            resources.updateConfiguration(configuration, metrics)
            onConfigurationChanged(configuration)
        }

        updateLanguage()
    }

    private fun setupUI() {
        val username = sessionManager.getUserName()
        if(username != null) {
            "${resources.getString(R.string.hi)}, $username".also { binding.titleText.text = it }
        } else {
            resources.getString(R.string.hi).also { binding.titleText.text = it }
        }
    }

    private fun handleActivity() {
        binding.logoutButton.setOnClickListener {
            sessionManager.disconnect()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.learnButton.setOnClickListener {
            val intent = Intent(this, LearnActivity::class.java)
            startActivity(intent)
        }

        binding.quizButton.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        binding.learningPathButton.setOnClickListener {
            val intent = Intent(this, LearnPathActivity::class.java)
            startActivity(intent)
        }

        binding.statsButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        binding.learnText.text = resources.getString(R.string.learn)
        binding.quizText.text = resources.getString(R.string.quiz)
        binding.profileText.text = resources.getString(R.string.profile)
        binding.statsText.text = resources.getString(R.string.statistics)
        binding.settingsText.text = resources.getString(R.string.settings)
        setupUI()
    }
}