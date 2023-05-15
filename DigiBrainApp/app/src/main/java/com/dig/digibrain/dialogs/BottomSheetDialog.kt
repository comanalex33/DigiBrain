package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.transition.Visibility
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.ViewGroup.LayoutParams
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.activities.LearnPathActivity
import com.dig.digibrain.adapters.LearnPathFilterSubjectAdapter
import com.dig.digibrain.adapters.SubjectSpinnerItemAdapter
import com.dig.digibrain.databinding.DialogBottomSheetBinding
import com.dig.digibrain.interfaces.IApplyLearnPathFilter
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.models.subject.ClassModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel
import com.dig.digibrain.utils.Helper.Companion.forAllChildren
import kotlin.math.exp

class BottomSheetDialog(
    var subjects: List<SubjectModel>,
    var currentClass: Int?,
    var isUniversity: Boolean,
    var listener: IApplyLearnPathFilter): DialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: DialogBottomSheetBinding
    private lateinit var adapter: LearnPathFilterSubjectAdapter

    private var currentSelectedButton: Button? = null

    private var classes: List<ClassModel> = mutableListOf()
    private var domains: List<DomainModel> = mutableListOf()

    private var selectedSubjectsIds = mutableListOf<Long>()
    private var selectedName = ""

    private var filteredSubjects: MutableList<SubjectModel> = subjects.toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomSheetBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)

        // Setup default name
        if(selectedName != "") {
            val editable = binding.learnPathName.text
            editable!!.replace(0, editable.length, selectedName)
        }

        // Handle filter options expand
        handleExpandable(filter = binding.classFilter, filterContent = binding.classFilterContent, filterExpand = binding.classFilterExpand)
        handleExpandable(filter = binding.subjectFilter, filterContent = binding.subjectFilterContent, filterExpand = binding.subjectFilterExpand)
        handleExpandable(filter = binding.nameFilter, filterContent = binding.nameFilterContent, filterExpand = binding.nameFilterExpand)

        // Set subjects in RecyclerView
        binding.subjectFilterContent.layoutManager = GridLayoutManager(activity, 2)
        adapter = LearnPathFilterSubjectAdapter(requireContext(), filteredSubjects, selectedSubjectsIds)
        binding.subjectFilterContent.adapter = adapter

        binding.searchButton.setOnClickListener {
            val selectedSubjectIds =  adapter.getSelectedSubjects()
            val list = mutableListOf<String>()
            for(subject in subjects) {
                if(selectedSubjectIds.contains(subject.id)) {
                    list.add(subject.name)
                }
            }
            listener.applyFilter(subjectIds = selectedSubjectIds, name = binding.learnPathName.text.toString())
            dialog?.dismiss()
        }

        // Handle class filter update
        binding.classFilterContent.forAllChildren { view ->
            when(view) {
                is Button -> {
                    // Set current value
                    currentClass?.apply {
                        if(isUniversity) {
                            if(getClassNumberIfUniversity(view.text.toString()) == this) {
                                view.backgroundTintList = AppCompatResources.getColorStateList(
                                    requireContext(),
                                    R.color.blue_light
                                )
                                currentSelectedButton = view
                                updateFilteredSubjects(currentClass!!, isUniversity)
                            }
                        } else {
                            if (view.text == this.toString()) {
                                view.backgroundTintList = AppCompatResources.getColorStateList(
                                    requireContext(),
                                    R.color.blue_light
                                )
                                currentSelectedButton = view
                                updateFilteredSubjects(currentClass!!, isUniversity)
                            }
                        }
                    }
                    // Update value
                    view.setOnClickListener {
                        if (currentSelectedButton != null) {
                            currentSelectedButton!!.backgroundTintList =
                                AppCompatResources.getColorStateList(requireContext(), R.color.gray)
                        }
                        currentSelectedButton = view
                        view.backgroundTintList = AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.blue_light
                        )

                        if(currentSelectedButton!!.text.all { char -> char.isDigit() }) {
                            val classNumber = Integer.parseInt(currentSelectedButton!!.text.toString())
                            listener.changeClass(classNumber = classNumber, isUniversity = false)
                            updateFilteredSubjects(classNumber, false)
                        } else {
                            val classNumber = getClassNumberIfUniversity(currentSelectedButton!!.text.toString())
                            listener.changeClass(classNumber = classNumber, isUniversity = true)
                            updateFilteredSubjects(classNumber, true)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // get the display metrics of the device
        val displayMetrics = DisplayMetrics()

        (context as LearnPathActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)

        // calculate the height of the screen
        val screenHeight = displayMetrics.heightPixels

        // calculate 66% of the screen height
        val layoutHeight = (screenHeight * 0.7).toInt()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    fun setClasses(classes: List<ClassModel>) {
        this.classes = classes
    }

    fun setDomains(domains: List<DomainModel>) {
        this.domains = domains
    }

    fun setSelected(subjectsIds: List<Long>, name: String) {
        this.selectedSubjectsIds = subjectsIds.toMutableList()
        this.selectedName = name
    }

    private fun handleExpandable(filter: View, filterContent: View, filterExpand: View) {
        filter.setOnClickListener {
            if(filterContent.visibility == View.VISIBLE) {
                setExpandable(filterContent, filterExpand, View.GONE)
            } else {
                when(filter) {
                    binding.classFilter -> {
                        setExpandable(binding.subjectFilterContent, binding.subjectFilterExpand, View.GONE)
                        setExpandable(binding.nameFilterContent, binding.nameFilterExpand, View.GONE)
                    }
                    binding.subjectFilter -> {
                        setExpandable(binding.classFilterContent, binding.classFilterExpand, View.GONE)
                        setExpandable(binding.nameFilterContent, binding.nameFilterExpand, View.GONE)
                    }
                    binding.nameFilter -> {
                        setExpandable(binding.subjectFilterContent, binding.subjectFilterExpand, View.GONE)
                        setExpandable(binding.classFilterContent, binding.classFilterExpand, View.GONE)
                    }
                }
                setExpandable(filterContent, filterExpand, View.VISIBLE)
            }
        }
    }

    private fun setExpandable(content: View, expand: View, visibility: Int) {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
        content.visibility = visibility
        if(visibility == View.GONE)
            expand.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_more)
        else
            expand.background = ContextCompat.getDrawable(requireContext(), R.drawable.ic_expand_less)
    }

    private fun getClassNumberIfUniversity(number: String): Int {
        return when(number) {
            "I" -> 1
            "II" -> 2
            "III" -> 3
            "IV" -> 4
            "V" -> 5
            "VI" -> 6
            else -> 0
        }
    }

    private fun updateFilteredSubjects(classNumber: Int, atUniversity: Boolean) {
        filteredSubjects = mutableListOf()
        val classModel = getClass(classNumber, atUniversity)
        classModel?.apply {
            for(subject in subjects) {
                if(subject.classId == classModel.id)
                    filteredSubjects.add(subject)
            }
        }
        adapter = LearnPathFilterSubjectAdapter(requireContext(), filteredSubjects, selectedSubjectsIds)
        binding.subjectFilterContent.adapter = adapter
    }

    private fun getClass(classNumber: Int, atUniversity: Boolean): ClassModel? {
        for(classModel in classes) {
            if(classModel.number == classNumber && classModel.atUniversity == atUniversity)
                return classModel
        }
        return null
    }
}