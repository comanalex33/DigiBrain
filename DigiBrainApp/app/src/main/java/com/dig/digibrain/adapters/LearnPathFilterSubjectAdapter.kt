package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ItemCheckBoxBinding
import com.dig.digibrain.models.subject.SubjectModel

class LearnPathFilterSubjectAdapter(var context: Context, private var arrayList: List<SubjectModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: ItemCheckBoxBinding

    private var selectedSubjects = mutableListOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = ItemCheckBoxBinding.inflate(LayoutInflater.from(context), parent, false)
        return SubjectViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val subjectItem: SubjectModel = arrayList[position]

        (holder as SubjectViewHolder).initializeUIComponents(subjectItem)

        holder.subject.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                selectedSubjects.add(subjectItem.id)
            } else {
                selectedSubjects.remove(subjectItem.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun getSelectedSubjects(): List<Long> {
        return selectedSubjects
    }

    inner class SubjectViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var subject: CheckBox = myView.findViewById(R.id.multiple_choice_answer)

        fun initializeUIComponents(model: SubjectModel) {
            subject.text = model.name

            itemView.backgroundTintList = AppCompatResources.getColorStateList(
                context,
                R.color.white
            )
        }
    }
}