package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.adapters.ViewPagerAdapter
import com.dig.digibrain.databinding.ActivityLearnPathBinding
import com.dig.digibrain.dialogs.BottomSheetDialog
import com.dig.digibrain.fragments.LearnPathTabFragment
import com.dig.digibrain.interfaces.IApplyLearnPathFilter
import com.dig.digibrain.models.learnPaths.LearnPathDetailedModel
import com.dig.digibrain.models.learnPaths.LearnPathModel
import com.dig.digibrain.models.learnPaths.LearnPathStatusModel
import com.dig.digibrain.models.subject.ClassModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnPathViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class LearnPathActivity : AppCompatActivity(), IApplyLearnPathFilter {

    private lateinit var binding: ActivityLearnPathBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: LearnPathViewModel

    private var classes: List<ClassModel> = mutableListOf()
    private var domains: List<DomainModel> = mutableListOf()
    private var subjects: List<SubjectModel> = mutableListOf()

    private var learnPaths: List<LearnPathModel> = mutableListOf()
    private var detailedLearnPaths = mutableListOf<LearnPathDetailedModel>()
    private var filteredLearnPaths = mutableListOf<LearnPathDetailedModel>()
    private var userStatus = mutableListOf<LearnPathStatusModel>()

    private var selectedSubjectsIds = mutableListOf<Long>()
    private var selectedName = ""

    private var currentClass: Int = 2
    private var isUniversity: Boolean = false

    private var ALL_FRAGMENT = "All"
    private var STARTED_FRAGMENT = "Started"
    private var DONE_FRAGMENT = "Done"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnPathBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ALL_FRAGMENT = resources.getString(R.string.all)
        STARTED_FRAGMENT = resources.getString(R.string.started)
        DONE_FRAGMENT = resources.getString(R.string.done)

        sessionManager = SessionManager(applicationContext)
        setupViewModel()
        getLearnPaths()
        getClasses()
        getDomains()

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.filterButton.setOnClickListener {
            val dialog = BottomSheetDialog(subjects, currentClass, isUniversity, this)
            dialog.setClasses(classes)
            dialog.setDomains(domains)
            dialog.setSelected(selectedSubjectsIds, selectedName)
            dialog.show(supportFragmentManager, "Filter learn paths")
        }

        binding.classFilterValue.text = "2"
        binding.subjectFilter.visibility = View.GONE
        binding.nameFilter.visibility = View.GONE
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

                                detailedLearnPaths = getDetailedLearnPaths(learnPaths).toMutableList()
                                filteredLearnPaths = detailedLearnPaths
                                getLearnPathsForUser(detailedLearnPaths)
                            }
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    private fun getLearnPathsForUser(detailedLearnPaths: List<LearnPathDetailedModel>) {
        val username = sessionManager.getUserName()
        username?.apply {
            viewModel.getLearnPathsStatusForUsername(this)
                .observe(this@LearnPathActivity) {
                    it.let { resource ->
                        when(resource.status) {
                            Status.SUCCESS -> {
                                if(resource.data != null) {
                                    val adapter = ViewPagerAdapter(supportFragmentManager)
                                    userStatus = resource.data.toMutableList()

                                    adapter.addFragment(LearnPathTabFragment(ALL_FRAGMENT, null, preview = true, finished = false), ALL_FRAGMENT)
                                    adapter.addFragment(LearnPathTabFragment(STARTED_FRAGMENT, resource.data, preview = false, finished = false), STARTED_FRAGMENT)
                                    adapter.addFragment(LearnPathTabFragment(DONE_FRAGMENT, null, preview = true, finished = true) , DONE_FRAGMENT)

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
    }

    private fun getClasses() {
        viewModel.getAllClasses()
            .observe(this@LearnPathActivity) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data != null) {
                                classes = resource.data
                            }
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    private fun getDomains() {
        viewModel.getAllDomains()
            .observe(this@LearnPathActivity) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data != null) {
                                domains = resource.data
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

    private fun splitLearnPaths(detailedLearnPaths: List<LearnPathDetailedModel>, userLearnPathsStatus: List<LearnPathStatusModel>, tag: String): List<LearnPathDetailedModel> {
        return when(tag) {
            ALL_FRAGMENT -> {
                val filteredList = mutableListOf<LearnPathDetailedModel>()
                for(learnPath in detailedLearnPaths) {
                    var started = false
                    for(userLearnPath in userLearnPathsStatus) {
                        if(learnPath.id == userLearnPath.pathLearnId)
                            started = true
                    }
                    if(!started)
                        filteredList.add(learnPath)
                }
                filteredList
            }
            STARTED_FRAGMENT -> {
                val filteredList = mutableListOf<LearnPathDetailedModel>()
                for(learnPath in detailedLearnPaths) {
                    for(userLearnPath in userLearnPathsStatus) {
                        if(learnPath.id == userLearnPath.pathLearnId && !userLearnPath.finished)
                            filteredList.add(learnPath)
                    }
                }
                filteredList
            }
            DONE_FRAGMENT -> {
                val filteredList = mutableListOf<LearnPathDetailedModel>()
                for(learnPath in detailedLearnPaths) {
                    for(userLearnPath in userLearnPathsStatus) {
                        if(learnPath.id == userLearnPath.pathLearnId && userLearnPath.finished)
                            filteredList.add(learnPath)
                    }
                }
                filteredList
            }
            else -> {
                mutableListOf()
            }
        }
    }

    private fun filterLearnPaths(learnPaths: List<LearnPathModel>, subjectIds: List<Long>, name: String): List<LearnPathDetailedModel> {
        val filteredList = mutableListOf<LearnPathModel>()
        for(learnPath in learnPaths) {
            // Filter based on subject
            if(subjectIds.isNotEmpty()) {
                if(subjectIds.contains(learnPath.subjectId)) {
                    filteredList.add(learnPath)
                }
            } else {
                filteredList.add(learnPath)
            }

            // Filter based on name
            if(name != "") {
                if(!learnPath.title.contains(name)) {
                    filteredList.remove(learnPath)
                }
            }
        }

        return getDetailedLearnPaths(filteredList)
    }

    private fun getDetailedLearnPaths(learnPaths: List<LearnPathModel>): List<LearnPathDetailedModel> {
        val list = mutableListOf<LearnPathDetailedModel>()

        for(learnPath in learnPaths) {
            list.add(LearnPathDetailedModel(
                learnPath.id,
                learnPath.title,
                learnPath.description,
                learnPath.author,
                learnPath.date,
                learnPath.started,
                getSubjectNameById(subjects, learnPath.subjectId),
                0,
                learnPath.imageName
            ))
        }

        return list
    }

    override fun changeClass(classNumber: Int, isUniversity: Boolean) {
        this.currentClass = classNumber
        this.isUniversity = isUniversity
    }

    override fun applyFilter(subjectIds: List<Long>, name: String) {
        selectedSubjectsIds = subjectIds.toMutableList()
        selectedName = name

        filteredLearnPaths = filterLearnPaths(learnPaths, subjectIds, name).toMutableList()

        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(LearnPathTabFragment(ALL_FRAGMENT, null, preview = true, finished = false), ALL_FRAGMENT)
        adapter.addFragment(LearnPathTabFragment(STARTED_FRAGMENT, userStatus, preview = false, finished = false), STARTED_FRAGMENT)
        adapter.addFragment(LearnPathTabFragment(DONE_FRAGMENT, null, preview = true, finished = true), DONE_FRAGMENT)

        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        // Update class text
        binding.classFilterValue.text = "$currentClass"

        // Update subjects text
        if(subjectIds.isEmpty()) {
            binding.subjectFilter.visibility = View.GONE
        } else {
            var subjectsString = ""
            for(subjectId in subjectIds) {
                val subjectName = getSubjectNameById(subjects, subjectId)
                subjectName?.apply {
                    subjectsString += "$subjectName, "
                }
            }
            if(subjectsString != "")
                subjectsString = subjectsString.dropLast(2)
            binding.subjectFilterValue.text = subjectsString
            binding.subjectFilter.visibility = View.VISIBLE
        }

        // Update name text
        if(name == "") {
            binding.nameFilter.visibility = View.GONE
        } else {
            binding.nameFilterValue.text = name
            binding.nameFilter.visibility = View.VISIBLE
        }
    }

    fun getLearnPaths(type: String): List<LearnPathDetailedModel> {
        return splitLearnPaths(filteredLearnPaths, userStatus, type)
    }
}