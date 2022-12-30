package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
            binding.lessonContent.text = lessonContent
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        // TODO - Markdown content
    }
}