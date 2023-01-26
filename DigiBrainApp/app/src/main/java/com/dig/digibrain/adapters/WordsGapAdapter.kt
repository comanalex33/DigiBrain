package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.models.quiz.AnswerModel

class WordsGapAdapter(var context: Context, private var arrayList: List<String>, var answers: List<AnswerModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var spinnerAnswers = mutableMapOf<Int, CorrectAnswerModel>()
    private var questionAnswered = false
    private var failedTry = false

    private var score: Double = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 0) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.card_words_gap_text, parent, false)
            TextViewHolder(view)
        } else {
            val view =
                LayoutInflater.from(context).inflate(R.layout.card_words_gap_blank, parent, false)
            GapViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(arrayList[position] != "__")
            return 0
        return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder.itemViewType == 0) {
            (holder as TextViewHolder).initializeUIComponents(arrayList[position])
        } else {
            (holder as GapViewHolder).initializeUIComponents(position)


            if(questionAnswered) {
                if(spinnerAnswers.containsKey(position)) {
                    if(spinnerAnswers[position]!!.correct) {
                        holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                            context,
                            R.color.green
                        )
                        holder.spinner.setSelection(spinnerAnswers[position]!!.answerSpinnerPosition)
                        score += 1.0 / answers.size
                    } else {
                        holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                            context,
                            R.color.red
                        )
                        holder.spinner.setSelection(spinnerAnswers[position]!!.answerSpinnerPosition)
                    }
                }
            }

            if(failedTry) {
                if(spinnerAnswers.containsKey(position)) {
                    if(spinnerAnswers[position]!!.answerSpinnerPosition == 0) {
                        holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                            context,
                            R.color.yellow_black
                        )
                    }
                    holder.spinner.setSelection(spinnerAnswers[position]!!.answerSpinnerPosition)
                }
                if(!spinnerAnswers.containsKey(position)) {
                    holder.card.backgroundTintList = AppCompatResources.getColorStateList(
                        context,
                        R.color.yellow_black
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class TextViewHolder(myView: View): RecyclerView.ViewHolder(myView) {
        var textView: TextView = myView.findViewById(R.id.words_gap_text)

        fun initializeUIComponents(text: String) {
            textView.text = text
        }
    }

    inner class GapViewHolder(myView: View): RecyclerView.ViewHolder(myView) {
        val card: View = myView.findViewById(R.id.words_gap_card)
        var spinner: Spinner = myView.findViewById(R.id.words_gap_blank)

        fun initializeUIComponents(position: Int) {
            val dataAdapter = ArrayAdapter(context, R.layout.quiz_spinner_item, getAnswersText())
            spinner.adapter = dataAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if(p2 != 0) {
                        val answerCorrectPosition = getAnswerCorrectPosition(answers[p2 - 1])
                        spinnerAnswers[position] =
                            CorrectAnswerModel(position == answerCorrectPosition, answers[p2 - 1], p2)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }
        }
    }

    fun answerQuestion(): Boolean {
        if(allSpinnersCompleted()) {
            questionAnswered = true
            notifyDataSetChanged()
            return true
        }
        failedTry = true
        notifyDataSetChanged()
        return false
    }

    fun getScore(): Double {
        return score
    }

    private fun allSpinnersCompleted(): Boolean {
        val it = arrayList.listIterator()
        while(it.hasNext()) {
            if(it.next() == "__") {
                if(spinnerAnswers.containsKey(it.nextIndex() - 1))
                    continue
                Toast.makeText(context, "${arrayList.toString()} ${it.nextIndex()} ${it.nextIndex()}", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    fun getSelectedAnswers(): List<AnswerModel> {
        val selectedAnswers = mutableListOf<AnswerModel>()
        for((_, spinnerAnswer) in spinnerAnswers) {
            val model = spinnerAnswer.answerModel
            model.correct = spinnerAnswer.correct
            selectedAnswers.add(model)
        }
        return selectedAnswers
    }

    fun getAnswersText(): List<String> {
        val list = mutableListOf(" - null - ")
        for(answer in answers) {
            list.add(answer.text)
        }
        return list
    }

    fun getAnswerCorrectPosition(answer: AnswerModel): Int {
        var gapPosition = 0
        val it = arrayList.listIterator()
        while(it.hasNext()) {
            if(it.next() == "__") {
                gapPosition++
                if(gapPosition == answer.position)
                    return it.nextIndex() - 1
            }
        }
        return -1
    }
}

private class CorrectAnswerModel(var correct: Boolean,var answerModel: AnswerModel, var answerSpinnerPosition: Int)