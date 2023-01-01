package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardMultipleChoiceAnswerBinding

class MultipleChoiceAnswerAdapter(var context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardMultipleChoiceAnswerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardMultipleChoiceAnswerBinding.inflate(LayoutInflater.from(context), parent, false)
        return MultipleChoiceAnswerViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class MultipleChoiceAnswerViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.multiple_choice_answer_card)
        var answerText: TextView = myView.findViewById(R.id.multiple_choice_answer)

        fun initializeUIComponents() {
            // TODO - Complete question
        }
    }
}