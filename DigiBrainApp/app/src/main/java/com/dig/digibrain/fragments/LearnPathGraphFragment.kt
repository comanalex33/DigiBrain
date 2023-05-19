package com.dig.digibrain.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.activities.QuizActivity
import com.dig.digibrain.adapters.LearnPathDetailsAdapter
import com.dig.digibrain.databinding.TabRequestsBinding
import com.dig.digibrain.models.learnPaths.LearnPathExpandedModel
import com.dig.digibrain.models.learnPaths.LearnPathStatusUpdateModel
import com.dig.digibrain.objects.LearnPathLocalStatus
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnPathViewModel
import com.dig.digibrain.viewModels.LearnViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class LearnPathGraphFragment(
    var expandedLearnPath: LearnPathExpandedModel,
    var preview: Boolean,
    var sectionNumber: Long,
    var lessonNumber: Long,
    var theoryNumber: Long): Fragment() {

    var sectionPosition = 1

    private lateinit var binding: TabRequestsBinding
    private lateinit var viewModel: LearnPathViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: LearnPathDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TabRequestsBinding.inflate(layoutInflater)

        setupViewModel()
        sessionManager = SessionManager(requireContext())

        adapter = LearnPathDetailsAdapter(
            context = requireContext(),
            learnPathExpandedModel = sortedLearnPathDetails(),
            sectionPosition = 1,
            sectionNumber = sectionNumber,
            lessonNumber = lessonNumber,
            theoryNumber = theoryNumber,
            preview = preview
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if(LearnPathLocalStatus.done) {
//            sectionPosition = LearnPathLocalStatus.sectionNumber!!

            if(LearnPathLocalStatus.sectionNumber!!.toLong() > expandedLearnPath.sections.size) {
                updateStatus(true)
            }
            adapter = LearnPathDetailsAdapter(
                context = requireContext(),
                learnPathExpandedModel = expandedLearnPath,
                sectionPosition = sectionPosition,
                sectionNumber = LearnPathLocalStatus.sectionNumber!!.toLong(),
                lessonNumber = LearnPathLocalStatus.lessonNumber!!.toLong(),
                theoryNumber = LearnPathLocalStatus.theoryNumber!!.toLong(),
                preview = preview
            )
            binding.recyclerView.adapter = adapter

            updateStatus(false)

            LearnPathLocalStatus.done = false
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[LearnPathViewModel::class.java]
    }

    fun setSection(sectionPosition: Int) {
        this.sectionPosition = sectionPosition

        adapter = LearnPathDetailsAdapter(
            context = requireContext(),
            learnPathExpandedModel = expandedLearnPath,
            sectionPosition = sectionPosition,
            sectionNumber = sectionNumber,
            lessonNumber = lessonNumber,
            theoryNumber = theoryNumber,
            preview = preview
        )
        binding.recyclerView.adapter = adapter
    }

    fun changeVisibility(preview: Boolean) {
        this.preview = preview
        adapter.changeVisibility(preview)
        adapter.notifyDataSetChanged()
    }

    private fun sortedLearnPathDetails(): LearnPathExpandedModel {
        val sortedLearnPath = expandedLearnPath
        sortedLearnPath.sections = sortedLearnPath.sections.sortedBy { it.number }

        for(section in sortedLearnPath.sections) {
            section.lessons = section.lessons.sortedBy { it.number }
            for(lesson in section.lessons) {
                lesson.theory = lesson.theory.sortedBy { it.number }
            }
        }

        return sortedLearnPath
    }

    private fun updateStatus(finished: Boolean) {
        val authToken: String? = sessionManager.getBearerAuthToken()
        val username: String? = sessionManager.getUserName()
        val model = LearnPathStatusUpdateModel(
            sectionNumber = LearnPathLocalStatus.sectionNumber!!.toLong(),
            lessonNumber = LearnPathLocalStatus.lessonNumber!!.toLong(),
            theoryNumber = LearnPathLocalStatus.theoryNumber!!.toLong(),
            finished = finished
        )
        if(authToken != null && username != null) {
            viewModel.updateLearnPathStatus(authToken, expandedLearnPath.id, username, model)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {}
                            Status.ERROR -> {
                                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }
}