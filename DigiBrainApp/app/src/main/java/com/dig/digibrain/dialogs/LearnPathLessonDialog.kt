package com.dig.digibrain.dialogs

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.R
import com.dig.digibrain.activities.LessonActivity
import com.dig.digibrain.databinding.DialogLearnPathLessonDetailsBinding
import com.dig.digibrain.models.learnPaths.LearnPathLesson
import com.dig.digibrain.models.learnPaths.LearnPathQuiz
import com.dig.digibrain.models.learnPaths.LearnPathTheory

class LearnPathLessonDialog(var lesson: LearnPathLesson?, var theory: LearnPathTheory? = null, var quiz: LearnPathQuiz? = null): DialogFragment() {

    private lateinit var binding: DialogLearnPathLessonDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLearnPathLessonDetailsBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        initializeUIComponents()

        theory?.apply {
            binding.startButton.setOnClickListener {
                val intent = Intent(requireContext(), LessonActivity::class.java)

                val bundle = Bundle()
                bundle.putString("lessonTitle", this.title)
                bundle.putString("lessonContent", this.text)
                intent.putExtras(bundle)

                startActivity(intent)

                dialog?.dismiss()
            }
        }

        return binding.root
    }

    private fun initializeUIComponents() {
        theory?.apply {
            binding.lessonTitle.text = this.title
            binding.lessonNumber.text = "Lectia ${this.number} din ${lesson!!.theory.size}"
        }
        quiz?.apply {
            binding.lessonTitle.text = "Quiz"
            binding.lessonNumber.visibility = View.INVISIBLE

            // Set yellow background for Quiz
            binding.card.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.yellow
            )
            binding.startButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
        }
    }
}