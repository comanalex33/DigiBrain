package com.dig.digibrain.adapters

import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardSubjectChapterBinding
import com.dig.digibrain.interfaces.IChapterChanged
import com.dig.digibrain.models.subject.ChapterModel

class ChapterAdapter(var context: Context, private var arrayList: List<ChapterModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardSubjectChapterBinding
    private lateinit var listener: IChapterChanged

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardSubjectChapterBinding.inflate(LayoutInflater.from(context), parent, false)
        return ChapterViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chapterItem = arrayList[position]

        (holder as ChapterViewHolder).initializeUIComponents(chapterItem)

        holder.card.setOnClickListener {
            if(holder.chapterContent.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                holder.chapterContent.visibility = View.GONE
                holder.expandArrow.setImageResource(R.drawable.ic_expand_more)
            } else {
                TransitionManager.beginDelayedTransition(binding.root, AutoTransition())
                holder.chapterContent.visibility = View.VISIBLE
                holder.expandArrow.setImageResource(R.drawable.ic_expand_less)
            }
        }
    }

    fun addListener(listener: IChapterChanged) {
        this.listener = listener
    }

    inner class ChapterViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.chapter_card_view)
        var chapterName: TextView = myView.findViewById(R.id.chapter_name)
        var recyclerView: RecyclerView = myView.findViewById(R.id.lessons_recycler_view)
        var expandArrow: ImageView = myView.findViewById(R.id.chapter_expand)
        var chapterContent: View = myView.findViewById(R.id.chapter_content)
        var noContent: View = myView.findViewById(R.id.chapter_no_content)

        fun initializeUIComponents(model: ChapterModel) {
            chapterName.text = "${model.number}. ${model.name}"
            listener.getLessons(model.id, recyclerView, noContent)

            chapterContent.visibility = View.GONE
        }
    }

}