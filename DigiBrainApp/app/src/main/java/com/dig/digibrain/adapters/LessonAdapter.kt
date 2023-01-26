package com.dig.digibrain.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.CardSubjectLessonBinding
import com.dig.digibrain.interfaces.ILessonSelected
import com.dig.digibrain.models.subject.LessonModel

class LessonAdapter(var context: Context, private var arrayList: List<LessonModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var binding: CardSubjectLessonBinding
    private lateinit var listener: ILessonSelected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = CardSubjectLessonBinding.inflate(LayoutInflater.from(context), parent, false)
        return LessonViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lessonItem = arrayList[position]

        (holder as LessonViewHolder).initializeUIComponents(lessonItem)

        holder.card.setOnTouchListener { _, motionEvent ->
            if(motionEvent.action == MotionEvent.ACTION_DOWN) {
                holder.card.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.white)
                true
            } else {
                holder.card.backgroundTintList = AppCompatResources.getColorStateList(context, R.color.blue_light)
            }
            false
        }

        holder.card.setOnClickListener {
            listener.openLesson(lessonItem)
        }
    }

    fun addListener(listener: ILessonSelected) {
        this.listener = listener
    }

    inner class LessonViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var card: CardView = myView.findViewById(R.id.lesson_card_view)
        var lessonName: TextView = myView.findViewById(R.id.lesson_title)

        fun initializeUIComponents(model: LessonModel) {
            lessonName.text = model.title
        }
    }
}