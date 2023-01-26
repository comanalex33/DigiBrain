package com.dig.digibrain.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityLoginBinding
import com.dig.digibrain.dialogs.LoadingDialog
import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LoginViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var animation: Animation
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSettings()
        setupViewModel()
        animation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)
        sessionManager = SessionManager(applicationContext)

        binding.loginButton.setOnClickListener {
            if (checkLoginFields()) {
                val loginModel = LoginModel(
                    username = binding.username.text.toString(),
                    password = binding.password.text.toString())
                setupObserver(loginModel)
            }
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        binding.usernameHolder.hint = resources.getString(R.string.username)
        binding.passwordHolder.hint = resources.getString(R.string.password)
        binding.loginButton.text = resources.getString(R.string.login)
        binding.noAccountText.text = resources.getString(R.string.don_t_have_an_account)
        binding.registerButton.text = resources.getString(R.string.create_account)
    }

    private fun setupSettings() {
        val darkMode = this.getSharedPreferences("application", Context.MODE_PRIVATE)
            .getBoolean("darkMode", false)
        if(darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val language = this.getSharedPreferences("application", Context.MODE_PRIVATE)
            .getString("language", null)
        language?.apply {
            val metrics = resources.displayMetrics
            val configuration = resources.configuration
            configuration.setLocale(Locale(language))
            resources.updateConfiguration(configuration, metrics)
            onConfigurationChanged(configuration)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService()))[LoginViewModel::class.java]
    }

    private fun setupObserver(loginModel: LoginModel) {
        changeEditTextStatus(usernameField = false, passwordField = false)
        val loadingDialog = LoadingDialog(this)
        viewModel.login(loginModel).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        binding.errorMessage.text = ""
                        changeEditTextStatus(usernameField = false, passwordField = false)

                        if(resource.data != null) {
                            val connectionStatus = sessionManager.setAuthToken(resource.data.token)
                            if(!connectionStatus) {
                                Toast.makeText(applicationContext, "Authentication could not be performed", Toast.LENGTH_SHORT).show()
                            } else {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        binding.errorMessage.text = resource.message
                        changeEditTextStatus(usernameField = true, passwordField = true)
                    }
                    Status.LOADING -> {
                        changeEditTextStatus(usernameField = false, passwordField = false)
                        binding.errorMessage.text = ""
                        loadingDialog.show()
                    }
                }
            }
        }
    }

    private fun checkLoginFields() : Boolean {
        changeEditTextStatus(usernameField = false, passwordField = false)

        var emptyFeilds = false
        if(binding.username.text.toString() == ""){
            changeEditTextStatus(usernameField = true)
            emptyFeilds = true
        }
        if(binding.password.text.toString() == "") {
            changeEditTextStatus(passwordField = true)
            emptyFeilds = true
        }
        return !emptyFeilds
    }

    private fun changeEditTextStatus(usernameField: Boolean? = null, passwordField: Boolean? = null) {
        usernameField?.let {
            if(it) {
                binding.username.setBackgroundResource(R.drawable.red_border)
                binding.usernameHolder.startAnimation(animation)
            } else {
                binding.username.setBackgroundResource(R.drawable.curve_shape)
                binding.username.backgroundTintList = AppCompatResources.getColorStateList(
                    applicationContext,
                    R.color.gray
                )
            }
        }
        passwordField?.let {
            if(it) {
                binding.password.setBackgroundResource(R.drawable.red_border)
                binding.passwordHolder.startAnimation(animation)
            } else {
                binding.username.setBackgroundResource(R.drawable.curve_shape)
                binding.username.backgroundTintList = AppCompatResources.getColorStateList(
                    applicationContext,
                    R.color.gray
                )
            }
        }
    }
}