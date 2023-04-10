package com.dig.digibrain.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.dig.digibrain.adapters.LearnPathAdapter
import com.dig.digibrain.adapters.ViewPagerAdapter
import com.dig.digibrain.databinding.ActivityLearnPathBinding
import com.dig.digibrain.fragments.LearnPathTabFragment
import com.dig.digibrain.models.learnPaths.LearnPathModel
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.services.server.ApiService
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnPathViewModel
import com.dig.digibrain.viewModels.LoginViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LearnPathActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLearnPathBinding
    private lateinit var viewModel: LearnPathViewModel

    lateinit var viewPager: ViewPager
    lateinit var tabs: TabLayout

    private var learnPaths: List<LearnPathModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnPathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

//        val adapter = ViewPagerAdapter(supportFragmentManager)
//        adapter.addFragment(LearnPathTabFragment(learnPaths), "All")
//        adapter.addFragment(LearnPathTabFragment(listOf()), "Started")
//        adapter.addFragment(LearnPathTabFragment(listOf()), "Done")
//        binding.viewPager.adapter = adapter
//        binding.tabs.setupWithViewPager(binding.viewPager)

        getLearnPaths()

        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[LearnPathViewModel::class.java]
    }

    private fun getLearnPaths() {
//        Toast.makeText(applicationContext, "It gets here", Toast.LENGTH_SHORT).show()
//        val call = ApiClient.getService().getLearnPaths2()
//        call.enqueue(object: Callback<List<LearnPathModel>> {
//            override fun onResponse(
//                call: Call<List<LearnPathModel>>,
//                response: Response<List<LearnPathModel>>
//            ) {
//                if(response.isSuccessful) {
//                    Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(applicationContext, "Bad", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<List<LearnPathModel>>, t: Throwable) {
//                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

        viewModel.getLearnPaths().observe(this) {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            learnPaths = resource.data

                            val adapter = ViewPagerAdapter(supportFragmentManager)
                            adapter.addFragment(LearnPathTabFragment(learnPaths), "All")
                            adapter.addFragment(LearnPathTabFragment(listOf()), "Started")
                            adapter.addFragment(LearnPathTabFragment(listOf()), "Done")
                            binding.viewPager.adapter = adapter
                            binding.tabs.setupWithViewPager(binding.viewPager)
                        }
                    }
                    Status.ERROR -> {
                        Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {}
                }
            }
        }
    }
}