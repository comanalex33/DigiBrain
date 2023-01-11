package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardReviewSubjectsBinding
import com.dig.digibrain.models.subject.SubjectReviewModel

class SubjectsReviewAdapter(var context: Context, private var arrayList: List<SubjectReviewModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardReviewSubjectsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardReviewSubjectsBinding.inflate(LayoutInflater.from(context), parent, false)
        return SubjectViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val reviewSubjectItem = arrayList[position]

        (holder as SubjectViewHolder).initializeComponents(reviewSubjectItem)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class SubjectViewHolder(myView: View): ViewHolder(myView) {
        var subjectText: TextView = myView.findViewById(R.id.review_subject_name)
        var domainText: TextView = myView.findViewById(R.id.review_domain_name)
        var classNumber: TextView = myView.findViewById(R.id.review_class_number)

        fun initializeComponents(model: SubjectReviewModel) {
            subjectText.text = model.subjectModel.name
            if(model.domainModel == null) {
                domainText.visibility = View.GONE
            } else {
                domainText.text = model.domainModel!!.name
            }
            classNumber.text = model.classModel.number.toString()
        }
    }

}