package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.interfaces.ItemClickListener
import com.dig.digibrain.models.quiz.AnswerModel

class MultipleChoiceAnswerAdapter(var context: Context, private var singleAnswer: Boolean, private var arrayList: List<AnswerModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener
    private var selectedPosition = -1
    private var questionAnswered = false
    private var selectedAnswers = mutableListOf<AnswerModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = if(singleAnswer) {
            LayoutInflater.from(context).inflate(R.layout.card_single_choice_answer, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.card_multiple_choice_answer, parent, false)
        }
        return MultipleChoiceAnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val answer = arrayList[position]

        (holder as MultipleChoiceAnswerViewHolder).initializeUIComponents(answer)

        if(questionAnswered) {
            if(answer.correct) {
                holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                    context,
                    R.color.green
                )
            }
            if(holder.answerButton.isChecked && !answer.correct) {
                holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                    context,
                    R.color.red
                )
            }
            if(holder.answerButton.isChecked) {
                selectedAnswers.add(answer)
            }
        } else {
            // Set correct button checked
            holder.answerButton.isChecked = position == selectedPosition

            holder.answerButton.setOnCheckedChangeListener { _, b ->
                if (b) {
                    selectedPosition = holder.adapterPosition
                    if(singleAnswer)
                        itemClickListener.onClick(holder.answerButton.text.toString())
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    fun answerQuestion(): Boolean {
        if(selectedPosition != -1) {
            questionAnswered = true
            notifyDataSetChanged()
            return true
        }
        return false
    }

    fun getSelectedAnswers(): List<AnswerModel> {
        return selectedAnswers
    }

    inner class MultipleChoiceAnswerViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView
        var answerButton: CompoundButton
        var correct: Boolean = false

        init {
            if (singleAnswer) {
                card = myView.findViewById(R.id.single_choice_answer_card)
                answerButton = myView.findViewById(R.id.single_choice_answer)
            } else {
                card = myView.findViewById(R.id.multiple_choice_answer_card)
                answerButton = myView.findViewById(R.id.multiple_choice_answer)
            }
        }

        fun initializeUIComponents(model: AnswerModel) {
            answerButton.text = model.text
            correct = model.correct
        }
    }
}