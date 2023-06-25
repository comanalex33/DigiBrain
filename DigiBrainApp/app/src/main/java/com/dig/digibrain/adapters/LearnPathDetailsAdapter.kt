package com.dig.digibrain.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dig.digibrain.R
import com.dig.digibrain.dialogs.LearnPathLessonDialog
import com.dig.digibrain.interfaces.IUpdateSection
import com.dig.digibrain.models.learnPaths.*
import com.dig.digibrain.objects.LearnPathLocalStatus
import com.dig.digibrain.utils.Helper

class LearnPathDetailsAdapter(
    var context: Context,
    private var learnPathExpandedModel: LearnPathExpandedModel,
    var sectionPosition: Int,
    var sectionNumber: Long,
    var lessonNumber: Long,
    var theoryNumber: Long,
    var preview: Boolean,
    var justCompleted: Boolean = false): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                    holder.view.setOnClickListener {
                        model?.apply {
                            val lesson = getLessonById(this.pathLessonId)
                            val dialog = LearnPathLessonDialog(lesson = lesson, theory = this, sectionNumber = sectionNumber, learnPathId = learnPathExpandedModel.id)
                            dialog.setListener(this@LearnPathDetailsAdapter, position)

                            dialog.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "Learn Path theory details"
                            )

                            if(holder.view.id == R.id.done_view)
                                dialog.setupUI(R.color.yellow, context.getString(R.string.start_again), false)
                            if(holder.view.id == R.id.future_view)
                                dialog.setupUI(R.color.gray, context.getString(R.string.blocked), false)
                        }
                    }
                }
            }
            is LearnPathQuizViewHolder -> {
                val quizes = holder.initializeUIComponents(position)
                if(!preview) {
                    holder.view.setOnClickListener {
                        quizes?.apply {
                            val lesson = getLessonById(this[0].pathLessonId)
                            val dialog = LearnPathLessonDialog(lesson = lesson, quizes = quizes, sectionNumber = sectionNumber, learnPathId = learnPathExpandedModel.id)
                            dialog.setListener(this@LearnPathDetailsAdapter, position)

                            dialog.show(
                                (context as AppCompatActivity).supportFragmentManager,
                                "Learn Path quiz"
                            )

                            if(holder.view.id == R.id.done_view)
                                dialog.setupUI(R.color.yellow, context.getString(R.string.start_again), false)
                            if(holder.view.id == R.id.future_view)
                                dialog.setupUI(R.color.gray, context.getString(R.string.blocked), false)
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
                itemCount += 1 + lesson.theory.size
                if(lesson.quiz.isNotEmpty())
                    itemCount++
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
                var currentLessonContentSize = lesson.theory.size
                if(lesson.quiz.isNotEmpty())
                    currentLessonContentSize++
                if(lessonPosition > currentLessonContentSize) {
                    vPosition += currentLessonContentSize
                    continue
                }
                for(theory in lesson.theory) {
                    if(theory.number == lessonPosition) {
                        return VIEW_TYPE_THEORY
                    }
                }
                if(lesson.quiz.isNotEmpty())
                    return VIEW_TYPE_QUIZ
            }
        }
        return -1
    }

    fun changeVisibility(preview: Boolean) {
        this.preview = preview
    }

    private fun getSection(): LearnPathSection? {
        for(section in learnPathExpandedModel.sections) {
            if(section.number == sectionPosition)
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
        var lessonCurrent: View = myView.findViewById(R.id.learn_path_lesson_current)
        var lessonCompleted: View = myView.findViewById(R.id.learn_path_lesson_completed)

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
                    if (itemCount == position) {
                        if(!preview)
                            if(this.number < sectionNumber.toInt() ||
                                (this.number == sectionNumber.toInt() && lesson.number < lessonNumber.toInt())) {
                                lessonCurrent.visibility = View.GONE
                                lessonCompleted.visibility = View.VISIBLE
                                if(this.lessons[this.lessons.size - 1] == lesson && justCompleted) {
                                    LearnPathLocalStatus.sectionFinished = sectionNumber - 1
                                    LearnPathLocalStatus.updated = false
                                }
                            }
                            else if(this.number == sectionNumber.toInt() && lesson.number == lessonNumber.toInt()) {
                                lessonCurrent.visibility = View.VISIBLE
                                lessonCompleted.visibility = View.GONE
                            } else {
                                lessonCurrent.visibility = View.GONE
                                lessonCompleted.visibility = View.GONE
                            }
                        return lesson
                    }
                    itemCount = 1 + lesson.theory.size
                    if(lesson.quiz.isNotEmpty())
                        itemCount++
                }
            }
            return null
        }
    }

    inner class LearnPathTheoryViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var view: View = myView.findViewById(R.id.future_view)
        var parent: ConstraintLayout = myView.findViewById(R.id.parent_view)

        var doneView: View = myView.findViewById(R.id.done_view)
        var currentView: View = myView.findViewById(R.id.current_view)
        var futureView: View = myView.findViewById(R.id.future_view)

        var gifImage: ImageView = myView.findViewById(R.id.gif_image)

        fun initializeUIComponents(position: Int): LearnPathTheory? {
            setupVisibility(future = true)
            val (model, vars) = getTheory(position)
            val (pos, len) = vars
            model?.apply {
                // Get position
                val place = getPosition(len, pos)

                // Set position
                val constraintSet = ConstraintSet()
                constraintSet.clone(parent)

                when(place) {
                    -1 -> {
                        constraintSet.connect(
                            view.id,
                            ConstraintSet.START,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.START
                        )
                        constraintSet.clear(view.id, ConstraintSet.END)

                        gifImage.visibility = View.VISIBLE

                        constraintSet.connect(
                            gifImage.id,
                            ConstraintSet.END,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.END
                        )
                        constraintSet.clear(gifImage.id, ConstraintSet.START)

                        putGif()
                    }
                    1 -> {
                        constraintSet.connect(
                            view.id,
                            ConstraintSet.END,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.END
                        )
                        constraintSet.clear(view.id, ConstraintSet.START)

                        gifImage.visibility = View.VISIBLE

                        constraintSet.connect(
                            gifImage.id,
                            ConstraintSet.START,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.START
                        )
                        constraintSet.clear(gifImage.id, ConstraintSet.END)

                        putGif()
                    }
                    0 -> {
                        constraintSet.connect(
                            view.id,
                            ConstraintSet.START,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.START
                        )
                        constraintSet.connect(
                            view.id,
                            ConstraintSet.END,
                            ConstraintSet.PARENT_ID,
                            ConstraintSet.END
                        )
                    }
                }

                constraintSet.applyTo(parent)

            }
            return model
        }

        private fun putGif() {
            Glide.with(context)
                .asGif()
                .load(Helper.getAllGIFs().random())
                .into(gifImage)
        }

        private fun getTheory(position: Int): Pair<LearnPathTheory?, Pair<Int, Int>> {
            var itemCount = 0
            var pos = 0
            var len = 0

            val section = getSection()
            section?.apply {
                for(lesson in this.lessons) {
                    itemCount++
                    val lessonPosition = position - itemCount + 1
                    var currentLessonContentSize = lesson.theory.size
                    if(lesson.quiz.isNotEmpty())
                        currentLessonContentSize++
                    if(lessonPosition > currentLessonContentSize) {
                        itemCount += currentLessonContentSize
                        continue
                    }
                    for(theory in lesson.theory) {

                        pos++
                        len = lesson.theory.size
                        if(lesson.quiz.isNotEmpty())
                            len++
                        if(theory.number == lessonPosition) {
                            if(!preview) {
                                if(this.number < sectionNumber.toInt() ||
                                    (this.number == sectionNumber.toInt() && lesson.number < lessonNumber.toInt()) ||
                                    (this.number == sectionNumber.toInt() && lesson.number == lessonNumber.toInt() && (theory.number < theoryNumber.toInt() || theoryNumber.toInt() == 0))) {
                                    view = doneView
                                    setupVisibility(done = true)
                                }
                                    //this@LearnPathTheoryViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                                if(this.number == sectionNumber.toInt() && lesson.number == lessonNumber.toInt() && theory.number == theoryNumber.toInt()) {
                                    view = currentView
                                    setupVisibility(current = true)
                                }
                                    //this@LearnPathTheoryViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
                            }
                            return Pair(theory, Pair(pos, len))
                        }
                    }
                    itemCount += lesson.theory.size
                    if(lesson.quiz.isNotEmpty())
                        itemCount++
                }
            }
            return Pair(null, Pair(0, 0))
        }

        private fun getPosition(sequenceLength: Int, target: Int): Int {
            if(target < 1 || target > sequenceLength)
                return -2
            if(target == 1 || target == sequenceLength)
                return 0

            var position = 0
            var increment = 1

            for (i in 2 until sequenceLength) {
                position += increment

                if(i == target)
                    break

                if(position == 1)
                    increment = -1
                if(position == -1)
                    increment = 1
            }

            return position
        }

        private fun setupVisibility(done: Boolean = false, current: Boolean = false, future: Boolean = false) {
            val doneVisibility = if (!done) View.GONE else View.VISIBLE
            val currentVisibility = if (!current) View.GONE else View.VISIBLE
            val futureVisibility = if (!future) View.GONE else View.VISIBLE

            doneView.visibility = doneVisibility
            currentView.visibility = currentVisibility
            futureView.visibility = futureVisibility
        }
    }

    inner class LearnPathQuizViewHolder(myView: View): RecyclerView.ViewHolder(myView) {

        var view: View = myView.findViewById(R.id.future_view)

        var doneView: View = myView.findViewById(R.id.done_view)
        var currentView: View = myView.findViewById(R.id.current_view)
        var futureView: View = myView.findViewById(R.id.future_view)

        fun initializeUIComponents(position: Int): List<LearnPathQuiz>? {
            setupVisibility(future = true)
            return getQuiz(position)
        }

        private fun getQuiz(position: Int): List<LearnPathQuiz>? {
            var itemCount = 0
            val section = getSection()
            section?.apply {
                for(lesson in this.lessons) {
                    itemCount++
                    val lessonPosition = position - itemCount + 1
                    var currentLessonContentSize = lesson.theory.size
                    if(lesson.quiz.isNotEmpty())
                        currentLessonContentSize++
                    if(lessonPosition > currentLessonContentSize) {
                        itemCount += currentLessonContentSize
                        continue
                    }
                    if(!preview) {
                        if(this.number < sectionNumber.toInt() ||
                            (this.number == sectionNumber.toInt() && lesson.number < lessonNumber.toInt())) {
                            view = doneView
                            setupVisibility(done = true)
                        }
                        //this@LearnPathTheoryViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                        if(this.number == sectionNumber.toInt() && lesson.number == lessonNumber.toInt() && theoryNumber.toInt() == 0) {
                            view = currentView
                            setupVisibility(current = true)
                        }
                    }
                    return lesson.quiz
                }
            }
            return null
        }

        private fun setupVisibility(done: Boolean = false, current: Boolean = false, future: Boolean = false) {
            val doneVisibility = if (!done) View.GONE else View.VISIBLE
            val currentVisibility = if (!current) View.GONE else View.VISIBLE
            val futureVisibility = if (!future) View.GONE else View.VISIBLE

            doneView.visibility = doneVisibility
            currentView.visibility = currentVisibility
            futureView.visibility = futureVisibility
        }
    }
}