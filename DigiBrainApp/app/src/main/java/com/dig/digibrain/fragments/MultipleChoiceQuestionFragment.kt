package com.dig.digibrain.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.adapters.MultipleChoiceAnswerAdapter
import com.dig.digibrain.databinding.FragmentMultipleChoiceQuestionBinding
import com.dig.digibrain.interfaces.ItemClickListener
import com.dig.digibrain.models.quiz.AnswerModel
import com.dig.digibrain.models.quiz.QuestionModel

class MultipleChoiceQuestionFragment(var question: QuestionModel, var answers: List<AnswerModel>) : QuestionFragment() {

    private lateinit var binding: FragmentMultipleChoiceQuestionBinding
    private lateinit var adapter: MultipleChoiceAnswerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMultipleChoiceQuestionBinding.inflate(layoutInflater)

        binding.questionText.text = question.text

        setupAnswers()

        return binding.root
    }

    private fun setupAnswers() {

        // Shuffle answers
        answers = answers.shuffled()

        // Create adapter
        adapter = MultipleChoiceAnswerAdapter(requireContext(), numberOfCorrectAnswers() == 1, answers)
        val itemClickListener = object : ItemClickListener {
            override fun onClick(name: String) {
                binding.answersRecyclerView.post {
                    kotlin.run {
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
        adapter.setItemClickListener(itemClickListener)

        // Configure RecyclerView
        binding.answersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.answersRecyclerView.adapter = adapter
    }

    private fun numberOfCorrectAnswers(): Int {
        var correctAnswers = 0
        for(answer in answers) {
            if(answer.correct)
                correctAnswers++
        }
        return correctAnswers
    }

    override fun answerQuestion(): Boolean {
        return adapter.answerQuestion()
    }

    override fun getSelectedAnswers(): List<AnswerModel> {
        return adapter.getSelectedAnswers()
    }
}