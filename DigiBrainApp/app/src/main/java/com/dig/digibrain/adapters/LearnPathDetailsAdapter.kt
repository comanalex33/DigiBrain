package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.R
import com.dig.digibrain.databinding.DialogLearnPathLessonDetailsBinding
import com.dig.digibrain.dialogs.LearnPathLessonDialog
import com.dig.digibrain.models.learnPaths.*

class LearnPathDetailsAdapter(var context: Context, var sectionNumber: Int, private var learnPathExpandedModel: LearnPathExpandedModel, var preview: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LESSON = 0
    private val VIEW_TYPE_THEORY = 1
    private val VIEW_TYPE_QUIZ = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)

        return when (viewType) {
            VIEW_TYPE_LESSON -> {
                val view = inflater.inflate(R.layout.card_learn_path_lesson, parent, false)
                LearnPathLessonViewHolder(view)
            }
            VIEW_TYPE_THEORY -> {
                val view = inflater.inflate(R.layout.card_learn_path_theory, parent, false)
                LearnPathTheoryViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.card_learn_path_quiz, parent, false)
                LearnPathQuizViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LearnPathLessonViewHolder -> {
                val model = holder.initializeUIComponents(position)
            }
            is LearnPathTheoryViewHolder -> {
                val model = holder.initializeUIComponents(position)

                if(!preview) {
                    holder.itemView.setOnClickListener {
                        model?.apply {
                            val lesson = getLessonById(this.pathLessonId)
                            val dialog = LearnPathLessonDialog(lesson = lesson, theory = this)

                            dialog.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "Learn Path theory details"
                            )
                        }
                    }
                }
            }
            is LearnPathQuizViewHolder -> {
                val model = holder.initializeUIComponents(position)
                if(!preview) {
                    holder.itemView.setOnClickListener {
                        model?.apply {
                            val lesson = getLessonById(this.pathLessonId)
                            val dialog = LearnPathLessonDialog(lesson = lesson, quiz = this)

                            dialog.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "Learn Path quiz details"
                            )
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        var itemCount = 0
        val section = getSection()
        section?.apply {
            for(lesson in this.lessons) {
                itemCount += 1 + lesson.theory.size + lesson.quiz.size
            }
        }
        return itemCount
    }

    override fun getItemViewType(position: Int): Int {
        var vPosition = 0
        val section = getSection()
        section?.apply {
            for(lesson in this.lessons) {
                if(position == vPosition) {
                    return VIEW_TYPE_LESSON
                }
                vPosition++
                val lessonPosition = position - vPosition + 1
                if(lessonPosition > lesson.theory.size + lesson.quiz.size) {
                    vPosition += lesson.theory.size + lesson.quiz.size
                    continue
                }
                for(theory in lesson.theory) {
                    if(theory.number == lessonPosition) {
                        return VIEW_TYPE_THEORY
                    }
                }
                for(quiz in lesson.quiz) {
                    if(quiz.number == lessonPosition) {
                        return VIEW_TYPE_QUIZ
                    }
                }
                vPosition += lesson.theory.size + lesson.quiz.size
            }
        }
        return -1
    }

    fun changeVisibility(preview: Boolean) {
        this.preview = preview
    }

    private fun getSection(): LearnPathSection? {
        for(section in learnPathExpandedModel.sections) {
            if(section.number == sectionNumber)
                return section
        }
        return null
    }

    private fun getLessonById(id: Long): LearnPathLesson? {
        val section = getSection()
        section?.apply {
            for(lesson in this.lessons) {
                if(lesson.id == id)
                    return lesson
            }
        }
        return null
    }

    inner class LearnPathLessonViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var lessonTitle: TextView = myView.findViewById(R.id.learn_path_lesson_title)

        fun initializeUIComponents(position: Int): LearnPathLesson? {
            val model = getLesson(position)
            model?.apply {
                lessonTitle.text = this.title
            }
            return model
        }

        private fun getLesson(position: Int): LearnPathLesson? {
            var itemCount = 0
            val section = getSection()
            section?.apply {
                for(lesson in this.lessons) {
                    if (itemCount == position)
                        return lesson
                    itemCount = 1 + lesson.quiz.size + lesson.theory.size
                }
            }
            return null
        }
    }

    inner class LearnPathTheoryViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var theoryTitle: TextView = myView.findViewById(R.id.learn_path_theory_title)

        fun initializeUIComponents(position: Int): LearnPathTheory? {
            val model = getTheory(position)
            model?.apply {
                theoryTitle.text = this.title
            }
            return model
        }

        private fun getTheory(position: Int): LearnPathTheory? {
            var itemCount = 0
            val section = getSection()
            section?.apply {
                for(lesson in this.lessons) {
                    itemCount++
                    val lessonPosition = position - itemCount + 1
                    if(lessonPosition > lesson.theory.size + lesson.quiz.size) {
                        itemCount += lesson.theory.size + lesson.quiz.size
                        continue
                    }
                    for(theory in lesson.theory) {
                        if(theory.number == lessonPosition) {
                            return theory
                        }
                    }
                    itemCount += lesson.theory.size + lesson.quiz.size
                }
            }
            return null
        }
    }

    inner class LearnPathQuizViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var quizTitle: TextView = myView.findViewById(R.id.learn_path_quiz_title)

        fun initializeUIComponents(position: Int): LearnPathQuiz? {
            val model = getQuiz(position)
            model?.apply {
                quizTitle.text = "Quiz"
            }
            return model
        }

        private fun getQuiz(position: Int): LearnPathQuiz? {
            var itemCount = 0
            val section = getSection()
            section?.apply {
                for(lesson in this.lessons) {
                    itemCount++
                    val lessonPosition = position - itemCount + 1
                    if(lessonPosition > lesson.theory.size + lesson.quiz.size) {
                        itemCount += lesson.theory.size + lesson.quiz.size
                        continue
                    }
                    for(quiz in lesson.quiz) {
                        if(quiz.number == lessonPosition) {
                            return quiz
                        }
                    }
                    itemCount += lesson.theory.size + lesson.quiz.size
                }
            }
            return null
        }
    }
}