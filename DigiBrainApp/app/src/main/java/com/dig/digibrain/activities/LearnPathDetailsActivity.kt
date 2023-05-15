package com.dig.digibrain.activities

import android.content.Intent
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
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Helper.Companion.getInitials
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnPathDetailsViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LearnPathDetailsActivity : AppCompatActivity(), ILearnPathSectionSelected {

    private lateinit var binding: ActivityLearnPathDetailsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: LearnPathDetailsViewModel

    private var detailedLearnPath: LearnPathDetailedModel? = null
    private var expandedLearnPath: LearnPathExpandedModel? = null

    private var preview = true
    private var finished = false
    private var sectionNumber = 1L
    private var lessonNumber = 1L
    private var theoryNumber = 1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnPathDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)
        setupViewModel()

        val bundle = intent.extras
        if(bundle != null) {
            preview = bundle.getBoolean("preview")
            finished = bundle.getBoolean("finished")
            if(!preview || finished) {
                binding.learnPathStart.visibility = View.GONE
            }

            detailedLearnPath = bundle.getParcelable("learnPath")
            detailedLearnPath?.apply {
                getLearnPathDetails(this.id)
                binding.learnPathTitle.text = this.title
                binding.learnPathInitials.text = this.title.getInitials()
            }

            sectionNumber = bundle.getLong("sectionNumber")
            lessonNumber = bundle.getLong("lessonNumber")
            theoryNumber = bundle.getLong("theoryNumber")
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
            startLearnPath()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, LearnPathActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
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
                            adapter.addFragment(
                                LearnPathGraphFragment(
                                    expandedLearnPath = expandedLearnPath!!,
                                    preview = preview,
                                    sectionNumber = sectionNumber,
                                    lessonNumber = lessonNumber,
                                    theoryNumber = theoryNumber), "Graph")
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

    private fun startLearnPath() {
        val username = sessionManager.getUserName()
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(username != null && authToken != null && detailedLearnPath != null) {
            viewModel.startLearnPath(authToken, detailedLearnPath!!.id, username)
                .observe(this) {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                if (resource.data != null) {
                                    changeVisibility(preview = false)
                                    binding.learnPathStart.visibility = View.GONE

                                    val snackBar = Snackbar.make(binding.root, "Learn Path started", Snackbar.LENGTH_SHORT)
                                    snackBar.view.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.green))
                                    snackBar.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                                    snackBar.show()
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(
                                    applicationContext,
                                    resource.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }

    override fun changeSectionPosition(sectionPosition: Int) {
        val graphFragment = supportFragmentManager.findFragmentById(binding.viewPager.id) as LearnPathGraphFragment
        graphFragment.setSection(sectionPosition)
    }

    private fun changeVisibility(preview: Boolean) {
        val graphFragment = supportFragmentManager.findFragmentById(binding.viewPager.id) as LearnPathGraphFragment
        graphFragment.changeVisibility(preview)
    }
}