package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardLearnPathSectionBinding
import com.dig.digibrain.interfaces.ILearnPathSectionSelected
import com.dig.digibrain.models.learnPaths.LearnPathSection
import com.dig.digibrain.objects.LearnPathLocalStatus

class LearnPathSectionAdapter(var context: Context, var listener: ILearnPathSectionSelected, private var arrayList: List<LearnPathSection>, var sectionNumber: Long): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardLearnPathSectionBinding
    private var selectedPosition = if (sectionNumber != 0L) sectionNumber.toInt() else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardLearnPathSectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return LearnPathSectionViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val learnPathSection = arrayList[position]

        (holder as LearnPathSectionViewHolder).initializeUIComponents(learnPathSection, position)

        if(learnPathSection.number < sectionNumber.toInt()) {
            holder.statusPoint.backgroundTintList = AppCompatResources.getColorStateList(
                context,
                R.color.green
            )
            holder.statusBarUp.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            holder.statusBarDown.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
        } else if(learnPathSection.number == sectionNumber.toInt()) {
            holder.statusPoint.backgroundTintList = AppCompatResources.getColorStateList(
                context,
                R.color.yellow
            )
            holder.statusBarUp.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            holder.statusBarDown.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        } else if(learnPathSection.number - 1 == sectionNumber.toInt()) {
            holder.statusBarUp.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        }

        if(learnPathSection.number == selectedPosition) {
            holder.currentSection.visibility = View.VISIBLE
            if(!LearnPathLocalStatus.updated) {
                listener.changeSectionPosition(learnPathSection.number)
                LearnPathLocalStatus.updated = true
            }
        } else {
            holder.currentSection.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener.changeSectionPosition(learnPathSection.number)
            selectedPosition = learnPathSection.number
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class LearnPathSectionViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: View = myView.findViewById(R.id.card)
        var sectionTitle: TextView = myView.findViewById(R.id.learn_path_section_title)
        var currentSection: View = myView.findViewById(R.id.learn_path_Section_current)

        var statusPoint: View = myView.findViewById(R.id.learn_path_section_status_point)
        var statusBarUp: View = myView.findViewById(R.id.learn_path_section_status_bar_up)
        var statusBarDown: View = myView.findViewById(R.id.learn_path_section_status_bar_down)


        fun initializeUIComponents(model: LearnPathSection, position: Int) {
            sectionTitle.text = model.title

            if(position != 0) {
                statusBarUp.visibility = View.VISIBLE
            }

            if(arrayList.size != 1 && position != arrayList.size - 1) {
                statusBarDown.visibility = View.VISIBLE
            }
        }
    }
}