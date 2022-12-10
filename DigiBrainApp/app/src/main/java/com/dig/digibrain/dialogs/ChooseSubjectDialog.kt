package com.dig.digibrain.dialogs

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.activities.LearnActivity
import com.dig.digibrain.adapters.DomainAdapter
import com.dig.digibrain.adapters.SpacingItemDecorator
import com.dig.digibrain.adapters.SubjectAdapter
import com.dig.digibrain.databinding.DialogChooseDomainBinding
import com.dig.digibrain.databinding.DialogChooseSubjectBinding
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.interfaces.ISubjectChanged
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel

class ChooseSubjectDialog(
    var application: Application,
    var currentSubject: SubjectModel?,
    var selectedDomain: DomainModel?,
    var classNumber: Int,
    var atUniversity: Boolean): DialogFragment(), ISubjectChanged {

    private lateinit var binding: DialogChooseSubjectBinding

    private lateinit var subjectAdapter: SubjectAdapter
    private var selectedSubject: SubjectModel? = null

    private lateinit var listener: ISubjectChanged

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChooseSubjectBinding.inflate(layoutInflater)

        selectedSubject = currentSubject

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val subjects = listOf(
            SubjectModel(1, "Subject 1", 5, 1, 1, false),
            SubjectModel(2, "Subject 2", 57, 2, 1, false),
            SubjectModel(3, "Subject 3", 8, 1, 12, false),
            SubjectModel(4, "Subject 4", 5, 4, 12, false),
            SubjectModel(5, "Subject 5", 57, 1, 1, true),
            SubjectModel(6, "Subject 6", 8, 2, 1, true),
//            SubjectModel(7, "Domain 1", 5, 3),
//            SubjectModel(8, "Domain 2", 57, 1),
//            SubjectModel(9, "Domain 3", 8, 2)
        )

        val subjectsForRecyclerView = getSubjectsForRecyclerView(subjects)

        if(subjectsForRecyclerView.isNotEmpty()) {
            subjectAdapter = SubjectAdapter(
                activity as LearnActivity,
                application,
                this,
                currentSubject,
                subjectsForRecyclerView
            )
            binding.recyclerView.layoutManager = GridLayoutManager(activity, 2)
            binding.recyclerView.addItemDecoration(SpacingItemDecorator(30, 10))
            binding.recyclerView.adapter = subjectAdapter
        } else {
            binding.errorMessage.text = resources.getString(R.string.no_subjects_defined_for_this_class)
            binding.errorMessage.visibility = View.VISIBLE
            binding.selectButton.text = "Ok"
        }

        binding.selectButton.setOnClickListener {
            if(subjectsForRecyclerView.isNotEmpty()) {
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

        return binding.root
    }

    override fun changeSubject(value: SubjectModel) {
        selectedSubject = value
    }

    override fun disableErrorMessage() {
        binding.errorMessage.visibility = View.GONE
    }

    fun addListener(listener: ISubjectChanged) {
        this.listener = listener
    }

    private fun getSubjectsForRecyclerView(list: List<SubjectModel>): List<SubjectModel> {
        val filteredList = ArrayList<SubjectModel>()
        for(subject in list) {
            if(subject.atUniversity == atUniversity) {
                if(atUniversity || classNumber >= 8) {
                    selectedDomain?.apply {
                        if (subject.domainId == this.id && subject.classNumber == classNumber) {
                            filteredList.add(subject)
                        }
                    }
                } else {
                    if (subject.classNumber == classNumber) {
                        filteredList.add(subject)
                    }
                }
            }
        }
        return filteredList
    }
}