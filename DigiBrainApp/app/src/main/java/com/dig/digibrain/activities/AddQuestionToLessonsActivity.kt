package com.dig.digibrain.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.adapters.ViewPagerAdapter
import com.dig.digibrain.databinding.ActivityAddQuestionToLessonsBinding
import com.dig.digibrain.fragments.ChooseLessonsFragment
import com.dig.digibrain.fragments.QuestionFragment
import com.dig.digibrain.fragments.ReviewAddedLessonsFragment
import com.dig.digibrain.models.subject.ClassModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.ReviewLessonsViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class AddQuestionToLessonsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddQuestionToLessonsBinding
    private lateinit var viewModel: ReviewLessonsViewModel
    private lateinit var sessionManager: SessionManager

    var questionId: Long? = null
    var questionText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQuestionToLessonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        sessionManager = SessionManager(applicationContext)

        val bundle = intent.extras
        if(bundle != null) {
            questionId = bundle.getLong("questionId")
            questionText = bundle.getString("questionText")
            binding.questionText.text = questionText
        } else {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
        }

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(ChooseLessonsFragment(), "Choose")
        adapter.addFragment(ReviewAddedLessonsFragment(), "Review")

        binding.viewPager.adapter = adapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[ReviewLessonsViewModel::class.java]
    }

    fun addSubject(subjectModel: SubjectModel, domainModel: DomainModel?, classModel: ClassModel) {
        val fragment = supportFragmentManager.findFragmentById(binding.viewPager.id) as ReviewAddedLessonsFragment
        fragment.addSubjectReview(subjectModel, domainModel, classModel)
    }

    fun addLessons(subjectIds: List<Long>) {
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(subjectIds.isEmpty()) {
            Toast.makeText(applicationContext, "Need to specify at least one subject", Toast.LENGTH_SHORT).show()
            return
        }

        if(authToken != null) {
            viewModel.addQuestionToSubjects(authToken, questionId!!, subjectIds)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                val intent = Intent(applicationContext, QuizActivity::class.java)
                                startActivity(intent)
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
}