package com.dig.digibrain.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.adapters.MultipleChoiceAnswerAdapter
import com.dig.digibrain.databinding.FragmentMultipleChoiceQuestionBinding
import com.dig.digibrain.models.quiz.AnswerModel

class MultipleChoiceQuestionFragment(var questionText: String, var answers: List<AnswerModel>) : Fragment() {

    private lateinit var binding: FragmentMultipleChoiceQuestionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMultipleChoiceQuestionBinding.inflate(layoutInflater)

        binding.questionText.text = questionText

        val adapter = MultipleChoiceAnswerAdapter(requireContext(), answers)
        binding.answersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.answersRecyclerView.adapter = adapter


        return binding.root
    }
}