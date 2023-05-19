package com.dig.digibrain.dialogs

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.activities.LessonActivity
import com.dig.digibrain.activities.QuestionActivity
import com.dig.digibrain.databinding.DialogLearnPathLessonDetailsBinding
import com.dig.digibrain.models.learnPaths.LearnPathLesson
import com.dig.digibrain.models.learnPaths.LearnPathQuiz
import com.dig.digibrain.models.learnPaths.LearnPathSection
import com.dig.digibrain.models.learnPaths.LearnPathTheory
import com.dig.digibrain.objects.LearnPathLocalStatus

class LearnPathLessonDialog(
    var learnPathId: Long,
    var sectionNumber: Long,
    var lesson: LearnPathLesson?,
    var theory: LearnPathTheory? = null,
    var quizes: List<LearnPathQuiz>? = null): DialogFragment() {

    private lateinit var binding: DialogLearnPathLessonDetailsBinding
    private lateinit var listener: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var position: Int = 0

    private var backgroundColor = R.color.green
    private var buttonText = "Start"
    private var updateStatus = true

    private val VIEW_TYPE_LESSON = 0
    private val VIEW_TYPE_THEORY = 1
    private val VIEW_TYPE_QUIZ = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLearnPathLessonDetailsBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        initializeUIComponents()

        if(theory != null || quizes != null) {
            binding.startButton.setOnClickListener {
                if(backgroundColor != R.color.gray) {

                    theory?.apply {
                        val intent = Intent(requireContext(), LessonActivity::class.java)

                        val bundle = Bundle()
                        bundle.putString("lessonTitle", this.title)
                        bundle.putString("lessonContent", this.text)
                        bundle.putBoolean("learnPath", true)
                        bundle.putBoolean("updateStatus", updateStatus)
                        intent.putExtras(bundle)

                        startActivity(intent)
//                        val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                            if (result.resultCode == Activity.RESULT_OK) {
//                                val data: Intent? = result.data
//                                val resultValue = data?.getStringExtra("result_key")
//                                Toast.makeText(context, resultValue, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                        getResult.launch(intent)

                        // Set current point
                        LearnPathLocalStatus.currentSectionNumber = sectionNumber.toInt()
                        LearnPathLocalStatus.currentLessonNumber = lesson!!.number
                        if(theory != null) {
                            LearnPathLocalStatus.currentTheoryNumber = theory!!.number
                        }
                        else {
                            LearnPathLocalStatus.currentTheoryNumber = 1
                        }

                        // Set next and update database
                        if(updateStatus)
                            setNext()

                        dialog?.dismiss()
                    }
//                    if(quizes != null)
//                        Toast.makeText(context, "Test", Toast.LENGTH_SHORT).show()
//                    else
//                        Toast.makeText(context, "E null", Toast.LENGTH_SHORT).show()
                    quizes?.apply {

                        val intent = Intent(requireContext(), QuestionActivity::class.java)

                        val ids = mutableListOf<Long>()
                        for(quiz in this)
                            ids.add(quiz.questionId)

                        var score = 0
                        for(quiz in this)
                            score += quiz.score

                        val bundle = Bundle()
                        bundle.putLongArray("questionIds", ids.toLongArray())
                        bundle.putBoolean("updateStatus", updateStatus)
                        bundle.putLong("learnPathId", learnPathId)
                        bundle.putInt("score", score)
                        intent.putExtras(bundle)

                        startActivity(intent)

                        // Set current point
                        LearnPathLocalStatus.currentSectionNumber = sectionNumber.toInt()
                        LearnPathLocalStatus.currentLessonNumber = lesson!!.number
                        if(theory != null) {
                            LearnPathLocalStatus.currentTheoryNumber = theory!!.number
                        }
                        else {
                            LearnPathLocalStatus.currentTheoryNumber = 1
                        }

                        if(updateStatus)
                            setNext()

                        dialog?.dismiss()
                    }
                }
            }
        }

        return binding.root
    }

    private fun setNext() {
        LearnPathLocalStatus.sectionNumber = sectionNumber.toInt()
        LearnPathLocalStatus.lessonNumber = lesson!!.number
        if(theory != null) {
            LearnPathLocalStatus.theoryNumber = theory!!.number
            LearnPathLocalStatus.currentTheoryNumber = theory!!.number
        }
        else {
            LearnPathLocalStatus.theoryNumber = 1
            LearnPathLocalStatus.currentTheoryNumber = 1
        }

        LearnPathLocalStatus.done = false

        when(listener.getItemViewType(position + 1)) {
            VIEW_TYPE_LESSON -> {
                LearnPathLocalStatus.lessonNumber = lesson!!.number + 1
                LearnPathLocalStatus.theoryNumber = 1
            }
            VIEW_TYPE_THEORY -> {
                LearnPathLocalStatus.theoryNumber = theory!!.number + 1
            }
            VIEW_TYPE_QUIZ -> {
                LearnPathLocalStatus.theoryNumber = 0
            }
            else -> {
                LearnPathLocalStatus.sectionNumber = sectionNumber.toInt() + 1
                LearnPathLocalStatus.lessonNumber = 1
                LearnPathLocalStatus.theoryNumber = 1
            }
        }
    }

    private fun initializeUIComponents() {
        theory?.apply {
            binding.lessonTitle.text = this.title
            binding.lessonNumber.text = "Lectia ${this.number} din ${lesson!!.theory.size}"
        }
        quizes?.apply {
            binding.lessonTitle.text = "Quiz"
            binding.lessonNumber.visibility = View.INVISIBLE

            // Set yellow background for Quiz
            binding.card.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.yellow
            )
            binding.startButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
        }

        binding.card.backgroundTintList = AppCompatResources.getColorStateList(
            requireContext(),
            backgroundColor
        )

        binding.startButton.text = buttonText
        when(backgroundColor) {
            R.color.yellow -> {
                binding.startButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            }
            R.color.gray -> {
                binding.startButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
                binding.startButton.isClickable = false
            }
            R.color.green -> {
                binding.startButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
        }

        if (backgroundColor == R.color.gray)
            binding.startButton.isClickable = false
    }

    fun setupUI(backgroundColor: Int, buttonText: String, update: Boolean = true) {
//        binding.card.backgroundTintList = AppCompatResources.getColorStateList(
//            requireContext(),
//            backgroundColor
//        )
//
//        binding.startButton.text = buttonText
//
//        if (backgroundColor == R.color.gray)
//            binding.startButton.isClickable = false
        this.updateStatus = update
        this.backgroundColor = backgroundColor
        this.buttonText = buttonText
    }

    fun setListener(listener: RecyclerView.Adapter<RecyclerView.ViewHolder>, position: Int) {
        this.listener = listener
        this.position = position
    }
}