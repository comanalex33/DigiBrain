package com.dig.digibrain.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityLearnBinding
import com.dig.digibrain.dialogs.ChooseClassDialog
import com.dig.digibrain.dialogs.ChooseDomainDialog
import com.dig.digibrain.dialogs.ChooseSubjectDialog
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.interfaces.ISubjectChanged
import com.dig.digibrain.models.subject.ClassModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnViewModel
import com.dig.digibrain.viewModels.LoginViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class LearnActivity : AppCompatActivity(), IClassChanged, IDomainChanged, ISubjectChanged {

    private lateinit var binding: ActivityLearnBinding
    private lateinit var viewModel: LearnViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedClass: Int? = null
    private var selectedClassModel: ClassModel? = null
    private var isUniversity: Boolean = false
    private var selectedDomain: DomainModel? = null
    private var selectedSubject: SubjectModel? = null

    private var domainClickable: Boolean = false
    private var subjectClickable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        sessionManager = SessionManager(applicationContext)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.chooseClassButton.setOnClickListener {
            val dialog = ChooseClassDialog(selectedClass, isUniversity)
            dialog.addListener(this)
            dialog.show(this.supportFragmentManager, "Choose class")
        }

        binding.chooseDomainButton.setOnClickListener {
            if(domainClickable) {
                val languageId = this.getSharedPreferences("application", Context.MODE_PRIVATE)
                    .getLong("languageId", 2)
                val dialog = ChooseDomainDialog(application, selectedDomain, selectedClass!!, isUniversity, languageId)
                dialog.addListener(this)
                dialog.setViewModel(viewModel)
                dialog.show(this.supportFragmentManager, "Choose domain")
            }
        }

        binding.chooseSubjectButton.setOnClickListener {
            if(subjectClickable) {
                selectedClass?.apply {
                    val dialog = ChooseSubjectDialog(application, sessionManager.getUserRole() == "admin" || sessionManager.getUserRole() == "teacher", selectedSubject, selectedClassModel, isUniversity)
                    dialog.addListener(this@LearnActivity)
                    dialog.setViewModel(viewModel)
                    dialog.show(this@LearnActivity.supportFragmentManager, "Choose subject")
                }
            }
        }

        binding.searchButton.setOnClickListener {
            if(selectedClass != null && selectedSubject != null) {
                val intent = Intent(this@LearnActivity, SubjectActivity::class.java)

                val bundle = Bundle()
                bundle.putLong("subjectId", selectedSubject!!.id)
                bundle.putString("subjectName", selectedSubject!!.name)
                bundle.putLong("subjectIconId", selectedSubject!!.iconId)
                bundle.putInt("classNumber", selectedClass!!)
                bundle.putBoolean("atUniversity", isUniversity)
                intent.putExtras(bundle)

                startActivity(intent)
            } else {
                // TODO - Message if not all the fields completed
            }
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

    override fun add() {
        val intent = Intent(this, AddSubjectActivity::class.java)

        val bundle = Bundle()
        bundle.putLong("classId", selectedClassModel!!.id)
        intent.putExtras(bundle)

        startActivity(intent)
        finish()
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
}