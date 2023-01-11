package com.dig.digibrain.activities

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.liveData
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityQuestionBinding
import com.dig.digibrain.dialogs.QuizResultsDialog
import com.dig.digibrain.fragments.MultipleChoiceQuestionFragment
import com.dig.digibrain.fragments.QuestionFragment
import com.dig.digibrain.fragments.WordsGapFragment
import com.dig.digibrain.interfaces.ItemClickListener
import com.dig.digibrain.models.postModels.quiz.QuizReportPostModel
import com.dig.digibrain.models.quiz.AnswerModel
import com.dig.digibrain.models.quiz.QuestionAnswerModel
import com.dig.digibrain.models.quiz.QuestionModel
import com.dig.digibrain.models.quiz.QuestionsAnswersList
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.QuestionViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.text.NumberFormat

class QuestionActivity : AppCompatActivity(), ItemClickListener {

    private lateinit var binding: ActivityQuestionBinding
    private lateinit var viewModel: QuestionViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var dialog: QuizResultsDialog

    var difficulty: String? = null
    var subjectId: Long? = null
    var type: String? = null
    var nextQuestion = false
    var questionsNumber = 2

    var score: Double = 0.0

    private val quizMinutes: Long = 4
    private val quizSeconds: Long = 0

    var questionsAnswers = listOf<QuestionsAnswersList>()
    var selectedAnswers = mutableListOf<List<AnswerModel>>()
    var currentQuestionPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(applicationContext)
        setupViewModel()

        val bundle = intent.extras
        if(bundle != null) {
            subjectId = bundle.getLong("subjectId")
            difficulty = bundle.getString("difficulty")
            type = bundle.getString("type")
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

        if(subjectId != null && difficulty != null && type != null) {
            getQuizQuestions(questionsNumber)
        }

        binding.nextButton.setOnClickListener {
            if(nextQuestion) {
                val fragment = supportFragmentManager.findFragmentById(binding.containerFragment.id) as QuestionFragment
                score += fragment.getScore()
                updateQuestion()
                binding.nextButton.text = applicationContext.getString(R.string.check)
                nextQuestion = false
            } else {
                val fragment = supportFragmentManager.findFragmentById(binding.containerFragment.id) as QuestionFragment
                if (fragment.answerQuestion()) {

                    selectedAnswers.add(fragment.getSelectedAnswers())
                    for(list in selectedAnswers) {
                        for(element in list) {
                            println("${element.text} - ${element.correct}")
                        }
                    }
//
//                    lifecycleScope.launch {
//                        withContext(Dispatchers.Main) {
//                            val selectedAnswers = fragment.getSelectedAnswers()
//                            // Need to wait a bit for the fragment to return the selected answers
//                            delay(200)
//                            createQuiz(questionsAnswers[currentQuestionPosition - 1].question, selectedAnswers)
//                        }
//                    }


                    binding.nextButton.text = applicationContext.getString(R.string.next)
                    nextQuestion = true
                } else {
                    Toast.makeText(applicationContext, "Choose an answer", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[QuestionViewModel::class.java]
    }
    private fun getQuizQuestions(number: Int) {
        viewModel.getRandomQuestionsForSubject(subjectId!!, number, difficulty!!, type!!, 2)
            .observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data != null) {
                                getAnswers(resource.data)
                            }
                        }
                        Status.ERROR -> { }
                        Status.LOADING -> { }
                    }
                }
            }
    }

    private fun getAnswers(questions: List<QuestionModel>) {
        val questionAnswersList = mutableListOf<QuestionsAnswersList>()
        var questionsResolved = 0

        for(question in questions) {
            viewModel.getQuestionAnswers(question.id)
                .observe(this) {
                    it.let { resource ->
                        when(resource.status) {
                            Status.SUCCESS -> {
                                questionsResolved++
                                if(resource.data != null) {
                                    questionAnswersList.add(
                                        QuestionsAnswersList(
                                            question,
                                            resource.data
                                        )
                                    )
                                }

                                if(questionsResolved == questions.size) {
                                    this.questionsAnswers = questionAnswersList
                                    binding.score.text = "0.0"
                                    binding.questionNumber.text = "1/${questionsAnswers.size}"
                                    updateQuestion()
                                    startTimer()
                                }
                            }
                            Status.ERROR -> {
                                questionsResolved++
                            }
                            Status.LOADING -> { }
                        }
                    }
                }
        }
    }

