package com.dig.digibrain.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.R
import com.dig.digibrain.activities.AddQuestionToLessonsActivity
import com.dig.digibrain.databinding.FragmentChooseLessonsBinding
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
import com.dig.digibrain.viewModels.ViewModelFactory

class ChooseLessonsFragment : Fragment(), IClassChanged, IDomainChanged, ISubjectChanged {

    private lateinit var binding: FragmentChooseLessonsBinding
    private lateinit var viewModel: LearnViewModel
    private lateinit var sessionManager: SessionManager

    private var selectedClass: Int? = null
    private var selectedClassModel: ClassModel? = null
    private var isUniversity: Boolean = false
    private var selectedDomain: DomainModel? = null
    private var selectedSubject: SubjectModel? = null

    private var domainClickable: Boolean = false
    private var subjectClickable: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChooseLessonsBinding.inflate(layoutInflater)

        setupViewModel()
        sessionManager = SessionManager(requireContext())

        binding.chooseClassButton.setOnClickListener {
            val dialog = ChooseClassDialog(selectedClass, isUniversity)
            dialog.addListener(this)
            dialog.show(this.childFragmentManager, "Choose class")
        }

        binding.chooseDomainButton.setOnClickListener {
            if(domainClickable) {
                val dialog = ChooseDomainDialog(requireActivity().application, selectedDomain, selectedClass!!, isUniversity, 2)
                dialog.addListener(this)
                dialog.setViewModel(viewModel)
                dialog.show(this.childFragmentManager, "Choose domain")
            }
        }

        binding.chooseSubjectButton.setOnClickListener {
            if(subjectClickable) {
                selectedClass?.apply {
                    val dialog = ChooseSubjectDialog(requireActivity().application, false, selectedSubject, selectedClassModel, isUniversity)
                    dialog.addListener(this@ChooseLessonsFragment)
                    dialog.setViewModel(viewModel)
                    dialog.show(this@ChooseLessonsFragment.childFragmentManager, "Choose subject")
                }
            }
        }

        binding.addButton.setOnClickListener {
            selectedSubject?.apply {
                (activity as AddQuestionToLessonsActivity).addSubject(selectedSubject!!, selectedDomain, selectedClassModel!!)
                Toast.makeText(requireContext(), "Added", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
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

    private fun setViewClickable(view: View, isClickable: Boolean) {
        if(isClickable) {
            view.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
                R.color.gray
            )
        } else {
            view.backgroundTintList = AppCompatResources.getColorStateList(
                requireContext(),
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

    override fun changeSubject(value: SubjectModel) {
        selectedSubject = value

        // Update text fields
        binding.chooseSubjectText.text = "${resources.getString(R.string.subject)}:"
        binding.chooseSubjectValue.text = value.name
    }

    override fun disableErrorMessage() {
        TODO("Not yet implemented")
    }

    override fun add() {
        TODO("Not yet implemented")
    }
}