package com.dig.digibrain.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.activities.LearnPathActivity
import com.dig.digibrain.adapters.LearnPathAdapter
import com.dig.digibrain.databinding.TabRequestsBinding
import com.dig.digibrain.models.learnPaths.LearnPathStatusModel

class LearnPathTabFragment(var type: String, var userStatus: List<LearnPathStatusModel>?, var preview: Boolean, var finished: Boolean): Fragment() {

    private lateinit var binding: TabRequestsBinding
    private lateinit var adapter: LearnPathAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TabRequestsBinding.inflate(layoutInflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val learnPaths = (activity as LearnPathActivity).getLearnPaths(type)
        adapter = LearnPathAdapter(requireContext(), learnPaths, userStatus, preview = preview, finished = finished)
        binding.recyclerView.adapter = adapter

        return binding.root
    }
}