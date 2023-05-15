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

class LearnPathSectionAdapter(var context: Context, var listener: ILearnPathSectionSelected, private var arrayList: List<LearnPathSection>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardLearnPathSectionBinding
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardLearnPathSectionBinding.inflate(LayoutInflater.from(context), parent, false)
        return LearnPathSectionViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val learnPathSection = arrayList[position]

        (holder as LearnPathSectionViewHolder).initializeUIComponents(learnPathSection)

        if(position == selectedPosition) {
            holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_light))
        } else {
            holder.card.background = null
        }

        holder.itemView.setOnClickListener {
            listener.changeSectionPosition(learnPathSection.number)
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class LearnPathSectionViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: View = myView.findViewById(R.id.card)
        var sectionTitle: TextView = myView.findViewById(R.id.learn_path_section_title)

        fun initializeUIComponents(model: LearnPathSection) {
            sectionTitle.text = model.title
        }
    }
}