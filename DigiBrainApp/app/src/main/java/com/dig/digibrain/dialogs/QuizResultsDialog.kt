package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.R
import com.dig.digibrain.databinding.DialogQuizResultsBinding
import com.dig.digibrain.interfaces.ItemClickListener

class QuizResultsDialog(var listener: ItemClickListener, var timeout: Boolean, var numberOfQuestions: Int, var totalTime: String, var score: Double): DialogFragment() {

    private lateinit var binding: DialogQuizResultsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogQuizResultsBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        dialog?.setCanceledOnTouchOutside(false)

        if(score / numberOfQuestions.toDouble() >= 0.5) {
            binding.dialogCard.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.green
            )
            binding.statusMessage.text = requireContext().getString(R.string.congratulations)
            binding.quizStatusLogo.setImageResource(R.drawable.kid_happy)

            binding.statusMessage.setTextColor(resources.getColor(R.color.black_white))
            binding.questionNumberText.setTextColor(resources.getColor(R.color.black_white))
            binding.questionsNumber.setTextColor(resources.getColor(R.color.black_white))
            binding.totalTimeText.setTextColor(resources.getColor(R.color.black_white))
            binding.totalTime.setTextColor(resources.getColor(R.color.black_white))
            binding.scoreText.setTextColor(resources.getColor(R.color.black_white))
            binding.score.setTextColor(resources.getColor(R.color.black_white))
        } else {
            binding.dialogCard.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.red
            )
            binding.statusMessage.text = requireContext().getString(R.string.failure)
            binding.quizStatusLogo.setImageResource(R.drawable.kid_unhappy)

            binding.statusMessage.setTextColor(resources.getColor(R.color.white_black))
            binding.questionNumberText.setTextColor(resources.getColor(R.color.white_black))
            binding.questionsNumber.setTextColor(resources.getColor(R.color.white_black))
            binding.totalTimeText.setTextColor(resources.getColor(R.color.white_black))
            binding.totalTime.setTextColor(resources.getColor(R.color.white_black))
            binding.scoreText.setTextColor(resources.getColor(R.color.white_black))
            binding.score.setTextColor(resources.getColor(R.color.white_black))
        }

        if(timeout) {
            binding.statusMessage.text = requireContext().getString(R.string.timeout)
        }

        binding.questionsNumber.text = numberOfQuestions.toString()
        binding.totalTime.text = totalTime
        binding.score.text = score.toString()

        binding.closeButton.setOnClickListener {
            listener.onClick("Close")
        }

        return binding.root
    }
}