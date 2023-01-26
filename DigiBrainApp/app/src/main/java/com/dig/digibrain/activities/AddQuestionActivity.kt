package com.dig.digibrain.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityAddQuestionBinding
import com.dig.digibrain.dialogs.ChooseTypeDialog
import com.dig.digibrain.interfaces.IQuizTypeChanged
import com.dig.digibrain.models.QuizTypeModel
import com.dig.digibrain.models.postModels.quiz.AnswerPostModel
import com.dig.digibrain.models.postModels.quiz.QuestionPostModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Helper.Companion.countMatches
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.AddQuestionViewModel
import com.dig.digibrain.viewModels.AddSubjectViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class AddQuestionActivity : AppCompatActivity(), IQuizTypeChanged {

    private lateinit var binding: ActivityAddQuestionBinding
    private lateinit var viewModel: AddQuestionViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedQuizType: QuizTypeModel? = null
    private var selectedDifficulty: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        sessionManager = SessionManager(applicationContext)

        binding.chooseTypeButton.setOnClickListener {
            val dialog = ChooseTypeDialog(application, selectedQuizType)
            dialog.addListener(this)
            dialog.show(this.supportFragmentManager, "Choose type")
        }

        binding.chooseDifficultySeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                selectedDifficulty = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.addQuestionButton.setOnClickListener {
            if(selectedQuizType != null) {
                when (selectedQuizType!!.key) {
                    "MultipleChoice" -> {
                        handleMultipleChoice()
                    }
                    "WordsGap" -> {
                        handleWordsGap()
                    }
                    "TrueFalse" -> {
                        handleTrueFalse()
                    }
                    else -> {
                        Toast.makeText(applicationContext, "Wrong type", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.trueAnswerCorrect.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                binding.falseAnswerCorrect.isChecked = false
            }
        }

        binding.falseAnswerCorrect.setOnCheckedChangeListener { compoundButton, b ->
            if(b) {
                binding.trueAnswerCorrect.isChecked = false
            }
        }

        binding.backArrow.setOnClickListener {
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[AddQuestionViewModel::class.java]
    }

    private fun handleWordsGap() {
        // Check question is completed
        if(binding.questionText.text.toString() == "") {
            Toast.makeText(applicationContext, "Add question", Toast.LENGTH_SHORT).show()
            return
        }

        val positionCount = binding.questionText.text.toString().countMatches("__")
        if(positionCount > 4) {
            Toast.makeText(applicationContext, "Too many spaces for answers, try to reduce them", Toast.LENGTH_SHORT).show()
            return
        }

        if(positionCount >= 1) {
            if(binding.wordsGapAnswer1.text.toString() == "") {
                Toast.makeText(applicationContext, "Must to have $positionCount positional answers for this question", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(positionCount >= 2) {
            if(binding.wordsGapAnswer2.text.toString() == "") {
                Toast.makeText(applicationContext, "Must to have $positionCount positional answers for this question", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(positionCount >= 3) {
            if(binding.wordsGapAnswer3.text.toString() == "") {
                Toast.makeText(applicationContext, "Must to have $positionCount positional answers for this question", Toast.LENGTH_SHORT).show()
                return
            }
        }
        if(positionCount >= 4) {
            if(binding.wordsGapAnswer4.text.toString() == "") {
                Toast.makeText(applicationContext, "Must to have $positionCount positional answers for this question", Toast.LENGTH_SHORT).show()
                return
            }
        }

        createQuestion()
    }

    private fun handleMultipleChoice() {
        // Check question is completed
        if(binding.questionText.text.toString() == "") {
            Toast.makeText(applicationContext, "Add question text", Toast.LENGTH_SHORT).show()
            return
        }

        // Check answers are completed
        if(binding.multipleChoiceAnswer1.text.toString() == "") {
            Toast.makeText(applicationContext, "Answer 1 not completed", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.multipleChoiceAnswer2.text.toString() == "") {
            Toast.makeText(applicationContext, "Answer 2 not completed", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.multipleChoiceAnswer3.text.toString() == "") {
            Toast.makeText(applicationContext, "Answer 3 not completed", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.multipleChoiceAnswer4.text.toString() == "") {
            Toast.makeText(applicationContext, "Answer 4 not completed", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if at least one answer is set to true
        if(!binding.multipleChoiceAnswer1Correct.isChecked && !binding.multipleChoiceAnswer2Correct.isChecked
            && !binding.multipleChoiceAnswer3Correct.isChecked && !binding.multipleChoiceAnswer4Correct.isChecked) {
            Toast.makeText(applicationContext, "At least one answer have to be correct", Toast.LENGTH_SHORT).show()
            return
        }

        createQuestion()
    }

    private fun handleTrueFalse() {
        // Check question is completed
        if(binding.questionText.text.toString() == "") {
            Toast.makeText(applicationContext, "Add question text", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if answer is selected
        if(!binding.trueAnswerCorrect.isChecked && !binding.falseAnswerCorrect.isChecked) {
            Toast.makeText(applicationContext, "Must select true or false", Toast.LENGTH_SHORT).show()
            return
        }

        createQuestion()
    }

    private fun createQuestion() {
        val languageId = this.getSharedPreferences("application", Context.MODE_PRIVATE)
            .getLong("languageId", 2)
        val questionPostModel = QuestionPostModel(
            type = selectedQuizType!!.key,
            difficulty = getDifficulty(),
            text = binding.questionText.text.toString(),
            languageId = languageId
        )
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(authToken != null) {
            viewModel.createQuestion(authToken, questionPostModel)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                if(resource.data != null) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Question added",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    addAnswers(resource.data.id)
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }

    private fun addAnswers(questionId: Long) {
        var answers: MutableList<AnswerPostModel>? = null

        when (selectedQuizType!!.key) {
            "MultipleChoice" -> {
                val answer1 = AnswerPostModel(binding.multipleChoiceAnswer1.text.toString(), 0, binding.multipleChoiceAnswer1Correct.isChecked, questionId)
                val answer2 = AnswerPostModel(binding.multipleChoiceAnswer2.text.toString(), 0, binding.multipleChoiceAnswer2Correct.isChecked, questionId)
                val answer3 = AnswerPostModel(binding.multipleChoiceAnswer3.text.toString(), 0, binding.multipleChoiceAnswer3Correct.isChecked, questionId)
                val answer4 = AnswerPostModel(binding.multipleChoiceAnswer4.text.toString(), 0, binding.multipleChoiceAnswer4Correct.isChecked, questionId)
                answers = mutableListOf(answer1, answer2, answer3, answer4)
            }
            "WordsGap" -> {
                val positionCount = binding.questionText.text.toString().countMatches("__")
                if(positionCount >= 1)
                    answers = mutableListOf()
                    answers!!.add(AnswerPostModel(binding.wordsGapAnswer1.text.toString(), 1, true, questionId))
                if(positionCount >= 2)
                    answers.add(AnswerPostModel(binding.wordsGapAnswer2.text.toString(), 2, true, questionId))
                if(positionCount >= 3)
                    answers.add(AnswerPostModel(binding.wordsGapAnswer3.text.toString(), 3, true, questionId))
                if(positionCount >= 4)
                    answers.add(AnswerPostModel(binding.wordsGapAnswer4.text.toString(), 4, true, questionId))
            }
            "TrueFalse" -> {
                val trueAnswer = AnswerPostModel(resources.getString(R.string.trueWord), 0, binding.trueAnswerCorrect.isChecked, questionId)
                val falseAnswer = AnswerPostModel(resources.getString(R.string.falseWord), 0, binding.falseAnswerCorrect.isChecked, questionId)
                answers = mutableListOf(trueAnswer, falseAnswer)
            }
            else -> {
                Toast.makeText(applicationContext, "Wrong type", Toast.LENGTH_SHORT).show()
            }
        }

        val authToken: String? = sessionManager.getBearerAuthToken()
        if(authToken != null && answers != null) {
            viewModel.addMultipleAnswers(authToken, answers)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                if(resource.data != null) {
                                    Toast.makeText(applicationContext, "Answers added", Toast.LENGTH_SHORT).show()
                                    addRelevantSubjects(questionId, binding.questionText.text.toString())
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }

    private fun addRelevantSubjects(questionId: Long, questionText: String) {
        val intent = Intent(applicationContext, AddQuestionToLessonsActivity::class.java)

        val bundle = Bundle()
        bundle.putLong("questionId", questionId)
        bundle.putString("questionText", questionText)
        intent.putExtras(bundle)

        startActivity(intent)
        finish()
    }

    override fun changeQuizType(value: QuizTypeModel) {
        selectedQuizType = value

        binding.chooseTypeText.text = "${resources.getString(R.string.type)}:"
        binding.chooseTypeValue.text = value.name

        binding.addQuestionButton.visibility = View.VISIBLE

        when(value.key) {
            "MultipleChoice" -> {
                binding.multipleChoiceAnswers.visibility = View.VISIBLE
                binding.wordsGapAnswers.visibility = View.GONE
                binding.trueFalseAnswers.visibility = View.GONE
            }
            "WordsGap" -> {
                binding.multipleChoiceAnswers.visibility = View.GONE
                binding.wordsGapAnswers.visibility = View.VISIBLE
                binding.trueFalseAnswers.visibility = View.GONE
            }
            "TrueFalse" -> {
                binding.multipleChoiceAnswers.visibility = View.GONE
                binding.wordsGapAnswers.visibility = View.GONE
                binding.trueFalseAnswers.visibility = View.VISIBLE
            }
            else -> {
                Toast.makeText(applicationContext, "Wrong type", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDifficulty(): String {
        if(selectedDifficulty == 0)
            return "Easy"
        if(selectedDifficulty == 1)
            return "Medium"
        return "Hard"
    }
}