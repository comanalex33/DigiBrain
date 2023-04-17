package com.dig.digibrain.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.adapters.LearnPathAdapter
import com.dig.digibrain.databinding.TabRequestsBinding
import com.dig.digibrain.models.learnPaths.LearnPathDetailedModel
import com.dig.digibrain.models.learnPaths.LearnPathModel

class LearnPathTabFragment(var detailedLearnPaths: List<LearnPathDetailedModel>): Fragment() {

    private lateinit var binding: TabRequestsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TabRequestsBinding.inflate(layoutInflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = LearnPathAdapter(requireContext(), detailedLearnPaths)

        return binding.root
    }
}