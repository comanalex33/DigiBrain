package com.dig.digibrain.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.adapters.LearnPathDetailsAdapter
import com.dig.digibrain.databinding.TabRequestsBinding
import com.dig.digibrain.models.learnPaths.LearnPathExpandedModel

class LearnPathGraphFragment(
    var expandedLearnPath: LearnPathExpandedModel,
    var preview: Boolean,
    var sectionNumber: Long,
    var lessonNumber: Long,
    var theoryNumber: Long): Fragment() {

    private lateinit var binding: TabRequestsBinding
    private lateinit var adapter: LearnPathDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TabRequestsBinding.inflate(layoutInflater)

        adapter = LearnPathDetailsAdapter(
            context = requireContext(),
            learnPathExpandedModel = expandedLearnPath,
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



    fun setSection(sectionPosition: Int) {
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
}