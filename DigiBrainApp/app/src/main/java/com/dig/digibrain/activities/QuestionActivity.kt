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
import com.dig.digibrain.dialogs.LoadingDialog
import com.dig.digibrain.dialogs.QuizResultsDialog
import com.dig.digibrain.fragments.MultipleChoiceQuestionFragment
import com.dig.digibrain.fragments.QuestionFragment
import com.dig.digibrain.fragments.WordsGapFragment
import com.dig.digibrain.interfaces.ItemClickListener
import com.dig.digibrain.models.learnPaths.LearnPathQuizStatusUpdateModel
import com.dig.digibrain.models.learnPaths.LearnPathStatusUpdateModel
import com.dig.digibrain.models.postModels.quiz.QuizReportPostModel
import com.dig.digibrain.models.quiz.AnswerModel
import com.dig.digibrain.models.quiz.QuestionAnswerModel
import com.dig.digibrain.models.quiz.QuestionModel
import com.dig.digibrain.models.quiz.QuestionsAnswersList
import com.dig.digibrain.objects.LearnPathLocalStatus
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
    var questionIds: List<Long>? = null

    var updateStatus = false

    var nextQuestion = false
    var questionsNumber = 5

    var score: Double = 0.0
    var totalScore: Int? = null
    var learnPathId: Long? = null

    private val quizMinutes: Long = 4
    private val quizSeconds: Long = 0

    private var minutesLeft: Long = 4
    private var secondsLeft: Long = 0

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
            val questionsLongArray = bundle.getLongArray("questionIds")
            questionsLongArray?.apply {
                questionIds = this.toList()
            }
            if(bundle.containsKey("updateStatus")) {
                updateStatus = bundle.getBoolean("updateStatus")
                Toast.makeText(applicationContext, "Status $updateStatus", Toast.LENGTH_SHORT).show()
            }
            totalScore = bundle.getInt("score")
            learnPathId = bundle.getLong("learnPathId")
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

        if (questionIds != null) {
            getQuestionsForIds()
        } else if(subjectId != null && difficulty != null && type != null) {
            getQuizQuestions(questionsNumber)
        }

        binding.nextButton.setOnClickListener {
            if(nextQuestion) {
                val fragment = supportFragmentManager.findFragmentById(binding.containerFragment.id) as QuestionFragment
                score += fragment.getScore()
                score = (score * 100).toInt() / 100.0
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
        val loadingDialog = LoadingDialog(this)
        viewModel.getRandomQuestionsForSubject(subjectId!!, number, difficulty!!, type!!, 2)
            .observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            if(resource.data != null) {
                                getAnswers(resource.data)
                            }
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        Status.LOADING -> {
                            loadingDialog.show()
                        }
                    }
                }
            }
    }

    private fun getQuestionsForIds() {
        val loadingDialog = LoadingDialog(this)
        viewModel.getQuestionsForIds(questionIds!!)
            .observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            if(resource.data != null) {
                                Toast.makeText(applicationContext, "Questions received", Toast.LENGTH_SHORT).show()
                                getAnswers(resource.data)
                            }
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                            Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        Status.LOADING -> {
                            loadingDialog.show()
                        }
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
            val f = DecimalFormat("00")
            val totalTime = "${f.format(quizMinutes)}:${f.format(quizSeconds)}"
            dialog = QuizResultsDialog(this, false, questionsAnswers.size, timeTaken(), score)
            dialog.show(this.supportFragmentManager, "Quiz results")
            currentQuestionPosition++
        } else {
            binding.score.text = score.toString()
            binding.questionNumber.text = "${selectedAnswers.size + 1}/${questionsAnswers.size}"
            val question = questionsAnswers[currentQuestionPosition].question

            if(question.type == "MultipleChoice" || question.type == "TrueFalse") {
                if(question.type == "MultipleChoice")
                    questionsAnswers[currentQuestionPosition].answers = questionsAnswers[currentQuestionPosition].answers.shuffled()
                supportFragmentManager.beginTransaction()
                    .replace(
                        binding.containerFragment.id, MultipleChoiceQuestionFragment(
                            questionsAnswers[currentQuestionPosition].question,
                            questionsAnswers[currentQuestionPosition].answers
                        )
                    )
                    .commit()
            } else if(question.type == "WordsGap") {
                questionsAnswers[currentQuestionPosition].answers = questionsAnswers[currentQuestionPosition].answers.shuffled()
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
        object : CountDownTimer(quizMinutes * 60 * 1000 + quizSeconds * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(currentQuestionPosition == questionsAnswers.size + 1) {
                    cancel()
                }
                val f = DecimalFormat("00")
                val min = (millisUntilFinished / 60000) % 60;
                val sec = (millisUntilFinished / 1000) % 60;
                minutesLeft = min
                secondsLeft = sec
                binding.quizTime.text = "${f.format(min)}:${f.format(sec)}"
            }

            override fun onFinish() {
                binding.quizTime.text = "00:00"
                val f = DecimalFormat("00")
                val totalTime = "${f.format(quizMinutes)}:${f.format(quizSeconds)}"
                dialog = QuizResultsDialog(this@QuestionActivity, true, questionsAnswers.size, timeTaken(), score)
                dialog.show(this@QuestionActivity.supportFragmentManager, "Quiz results")
            }
        }.start()
    }

    override fun onClick(name: String) {
        dialog.dismiss()

        if(type != null && subjectId != null && difficulty != null) {
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
        } else {
            if(updateStatus) {
                LearnPathLocalStatus.done = true
            }
            updateQuizStatus()
        }
    }

    private fun timeTaken(): String {
        val minutesTaken: Long
        val secondsTaken: Long

        if(secondsLeft != 0L) {
            minutesTaken = quizMinutes - minutesLeft - 1
            secondsTaken = 60L - secondsLeft
        } else {
            minutesTaken = quizMinutes - minutesLeft
            secondsTaken = 0L
        }
        val f = DecimalFormat("00")
        return "${f.format(minutesTaken)}:${f.format(secondsTaken)}"
    }

    private fun updateQuizStatus() {
        totalScore?.apply {
            val scoreToUpdate = (score / questionsAnswers.size.toDouble()) * this

            val authToken: String? = sessionManager.getBearerAuthToken()
            val username: String? = sessionManager.getUserName()
            val model = LearnPathQuizStatusUpdateModel(
                sectionNumber = LearnPathLocalStatus.currentSectionNumber!!.toLong(),
                lessonNumber = LearnPathLocalStatus.currentLessonNumber!!.toLong(),
                score = scoreToUpdate.toLong()
            )
            if(authToken != null && username != null && learnPathId != null) {
                viewModel.updateLearnPathQuizStatus(authToken, learnPathId!!, username, model)
                    .observe(this@QuestionActivity) {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    Toast.makeText(applicationContext, "Updated quiz status", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                Status.ERROR -> {
                                    Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                Status.LOADING -> {}
                            }
                        }
                    }
            }
        }
    }
}