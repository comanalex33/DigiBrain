package com.dig.digibrain.dialogs

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.adapters.QuizTypeAdapter
import com.dig.digibrain.adapters.SpacingItemDecorator
import com.dig.digibrain.databinding.DialogChooseTypeBinding
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.interfaces.IQuizTypeChanged
import com.dig.digibrain.models.QuizTypeModel

class ChooseTypeDialog(var application: Application, var currentType: QuizTypeModel?): DialogFragment(), IQuizTypeChanged {

    private lateinit var binding: DialogChooseTypeBinding
    private lateinit var listener: IQuizTypeChanged

    private var selectedType: QuizTypeModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChooseTypeBinding.inflate(layoutInflater)

        selectedType = currentType

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val quizTypes = listOf(
            QuizTypeModel(name = "Multiple Choice", key = "MultipleChoice", iconId = R.drawable.ic_multiple_choice),
            QuizTypeModel(name = "Words gap", key = "WordsGap", iconId = R.drawable.ic_words_gap),
            QuizTypeModel(name = "Written solution", key = "WrittenSolution", iconId = R.drawable.ic_written_solution)
        )

        setQuizTypes(quizTypes)

        binding.selectButton.setOnClickListener {
            if(selectedType == null) {
                binding.errorMessage.text = resources.getString(R.string.select_type)
                binding.errorMessage.visibility = View.VISIBLE
            } else {
                listener.changeQuizType(selectedType!!)
                dialog!!.dismiss()
            }
        }

        return binding.root
    }

    fun addListener(listener: IQuizTypeChanged) {
        this.listener = listener
    }

    private fun setQuizTypes(quizTypes: List<QuizTypeModel>) {
        val quizTypesAdapter = QuizTypeAdapter(
            application.applicationContext,
            this,
            currentType,
            quizTypes
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.addItemDecoration(SpacingItemDecorator(30, 10))
        binding.recyclerView.adapter = quizTypesAdapter
    }

    override fun changeQuizType(value: QuizTypeModel) {
        selectedType = value
    }
}