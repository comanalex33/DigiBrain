package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityRegisterBinding
import com.dig.digibrain.dialogs.LoadingDialog
import com.dig.digibrain.models.RegisterModel
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.RegisterViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var animation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        animation = AnimationUtils.loadAnimation(this, R.anim.shake_animation)

        binding.registerButton.setOnClickListener {
            if(checkRegisterFields()) {
                val registerModel = RegisterModel(
                    email = binding.email.text.toString(),
                    username = binding.username.text.toString(),
                    password = binding.password.text.toString())
                setupObserver(registerModel)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[RegisterViewModel::class.java]
    }

    private fun setupObserver(registerModel: RegisterModel) {
        changeEditTextStatus(usernameField = false, passwordField = false, emailField = false)
        val loadingDialog = LoadingDialog(this)
        viewModel.register(registerModel).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        loadingDialog.dismiss()
                        binding.errorMessage.text = ""
                        changeEditTextStatus(
                            usernameField = false,
                            passwordField = false,
                            emailField = false
                        )
                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
                    }
                    Status.ERROR -> {
                        loadingDialog.dismiss()
                        binding.errorMessage.text = resource.message
                        Toast.makeText(applicationContext, "${registerModel.username} ${resource.invalidFields.toString()}", Toast.LENGTH_SHORT).show()
                        if(resource.invalidFields != null) {
                            for(invalidField in resource.invalidFields) {
                                if(invalidField.uppercase() == "USERNAME") {
                                    changeEditTextStatus(usernameField = true)
                                }
                                if(invalidField.uppercase() == "PASSWORD") {
                                    changeEditTextStatus(passwordField = true)
                                }
                                if(invalidField.uppercase() == "EMAIL") {
                                    changeEditTextStatus(emailField = true)
                                }
                            }
                        } else {
                            changeEditTextStatus(
                                usernameField = true,
                                passwordField = true,
                                emailField = true
                            )
                        }
                    }
                    Status.LOADING -> {
                        changeEditTextStatus(
                            usernameField = false,
                            passwordField = false,
                            emailField = false
                        )
                        binding.errorMessage.text = ""
                        loadingDialog.show()
                    }
                }
            }
        }
    }

    private fun checkRegisterFields() : Boolean {
        changeEditTextStatus(usernameField = false, passwordField = false, emailField = false)
        binding.errorMessage.text = ""

        var emptyFeilds = false
        if(binding.username.text.toString() == ""){
            changeEditTextStatus(usernameField = true)
            emptyFeilds = true
        }
        if(binding.password.text.toString() == "") {
            changeEditTextStatus(passwordField = true)
            emptyFeilds = true
        }
        if(binding.email.text.toString() == "") {
            changeEditTextStatus(emailField = true)
            emptyFeilds = true
        }
        if(!emptyFeilds) {
            if(binding.password.text.toString() != binding.password2.text.toString()) {
                binding.password.setBackgroundResource(R.drawable.red_border)
                binding.passwordHolder1.startAnimation(animation)
                binding.password2.setBackgroundResource(R.drawable.red_border)
                binding.passwordHolder2.startAnimation(animation)
                binding.errorMessage.text = "Passwords doesn't match"
                return false
            }
            return true
        }
        return false
    }

    private fun changeEditTextStatus(
        usernameField: Boolean? = null,
        passwordField: Boolean? = null,
        emailField: Boolean? = null) {

        usernameField?.let {
            if(it) {
                binding.username.setBackgroundResource(R.drawable.red_border)
                binding.usernameHolder.startAnimation(animation)
            } else {
                binding.username.setBackgroundResource(R.color.gray)
            }
        }

        emailField?.let {
            if(it) {
                binding.email.setBackgroundResource(R.drawable.red_border)
                binding.emailHolder.startAnimation(animation)
            } else {
                binding.email.setBackgroundResource(R.color.gray)
            }
        }
        passwordField?.let {
            if(it) {
                binding.password.setBackgroundResource(R.drawable.red_border)
                binding.passwordHolder1.startAnimation(animation)
                binding.password2.setBackgroundResource(R.drawable.red_border)
                binding.passwordHolder2.startAnimation(animation)
            } else {
                binding.password.setBackgroundResource(R.color.gray)
                binding.password2.setBackgroundResource(R.color.gray)
            }
        }
    }
}