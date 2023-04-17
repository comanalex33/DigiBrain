package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.adapters.LearnPathDetailsAdapter
import com.dig.digibrain.adapters.LearnPathSectionAdapter
import com.dig.digibrain.adapters.ViewPagerAdapter
import com.dig.digibrain.databinding.ActivityLearnPathDetailsBinding
import com.dig.digibrain.dialogs.LearnPathInfoDialog
import com.dig.digibrain.fragments.LearnPathGraphFragment
import com.dig.digibrain.fragments.LearnPathSectionsFragment
import com.dig.digibrain.fragments.LearnPathTabFragment
import com.dig.digibrain.interfaces.ILearnPathSectionSelected
import com.dig.digibrain.models.learnPaths.LearnPathDetailedModel
import com.dig.digibrain.models.learnPaths.LearnPathExpandedModel
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Helper.Companion.getInitials
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnPathDetailsViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LearnPathDetailsActivity : AppCompatActivity(), ILearnPathSectionSelected {

    private lateinit var binding: ActivityLearnPathDetailsBinding
    private lateinit var viewModel: LearnPathDetailsViewModel

    private var detailedLearnPath: LearnPathDetailedModel? = null
    private var expandedLearnPath: LearnPathExpandedModel? = null
    private var preview = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnPathDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        val bundle = intent.extras
        if(bundle != null) {
            detailedLearnPath = bundle.getParcelable("learnPath")
            detailedLearnPath?.apply {
                getLearnPathDetails(this.id)
                binding.learnPathTitle.text = this.title
                binding.learnPathInitials.text = this.title.getInitials()
            }
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

        binding.infoButton.setOnClickListener {
            detailedLearnPath?.apply {
                val dialog = LearnPathInfoDialog(this)
                dialog.show(supportFragmentManager, "Learn Path info")
            }
        }

        binding.learnPathStart.setOnClickListener {
            changeVisibility(preview = false)
            binding.learnPathStart.visibility = View.GONE

            val snackBar = Snackbar.make(binding.root, "Learn Path started", Snackbar.LENGTH_SHORT)
            snackBar.view.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.green))
            snackBar.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
            snackBar.show()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[LearnPathDetailsViewModel::class.java]
    }


    private fun getLearnPathDetails(id: Long) {
        viewModel.getLearnPathDetails(id).observe(this) {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            expandedLearnPath = resource.data

                            val adapter = ViewPagerAdapter(supportFragmentManager)
                            adapter.addFragment(LearnPathSectionsFragment(expandedLearnPath!!.sections), "Sections")
                            adapter.addFragment(LearnPathGraphFragment(expandedLearnPath!!, preview), "Graph")
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

    override fun changeSectionPosition(sectionPosition: Int) {
        val graphFragment = supportFragmentManager.findFragmentById(binding.viewPager.id) as LearnPathGraphFragment
        graphFragment.setSection(sectionPosition)
    }

    fun changeVisibility(preview: Boolean) {
        val graphFragment = supportFragmentManager.findFragmentById(binding.viewPager.id) as LearnPathGraphFragment
        graphFragment.changeVisibility(preview)
    }
}