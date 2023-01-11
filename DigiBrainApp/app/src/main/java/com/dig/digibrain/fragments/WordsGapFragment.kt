package com.dig.digibrain.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.adapters.WordsGapAdapter
import com.dig.digibrain.databinding.FragmentWordsGapBinding
import com.dig.digibrain.models.quiz.AnswerModel
import com.dig.digibrain.models.quiz.QuestionModel
import com.dig.digibrain.utils.Helper
import com.dig.digibrain.utils.Helper.Companion.splitKeeping
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent

class WordsGapFragment(var question: QuestionModel, var answers: List<AnswerModel>) : QuestionFragment() {

    private lateinit var binding: FragmentWordsGapBinding
    private lateinit var adapter: WordsGapAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWordsGapBinding.inflate(layoutInflater)

        val splitText = splitText(question.text)

        answers = answers.shuffled()

        adapter = WordsGapAdapter(requireContext(), splitText, answers)

        val layoutManager = FlexboxLayoutManager(requireContext())
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.FLEX_START
        binding.answersRecyclerView.layoutManager = layoutManager

        binding.answersRecyclerView.adapter = adapter

        return binding.root
    }

    private fun splitText(text: String): List<String> {
        val textList = text.splitKeeping("__")
        val newTextList = mutableListOf<String>()
        for(element in textList) {
            newTextList.addAll(element.trim().split("\\s+".toRegex()).toList())
        }
        return newTextList
    }

    override fun answerQuestion(): Boolean {
        return adapter.answerQuestion()
    }

    override fun getSelectedAnswers(): List<AnswerModel> {
        return adapter.getSelectedAnswers()
    }

}