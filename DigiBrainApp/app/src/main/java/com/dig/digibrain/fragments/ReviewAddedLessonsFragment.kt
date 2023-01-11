package com.dig.digibrain.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.activities.AddQuestionToLessonsActivity
import com.dig.digibrain.adapters.SubjectsReviewAdapter
import com.dig.digibrain.databinding.FragmentReviewAddedLessonsBinding
import com.dig.digibrain.models.subject.ClassModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel
import com.dig.digibrain.models.subject.SubjectReviewModel

class ReviewAddedLessonsFragment : Fragment() {

    private lateinit var binding: FragmentReviewAddedLessonsBinding
    private lateinit var adapter: SubjectsReviewAdapter

    var list: MutableList<SubjectReviewModel> = mutableListOf()
    private var subjectIds: MutableList<Long> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewAddedLessonsBinding.inflate(layoutInflater)

        adapter = SubjectsReviewAdapter(requireContext(), list)

        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = adapter

        binding.addButton.setOnClickListener {
            (activity as AddQuestionToLessonsActivity).addLessons(subjectIds)
        }

        return binding.root
    }

    fun addSubjectReview(subjectModel: SubjectModel, domainModel: DomainModel?, classModel: ClassModel) {
        val model = SubjectReviewModel(subjectModel, domainModel, classModel)
        list.add(model)
        subjectIds.add(subjectModel.id)
        adapter.notifyDataSetChanged()
    }

}