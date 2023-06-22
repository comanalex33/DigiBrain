package com.dig.digibrain.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.adapters.LearnPathSectionAdapter
import com.dig.digibrain.databinding.FragmentLearnPathSectionsBinding
import com.dig.digibrain.databinding.TabRequestsBinding
import com.dig.digibrain.interfaces.ILearnPathSectionSelected
import com.dig.digibrain.models.learnPaths.LearnPathSection

class LearnPathSectionsFragment(var sections: List<LearnPathSection>, var sectionNumber: Long): Fragment(), ILearnPathSectionSelected {

    private lateinit var binding: FragmentLearnPathSectionsBinding
    private lateinit var listener: ILearnPathSectionSelected

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearnPathSectionsBinding.inflate(layoutInflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = LearnPathSectionAdapter(requireContext(), this, sections.sortedBy { it.number }, sectionNumber = sectionNumber)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ILearnPathSectionSelected) {
            listener = context
        }
    }

    override fun changeSectionPosition(sectionPosition: Int) {
        listener.changeSectionPosition(sectionPosition)
    }

}