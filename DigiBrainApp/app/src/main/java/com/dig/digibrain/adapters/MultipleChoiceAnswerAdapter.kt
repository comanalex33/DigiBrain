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
import com.dig.digibrain.models.quiz.AnswerModel

class MultipleChoiceAnswerAdapter(var context: Context, private var arrayList: List<AnswerModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardMultipleChoiceAnswerBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardMultipleChoiceAnswerBinding.inflate(LayoutInflater.from(context), parent, false)
        return MultipleChoiceAnswerViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val multipleChoiceAnswer = arrayList[position]

        (holder as MultipleChoiceAnswerViewHolder).initializeUIComponents(multipleChoiceAnswer)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class MultipleChoiceAnswerViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.multiple_choice_answer_card)
        var answerText: TextView = myView.findViewById(R.id.multiple_choice_answer)
        var correct: Boolean = false

        fun initializeUIComponents(model: AnswerModel) {
            answerText.text = model.text
            correct = model.correct
        }
    }
}