package com.dig.digibrain.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.activities.LearnPathDetailsActivity
import com.dig.digibrain.databinding.CardLearnPathBinding
import com.dig.digibrain.models.learnPaths.LearnPathDetailedModel
import com.dig.digibrain.models.learnPaths.LearnPathStatusModel
import com.dig.digibrain.utils.Helper
import com.dig.digibrain.utils.Helper.Companion.convertTimestampToDateFormat
import com.dig.digibrain.utils.Helper.Companion.getInitials
import java.text.SimpleDateFormat
import java.util.*

class LearnPathAdapter(var context: Context, private var arrayList: List<LearnPathDetailedModel>, var userStatus: List<LearnPathStatusModel>?, var preview: Boolean, var finished: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        holder.itemView.setOnClickListener {
            val intent = Intent(context, LearnPathDetailsActivity::class.java)

            val bundle = Bundle()
            bundle.putParcelable("learnPath", learnPathItem)
            bundle.putBoolean("preview", preview)
            bundle.putBoolean("finished", finished)

            // Send status if exists
            val status = getLearnPathStatus(learnPathItem.id)
            status?.apply {
                bundle.putLong("sectionNumber", this.sectionNumber)
                bundle.putLong("lessonNumber", this.lessonNumber)
                bundle.putLong("theoryNumber", this.theoryNumber)
            }

            intent.putExtras(bundle)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    private fun getLearnPathStatus(learnPathId: Long): LearnPathStatusModel? {
        if(userStatus != null) {
            for(status in userStatus!!) {
                if(status.pathLearnId == learnPathId) {
                    return status
                }
            }
        }
        return null
    }

    inner class LearnPathViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var titleText: TextView = myView.findViewById(R.id.learn_path_title)
        var authorText: TextView = myView.findViewById(R.id.learn_path_author)
        var dateText: TextView = myView.findViewById(R.id.learn_path_date)
        var titleInitials: TextView = myView.findViewById(R.id.learn_path_initials)
        var numberOfViews: TextView = myView.findViewById(R.id.number_of_views)

        fun initializeUIComponents(model: LearnPathDetailedModel) {
            titleText.text = model.title
            authorText.text = model.author
            dateText.text = Helper.convertTimestampToDateFormat(model.date)
            titleInitials.text = model.title.getInitials()
            numberOfViews.text = model.started.toString()
        }
    }
}