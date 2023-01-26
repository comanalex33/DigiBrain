package com.dig.digibrain.adapters

import android.app.Activity
import android.app.Application
import android.content.Context
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardSubjectChapterBinding
import com.dig.digibrain.databinding.DialogAddLessonBinding
import com.dig.digibrain.dialogs.AddLessonDialog
import com.dig.digibrain.interfaces.IAddLesson
import com.dig.digibrain.interfaces.IChapterChanged
import com.dig.digibrain.interfaces.ILessonSelected
import com.dig.digibrain.models.subject.ChapterModel
import com.dig.digibrain.models.subject.LessonModel

class ChapterAdapter(var context: Context, var modifyCapabilities: Boolean, private var arrayList: List<ChapterModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>(), IAddLesson {

    private lateinit var binding: CardSubjectChapterBinding
    private lateinit var listener: IChapterChanged
    private lateinit var list: View
    private lateinit var noList: View

    var chapterLessons = mutableMapOf<Long, MutableList<LessonModel>>()

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

        holder.addLesson.setOnClickListener {
            val dialog = AddLessonDialog(chapterItem.id)
            dialog.addListener(this)
            dialog.show((context as AppCompatActivity).supportFragmentManager, "Add lesson")
        }

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

    fun addChapterLessons(chapterId: Long, lessons: List<LessonModel>) {
        chapterLessons[chapterId] = lessons.toMutableList()
    }

    fun addChapterOneLesson(chapterId: Long, lesson: LessonModel) {
        val list = chapterLessons[chapterId]
        if(list == null) {
            chapterLessons[chapterId] = mutableListOf(lesson)
        } else {
            list.add(lesson)
            chapterLessons[chapterId] = list
        }
    }

    inner class ChapterViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.chapter_card_view)
        var chapterName: TextView = myView.findViewById(R.id.chapter_name)
        var recyclerView: RecyclerView = myView.findViewById(R.id.lessons_recycler_view)
        var expandArrow: ImageView = myView.findViewById(R.id.chapter_expand)
        var chapterContent: View = myView.findViewById(R.id.chapter_content)
        var noContent: View = myView.findViewById(R.id.chapter_no_content)
        var addLesson: View = myView.findViewById(R.id.chapter_add_lesson_button)

        fun initializeUIComponents(model: ChapterModel) {
            chapterName.text = "${model.number}. ${model.name}"
            listener.getLessons(model.id, recyclerView, noContent)

            addLesson.visibility = if(modifyCapabilities) View.VISIBLE else View.GONE
            chapterContent.visibility = View.GONE
            expandArrow.setImageResource(R.drawable.ic_expand_more)

            list = recyclerView
            noList = noContent
        }
    }

    override fun addLesson(chapterId: Long, title: String, text: String) {
        listener.addLesson(chapterId, title, text, this, list, noList)
    }

}