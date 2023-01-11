package com.dig.digibrain.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityQuizBinding
import com.dig.digibrain.dialogs.ChooseClassDialog
import com.dig.digibrain.dialogs.ChooseDomainDialog
import com.dig.digibrain.dialogs.ChooseSubjectDialog
import com.dig.digibrain.dialogs.ChooseTypeDialog
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.interfaces.IQuizTypeChanged
import com.dig.digibrain.interfaces.ISubjectChanged
import com.dig.digibrain.models.QuizTypeModel
import com.dig.digibrain.models.subject.ClassModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class QuizActivity : AppCompatActivity(), IClassChanged, IDomainChanged, ISubjectChanged, IQuizTypeChanged {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var viewModel: LearnViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedClass: Int? = null
    private var selectedClassModel: ClassModel? = null
    private var isUniversity: Boolean = false
    private var selectedDomain: DomainModel? = null
    private var selectedSubject: SubjectModel? = null
    private var selectedDifficulty: Int = 1
    private var selectedQuizType: QuizTypeModel? = null

    private var domainClickable: Boolean = false
    private var subjectClickable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        binding.backArrow.setOnClickListener {
            finish()
        }
        sessionManager = SessionManager(applicationContext)

        binding.chooseClassButton.setOnClickListener {
            val dialog = ChooseClassDialog(selectedClass, isUniversity)
            dialog.addListener(this)
            dialog.show(this.supportFragmentManager, "Choose class")
        }

        binding.chooseDomainButton.setOnClickListener {
            if(domainClickable) {
                val dialog = ChooseDomainDialog(application, selectedDomain, selectedClass!!, isUniversity, 2)
                dialog.addListener(this)
                dialog.setViewModel(viewModel)
                dialog.show(this.supportFragmentManager, "Choose domain")
            }
        }

        binding.chooseSubjectButton.setOnClickListener {
            if(subjectClickable) {
                selectedClass?.apply {
                    val dialog = ChooseSubjectDialog(application, sessionManager.getUserRole() == "admin" || sessionManager.getUserRole() == "teacher",  selectedSubject, selectedClassModel, isUniversity)
                    dialog.addListener(this@QuizActivity)
                    dialog.setViewModel(viewModel)
                    dialog.show(this@QuizActivity.supportFragmentManager, "Choose subject")
                }
            }
        }

        binding.searchButton.setOnClickListener {
            if(selectedClass != null && selectedSubject != null && selectedQuizType != null) {
                val intent = Intent(this, QuestionActivity::class.java)

                val bundle = Bundle()
                bundle.putString("difficulty", getDifficulty())
                bundle.putString("type", selectedQuizType!!.key)
                intent.putExtras(bundle)

                startActivity(intent)
            } else {
                // TODO - Message if not all the fields completed
            }
        }

        binding.chooseDifficultySeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                selectedDifficulty = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.chooseTypeButton.setOnClickListener {
            val dialog = ChooseTypeDialog(application, selectedQuizType)
            dialog.addListener(this)
            dialog.show(this.supportFragmentManager, "Choose type")
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[LearnViewModel::class.java]
    }

    override fun changeClass(classNumber: Int, isUniversity: Boolean) {
        this.selectedClass = classNumber
        this.isUniversity = isUniversity

        // Update text fields
        binding.chooseClassText.text = "${resources.getString(R.string.class_)}:"
        binding.chooseClassValue.text = classNumber.toString()

        // Change Domain visibility (only visible if High-school or University)
        if(isUniversity) {
            binding.chooseDomainButton.visibility = View.VISIBLE

            // Disable subject selection
            subjectClickable = false
            setViewClickable(binding.chooseSubjectButton, false)
        } else if (classNumber > 8) {
            binding.chooseDomainButton.visibility = View.VISIBLE

            // Disable subject selection
            subjectClickable = false
            setViewClickable(binding.chooseSubjectButton, false)
        } else {
            binding.chooseDomainButton.visibility = View.GONE

            // Enable subject selection
            subjectClickable = true
            setViewClickable(binding.chooseSubjectButton, true)
            getClassByNumberAndDomain()
        }

        // Enable domain selection
        domainClickable = true
        setViewClickable(binding.chooseDomainButton, true)

        // Initialize domain text fields
        binding.chooseDomainText.text = resources.getString(R.string.choose_domain)
        binding.chooseDomainValue.text = ""
        selectedDomain = null

        // Initialize subject text fields
        binding.chooseSubjectText.text = resources.getString(R.string.choose_subject)
        binding.chooseSubjectValue.text = ""
        selectedSubject = null
    }

    override fun changeDomain(value: DomainModel) {
        selectedDomain = value

        // Enable subject selection
        subjectClickable = true
        setViewClickable(binding.chooseSubjectButton, true)

        // Update text fields
        binding.chooseDomainText.text = "${resources.getString(R.string.domain)}:"
        binding.chooseDomainValue.text = value.name

        // Initialize subject text fields
        binding.chooseSubjectText.text = resources.getString(R.string.choose_subject)
        binding.chooseSubjectValue.text = ""
        selectedSubject = null

        getClassByNumberAndDomain()
    }

    override fun disableErrorMessage() {}
    override fun addSubject() {
        TODO("Not yet implemented")
    }

    override fun changeSubject(value: SubjectModel) {
        selectedSubject = value

        // Update text fields
        binding.chooseSubjectText.text = "${resources.getString(R.string.subject)}:"
        binding.chooseSubjectValue.text = value.name
    }

    private fun setViewClickable(view: View, isClickable: Boolean) {
        if(isClickable) {
            view.backgroundTintList = AppCompatResources.getColorStateList(
                applicationContext,
                R.color.gray
            )
        } else {
            view.backgroundTintList = AppCompatResources.getColorStateList(
                applicationContext,
                R.color.light_gray
            )
        }
    }

    private fun getClassByNumberAndDomain() {
        if(selectedDomain != null) {
            viewModel.getClassByNumberAndDomain(selectedClass!!, isUniversity, selectedDomain!!.id)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                selectedClassModel = resource.data
                            }
                            Status.ERROR -> {}
                            Status.LOADING -> {}
                        }
                    }
                }
        } else {
            viewModel.getClassByNumberAndDomain(selectedClass!!, isUniversity, 0)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                selectedClassModel = resource.data
                            }
                            Status.ERROR -> {}
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }

    override fun changeQuizType(value: QuizTypeModel) {
        selectedQuizType = value

        binding.chooseTypeText.text = "${resources.getString(R.string.type)}:"
        binding.chooseTypeValue.text = value.name
    }

    private fun getDifficulty(): String {
        if(selectedDifficulty == 0)
            return "Easy"
        if(selectedDifficulty == 1)
            return "Medium"
        return "Hard"
    }
}