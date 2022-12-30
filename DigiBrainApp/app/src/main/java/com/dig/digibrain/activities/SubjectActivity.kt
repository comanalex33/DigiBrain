package com.dig.digibrain.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.App
import com.dig.digibrain.adapters.ChapterAdapter
import com.dig.digibrain.adapters.LessonAdapter
import com.dig.digibrain.adapters.SpacingItemDecorator
import com.dig.digibrain.databinding.ActivitySubjectBinding
import com.dig.digibrain.interfaces.IChapterChanged
import com.dig.digibrain.interfaces.ILessonSelected
import com.dig.digibrain.models.subject.LessonModel
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnViewModel
import com.dig.digibrain.viewModels.SubjectViewModel
import com.dig.digibrain.viewModels.ViewModelFactory


class SubjectActivity : AppCompatActivity(), IChapterChanged, ILessonSelected {

    private lateinit var binding: ActivitySubjectBinding
    private lateinit var viewModel: SubjectViewModel

    var subjectId: Long = 0
    var subjectName: String? = ""
    var subjectIconId: Long = 0
    var classNumber: Int = 0
    var atUniversity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()

        val bundle = intent.extras
        if(bundle != null) {
            subjectId = bundle.getLong("subjectId")
            subjectName = bundle.getString("subjectName")
            subjectIconId = bundle.getLong("subjectIconId")
            classNumber = bundle.getInt("classNumber")
            atUniversity = bundle.getBoolean("atUniversity")
        } else {
            Toast.makeText(this, "Subject could not be extracted", Toast.LENGTH_SHORT).show()
        }

        if(subjectId > 0 && classNumber > 0 && subjectName != null) {
            binding.subjectTitle.text = subjectName
            if(atUniversity) {
                binding.classNumber.text = "Class $classNumber - University"
            } else {
                binding.classNumber.text = "Class $classNumber"
            }
            var iconAvailable = false
            val iconPack = (application as App).iconPack
            if(iconPack != null) {
                if(iconPack.getIcon(subjectIconId.toInt()) != null) {
                    binding.subjectImage.background = iconPack.getIcon(subjectIconId.toInt())!!.drawable
                    iconAvailable = true
                }
            }
            if(!iconAvailable) {
                // TODO - set default image
            }
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        getChapters()

        // TODO - loading screen if data not get yet
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[SubjectViewModel::class.java]
    }

    private fun getChapters() {
        viewModel.getChaptersForSubject(subjectId)
            .observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data != null) {
                                if(resource.data.isNotEmpty()) {
                                    binding.recyclerView.visibility = View.VISIBLE
                                    binding.subjectNoContent.visibility = View.GONE

                                    val adapter = ChapterAdapter(this, resource.data)
                                    adapter.addListener(this)

                                    binding.recyclerView.layoutManager = LinearLayoutManager(this)
                                    binding.recyclerView.adapter = adapter
                                } else {
                                    binding.recyclerView.visibility = View.GONE
                                    binding.subjectNoContent.visibility = View.VISIBLE
                                }
                            }
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    override fun getLessons(chapterId: Long, recyclerView: RecyclerView, noContent: View) {
        viewModel.getLessonsForChapter(chapterId)
            .observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            if(resource.data != null) {
                                if(resource.data.isNotEmpty()) {
                                    recyclerView.visibility = View.VISIBLE
                                    noContent.visibility = View.GONE

                                    val adapter = LessonAdapter(this, resource.data)
                                    adapter.addListener(this)

                                    recyclerView.layoutManager = LinearLayoutManager(this)
                                    recyclerView.adapter = adapter
                                } else {
                                    recyclerView.visibility = View.GONE
                                    noContent.visibility = View.VISIBLE
                                }
                            }
                        }
                        Status.ERROR -> {}
                        Status.LOADING -> {}
                    }
                }
            }
    }

    override fun openLesson(model: LessonModel) {
        val intent = Intent(this, LessonActivity::class.java)

        val bundle = Bundle()
        bundle.putString("lessonTitle", model.title)
        bundle.putString("lessonContent", model.text)
        intent.putExtras(bundle)

        startActivity(intent)
    }
}