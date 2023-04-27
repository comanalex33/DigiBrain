package com.dig.digibrain.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.adapters.LearnPathAdapter
import com.dig.digibrain.adapters.LearnPathFilterSubjectAdapter
import com.dig.digibrain.databinding.TabRequestsBinding
import com.dig.digibrain.models.learnPaths.LearnPathDetailedModel
import com.dig.digibrain.models.learnPaths.LearnPathModel
import com.dig.digibrain.models.learnPaths.LearnPathStatusModel

class LearnPathTabFragment(var appContext: Context, var detailedLearnPaths: List<LearnPathDetailedModel>, var userStatus: List<LearnPathStatusModel>?, var preview: Boolean, var finished: Boolean): Fragment() {

    private lateinit var binding: TabRequestsBinding
    private lateinit var adapter: LearnPathAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TabRequestsBinding.inflate(layoutInflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = LearnPathAdapter(requireContext(), detailedLearnPaths, userStatus, preview = preview, finished = finished)
        binding.recyclerView.adapter = adapter

        return binding.root
    }

    fun updateList(list: List<LearnPathDetailedModel>) {
        detailedLearnPaths = list

        adapter = LearnPathAdapter(appContext, detailedLearnPaths, userStatus, preview = preview, finished = finished)
        binding.recyclerView.adapter = adapter
    }
}