//    private fun createQuiz(question: QuestionModel, answers: List<AnswerModel>) {
//        if(quizId == 0L) {
//            val username = SessionManager(applicationContext).getUserName()
//            if (username != null) {
//                viewModel.createQuizForUser(username)
//                    .observe(this) {
//                        it.let { resource ->
//                            when (resource.status) {
//                                Status.SUCCESS -> {
//                                    if (resource.data != null) {
//                                        quizId = resource.data.id
//                                        addQuestionToQuiz(question, answers)
//                                    }
//                                }
//                                Status.ERROR -> {}
//                                Status.LOADING -> {}
//                            }
//                        }
//                    }
//            }
//        } else {
//            addQuestionToQuiz(question, answers)
//        }
//    }

//    private fun addQuestionToQuiz(question: QuestionModel, answers: List<AnswerModel>) {
//        //Toast.makeText(applicationContext, "Adding question ${answers.size}", Toast.LENGTH_SHORT).show()
//        for(answer in answers) {
//            val model = QuestionAnswerModel(quizId, question.id, answer.id)
//            viewModel.addQuestionToQuiz(model)
//                .observe(this) {
//                    it.let { resource ->
//                        when (resource.status) {
//                            Status.SUCCESS -> {}
//                            Status.ERROR -> {}
//                            Status.LOADING -> {}
//                        }
//                    }
//                }
//        }
//    }

    private fun updateQuestion() {
        if(currentQuestionPosition == questionsAnswers.size) {
            dialog = QuizResultsDialog(this, applicationContext.getString(R.string.congratulations), questionsAnswers.size, "${quizMinutes}:${quizSeconds}", score)
            dialog.show(this.supportFragmentManager, "Quiz results")
            currentQuestionPosition++
        } else {
            binding.score.text = score.toString()
            binding.questionNumber.text = "${selectedAnswers.size + 1}/${questionsAnswers.size}"

            if(type == "MultipleChoice") {
                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.containerFragment.id, MultipleChoiceQuestionFragment(
                            questionsAnswers[currentQuestionPosition].question,
                            questionsAnswers[currentQuestionPosition].answers
                        )
                    )
                    .commit()
            } else if(type == "WordsGap") {
                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.containerFragment.id, WordsGapFragment(
                            questionsAnswers[currentQuestionPosition].question,
                            questionsAnswers[currentQuestionPosition].answers
                        )
                    )
                    .commit()
            }
            currentQuestionPosition++
        }
    }

    private fun startTimer() {
        object : CountDownTimer(quizMinutes * 60 * 100 + quizSeconds * 100, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(currentQuestionPosition == questionsAnswers.size + 1) {
                    cancel()
                }
                val f = DecimalFormat("00")
                val min = (millisUntilFinished / 60000) % 60;
                val sec = (millisUntilFinished / 1000) % 60;
                binding.quizTime.text = "${f.format(min)}:${f.format(sec)}"
            }

            override fun onFinish() {
                binding.quizTime.text = "00:00"
                dialog = QuizResultsDialog(this@QuestionActivity, applicationContext.getString(R.string.timeout), questionsAnswers.size, "${quizMinutes}:${quizSeconds}", score)
                dialog.show(this@QuestionActivity.supportFragmentManager, "Quiz results")
            }
        }.start()
    }

    override fun onClick(name: String) {
        dialog.dismiss()

        val model = QuizReportPostModel(
            username = sessionManager.getUserName()!!,
            quizType = type!!,
            score = score,
            numberOfQuestions = questionsNumber,
            subjectId = subjectId!!,
            difficulty = difficulty!!
        )
        viewModel.addUserReport(model)
            .observe(this) {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                finish()
                            }
                            Status.ERROR -> {}
                            Status.LOADING -> {}
                        }
                    }
                }
    }
}