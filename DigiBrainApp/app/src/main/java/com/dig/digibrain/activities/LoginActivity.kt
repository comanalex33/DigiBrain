package com.dig.digibrain.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityLoginBinding
import com.dig.digibrain.dialogs.LoadingDialog
import com.dig.digibrain.models.LoginModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.notifications.UserActivityService
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LoginViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.google.firebase.messaging.FirebaseMessaging
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
                    password = binding.password.text.toString()
                )
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
        if (darkMode) {
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
            ViewModelFactory(ApiClient.getService())
        )[LoginViewModel::class.java]
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

                        if (resource.data != null) {
                            val previousUsername = sessionManager.getUserName()

                            val connectionStatus = sessionManager.setAuthToken(resource.data.token)
                            if (!connectionStatus) {
                                Toast.makeText(
                                    applicationContext,
                                    "Authentication could not be performed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val actualUsername = sessionManager.getUserName()
                                if(previousUsername != null && actualUsername != null && previousUsername != actualUsername) {
                                    unsubscribeSubjects()
                                }
                                getObjectStorageInfo()
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

    private fun getObjectStorageInfo() {
        val authToken: String? = sessionManager.getBearerAuthToken()
        var role = sessionManager.getUserRole()
        val username = sessionManager.getUserName()

        if (authToken != null && role != null && username != null) {
            viewModel.getObjectStorageInfo(authToken).observe(this) {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (resource.data != null) {
                                sessionManager.setObjectStorageInfo(resource.data)
                                setUserLastLoginAndAlarm()

                                val subscriptions = listOf(
                                    "all",
                                    role,
                                    "user-${username}"
                                )
                                updateSubscriptions(subscriptions)

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                        Status.ERROR -> {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        Status.LOADING -> {}
                    }
                }
            }
        }
    }

    private fun updateSubscriptions(newSubscriptions: List<String>) {
        val oldSubscriptionsString = this.getSharedPreferences("application", Context.MODE_PRIVATE)
            .getString("subscriptions", "")

        val oldSubscriptions = if(oldSubscriptionsString != null && oldSubscriptionsString != "")
            oldSubscriptionsString.split(",")
        else
            listOf()

        // Subscribe to new topics and update preference
        for(subscription in newSubscriptions) {
            if(!oldSubscriptions.contains(subscription)) {
                FirebaseMessaging.getInstance().subscribeToTopic(subscription)
            }
        }
        this.getSharedPreferences("application", Context.MODE_PRIVATE).edit()
            .putString("subscriptions", TextUtils.join(",", newSubscriptions)).apply()

        // Unsubscribe from unused topics
        for(subscription in oldSubscriptions) {
            if(!newSubscriptions.contains(subscription)) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(subscription)
            }
        }
    }

    private fun unsubscribeSubjects() {
        val subjectSubscriptionsString = this.getSharedPreferences("application", Context.MODE_PRIVATE)
            .getString("subjects", "")
        val subjectSubscriptions = if(subjectSubscriptionsString != null && subjectSubscriptionsString != "")
            subjectSubscriptionsString.split(",")
        else
            listOf()

        for(subscription in subjectSubscriptions) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(subscription)
        }

        this.getSharedPreferences("application", Context.MODE_PRIVATE).edit()
            .putString("subjects", "").apply()
    }

    private fun setUserLastLoginAndAlarm() {
        // Set user last login time
        this.getSharedPreferences("application", Context.MODE_PRIVATE).
            edit().putLong("lastLoginTime", System.currentTimeMillis()).apply()

        Log.v("LoginActivity", "Starting CheckRecentRun service...")
        startService(Intent(this, UserActivityService::class.java))
    }

    private fun checkLoginFields(): Boolean {
        changeEditTextStatus(usernameField = false, passwordField = false)

        var emptyFeilds = false
        if (binding.username.text.toString() == "") {
            changeEditTextStatus(usernameField = true)
            emptyFeilds = true
        }
        if (binding.password.text.toString() == "") {
            changeEditTextStatus(passwordField = true)
            emptyFeilds = true
        }
        return !emptyFeilds
    }

    private fun changeEditTextStatus(
        usernameField: Boolean? = null,
        passwordField: Boolean? = null
    ) {
        usernameField?.let {
            if (it) {
                binding.usernameHolder.startAnimation(animation)
            }
        }
        passwordField?.let {
            if (it) {
                binding.passwordHolder.startAnimation(animation)
            }
        }
    }
}