package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ItemSubjectSpinnerBinding
import com.dig.digibrain.models.subject.SubjectModel

class SubjectSpinnerItemAdapter(context: Context, subjects: List<SubjectModel>): ArrayAdapter<SubjectModel>(context, 0, subjects) {

    private val layoutInflater = LayoutInflater.from(context)
    private lateinit var binding: ItemSubjectSpinnerBinding

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        binding = ItemSubjectSpinnerBinding.inflate(layoutInflater, parent, false)

        val item = getItem(position)

        if(item != null) {
            binding.image.setImageResource(R.drawable.ic_profile)
            binding.text.text = item.name
        }

        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}