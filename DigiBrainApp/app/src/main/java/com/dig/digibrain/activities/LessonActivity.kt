package com.dig.digibrain.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityLessonBinding

class LessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLessonBinding

    var lessonTitle: String? = null
    var lessonContent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if(bundle != null) {
            lessonTitle = bundle.getString("lessonTitle")
            lessonContent = bundle.getString("lessonContent")
        } else {
            Toast.makeText(this, "Lesson could not be extracted", Toast.LENGTH_SHORT).show()
        }

        if(lessonTitle != null && lessonContent != null) {
            binding.lessonTitle.text = lessonTitle
//            binding.lessonContent.text = HtmlCompat.fromHtml(lessonContent!!, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.lessonContent.setBackgroundColor(Color.TRANSPARENT)
            binding.lessonContent.loadData(lessonContent!!, "text/html", "UTF-8")
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        // TODO - Markdown content
    }
}