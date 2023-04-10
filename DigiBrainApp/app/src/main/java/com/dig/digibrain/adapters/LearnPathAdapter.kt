package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardLearnPathBinding
import com.dig.digibrain.models.learnPaths.LearnPathModel

class LearnPathAdapter(var context: Context, private var arrayList: List<LearnPathModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardLearnPathBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardLearnPathBinding.inflate(LayoutInflater.from(context), parent, false)
        return LearnPathViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val learnPathItem = arrayList[position]

        (holder as LearnPathViewHolder).initializeUIComponents(learnPathItem)
    }

    inner class LearnPathViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var titleText: TextView = myView.findViewById(R.id.learn_path_title)
        var authorText: TextView = myView.findViewById(R.id.learn_path_author)
        var dateText: TextView = myView.findViewById(R.id.learn_path_date)

        fun initializeUIComponents(model: LearnPathModel) {
            titleText.text = model.title
            authorText.text = model.author
            dateText.text = model.date.toString()
        }
    }
}