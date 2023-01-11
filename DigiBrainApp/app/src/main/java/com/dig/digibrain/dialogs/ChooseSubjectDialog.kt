package com.dig.digibrain.dialogs

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.adapters.SpacingItemDecorator
import com.dig.digibrain.adapters.SubjectAdapter
import com.dig.digibrain.databinding.DialogChooseSubjectBinding
import com.dig.digibrain.interfaces.ISubjectChanged
import com.dig.digibrain.models.subject.ClassModel
import com.dig.digibrain.models.subject.SubjectModel
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnViewModel

class ChooseSubjectDialog(
    var application: Application,
    var modifyCapabilities: Boolean,
    var currentSubject: SubjectModel?,
    var classModel: ClassModel?,
    var atUniversity: Boolean): DialogFragment(), ISubjectChanged {

    private lateinit var binding: DialogChooseSubjectBinding
    private lateinit var listener: ISubjectChanged
    private lateinit var viewModel: LearnViewModel

    private lateinit var subjectAdapter: SubjectAdapter
    private var selectedSubject: SubjectModel? = null
    private var subjectsUpdated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChooseSubjectBinding.inflate(layoutInflater)

        selectedSubject = currentSubject

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        updateSubjects()

        binding.selectButton.setOnClickListener {
            if(subjectsUpdated) {
                if (selectedSubject == null) {
                    binding.errorMessage.text = resources.getString(R.string.select_subject)
                    binding.errorMessage.visibility = View.VISIBLE
                } else {
                    listener.changeSubject(selectedSubject!!)
                    dialog!!.dismiss()
                }
            } else {
                dialog!!.dismiss()
            }
        }

        binding.addButton.visibility = if(modifyCapabilities) View.VISIBLE else View.GONE
        binding.addButton.setOnClickListener {
            listener.add()
            dialog!!.dismiss()
        }

        return binding.root
    }

    override fun changeSubject(value: SubjectModel) {
        selectedSubject = value
    }

    override fun disableErrorMessage() {
        binding.errorMessage.visibility = View.GONE
    }

    override fun add() {}

    fun addListener(listener: ISubjectChanged) {
        this.listener = listener
    }

    fun setViewModel(viewModel: LearnViewModel) {
        this.viewModel = viewModel
    }

    private fun setSubjects(subjects: List<SubjectModel>) {
        subjectAdapter = SubjectAdapter(
            application.applicationContext,
            application,
            this,
            currentSubject,
            subjects
        )
        binding.recyclerView.layoutManager = GridLayoutManager(activity, 2)
        binding.recyclerView.addItemDecoration(SpacingItemDecorator(30, 10))
        binding.recyclerView.adapter = subjectAdapter
    }

    private fun setMessageForNoSubjects(message: String) {
        binding.errorMessage.text = message
        binding.errorMessage.visibility = View.VISIBLE
        binding.selectButton.text = "Ok"
    }

    private fun updateSubjects() {

        if(classModel != null) {
            viewModel.getSubjectsForClass(classModel!!.id).observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data != null) {
                                if(resource.data.isNotEmpty()) {
                                    subjectsUpdated = true
                                    setSubjects(resource.data)
                                } else {
                                    setMessageForNoSubjects(resources.getString(R.string.no_subjects_defined_for_this_class))
                                }
                            } else {
                                setMessageForNoSubjects(resources.getString(R.string.no_subjects_defined_for_this_class))
                            }
                        }
                        Status.ERROR -> {
                            setMessageForNoSubjects(resource.message!!)
                        }
                        Status.LOADING -> {
                            //TODO - Loading dialog
                        }
                    }
                }
            }
        } else {
            setMessageForNoSubjects(resources.getString(R.string.no_subjects_defined_for_this_class))
        }
    }
}