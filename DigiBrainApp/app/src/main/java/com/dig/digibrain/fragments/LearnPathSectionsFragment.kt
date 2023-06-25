package com.dig.digibrain.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.adapters.LearnPathSectionAdapter
import com.dig.digibrain.databinding.FragmentLearnPathSectionsBinding
import com.dig.digibrain.interfaces.ILearnPathSectionSelected
import com.dig.digibrain.models.learnPaths.LearnPathSection
import com.dig.digibrain.models.learnPaths.LearnPathStatusUpdateModel
import com.dig.digibrain.objects.LearnPathLocalStatus
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnPathViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class LearnPathSectionsFragment(var sections: List<LearnPathSection>, var sectionNumber: Long): Fragment(), ILearnPathSectionSelected {

    private lateinit var binding: FragmentLearnPathSectionsBinding
    private lateinit var viewModel: LearnPathViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var listener: ILearnPathSectionSelected

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearnPathSectionsBinding.inflate(layoutInflater)

        setupViewModel()
        sessionManager = SessionManager(requireContext())

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = LearnPathSectionAdapter(
            requireContext(),
            this,
            sections.sortedBy { it.number },
            sectionNumber = sectionNumber
        )

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if(LearnPathLocalStatus.sectionFinished != null) {
            if(sections.sortedBy { it.number }.last().number >= LearnPathLocalStatus.sectionFinished!!) {
                binding.recyclerView.adapter = LearnPathSectionAdapter(
                    requireContext(),
                    this,
                    sections.sortedBy { it.number },
                    sectionNumber = LearnPathLocalStatus.sectionFinished!! + 1
                )
            }
            if(sections.sortedBy { it.number }.last().number.toLong() == LearnPathLocalStatus.sectionFinished!!) {
                finishLearnPath()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ILearnPathSectionSelected) {
            listener = context
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[LearnPathViewModel::class.java]
    }

    override fun changeSectionPosition(sectionPosition: Int) {
        listener.changeSectionPosition(sectionPosition)
    }

    private fun finishLearnPath() {
        val authToken: String? = sessionManager.getBearerAuthToken()
        val username: String? = sessionManager.getUserName()
        val model = LearnPathStatusUpdateModel(
            sectionNumber = LearnPathLocalStatus.sectionNumber!!.toLong(),
            lessonNumber = LearnPathLocalStatus.lessonNumber!!.toLong(),
            theoryNumber = LearnPathLocalStatus.theoryNumber!!.toLong(),
            finished = true
        )

        if(authToken != null && username != null) {
            viewModel.updateLearnPathStatus(authToken, sections.first().pathLearnId, username, model)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                val snackBar = Snackbar.make(binding.root, getString(R.string.Learn_path_finished), Snackbar.LENGTH_SHORT)
                                snackBar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                                snackBar.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                                snackBar.show()
                            }
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