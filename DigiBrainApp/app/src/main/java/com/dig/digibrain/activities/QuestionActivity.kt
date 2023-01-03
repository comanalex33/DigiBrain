package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityQuestionBinding
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.viewModels.LearnViewModel
import com.dig.digibrain.viewModels.QuestionViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class QuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBinding
    private lateinit var viewModel: QuestionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[QuestionViewModel::class.java]
    }
}