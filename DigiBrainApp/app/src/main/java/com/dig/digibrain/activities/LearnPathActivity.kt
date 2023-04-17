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
import com.dig.digibrain.dialogs.BottomSheetDialog
import com.dig.digibrain.fragments.LearnPathTabFragment
import com.dig.digibrain.models.learnPaths.LearnPathDetailedModel
import com.dig.digibrain.models.learnPaths.LearnPathModel
import com.dig.digibrain.models.quiz.QuizReportModel
import com.dig.digibrain.models.subject.SubjectModel
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

    private var subjects: List<SubjectModel> = mutableListOf()
    private var learnPaths: List<LearnPathModel> = mutableListOf()
    private var detailedLearnPaths = mutableListOf<LearnPathDetailedModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnPathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        getLearnPaths()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.filterButton.setOnClickListener {
            val dialog = BottomSheetDialog(subjects)
            dialog.show(supportFragmentManager, "Filter learn paths")
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[LearnPathViewModel::class.java]
    }

    private fun getLearnPaths() {
        viewModel.getLearnPaths().observe(this) {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (resource.data != null) {
                            learnPaths = resource.data
                            getSubjects(learnPaths)
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

    private fun getSubjects(learnPaths: List<LearnPathModel>) {
        val subjectIds = mutableListOf<Long>()
        for(learnPath in learnPaths) {
            if(!subjectIds.contains(learnPath.subjectId))
                subjectIds.add(learnPath.subjectId)
        }
        viewModel.getSubjectsForIds(subjectIds)
            .observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data != null) {
                                subjects = resource.data

                                for(learnPath in learnPaths) {
                                    detailedLearnPaths.add(LearnPathDetailedModel(
                                        learnPath.id,
                                        learnPath.title,
                                        learnPath.description,
                                        learnPath.author,
                                        learnPath.date,
                                        getSubjectNameById(subjects, learnPath.subjectId),
                                        0,
                                        learnPath.imageName
                                    ))
                                }

                                val adapter = ViewPagerAdapter(supportFragmentManager)
                                adapter.addFragment(LearnPathTabFragment(detailedLearnPaths), "All")
                                adapter.addFragment(LearnPathTabFragment(listOf()), "Started")
                                adapter.addFragment(LearnPathTabFragment(listOf()), "Done")
                                binding.viewPager.adapter = adapter
                                binding.tabs.setupWithViewPager(binding.viewPager)
                            }
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    private fun getSubjectNameById(subjects: List<SubjectModel>, id: Long): String? {
        for(subject in subjects) {
            if(subject.id == id)
                return subject.name
        }
        return null
    }

    // TODO - Create Server call with multiple class ids
}