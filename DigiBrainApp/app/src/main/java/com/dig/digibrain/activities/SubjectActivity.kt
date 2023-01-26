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
import com.dig.digibrain.dialogs.AddChapterDialog
import com.dig.digibrain.interfaces.IAddChapter
import com.dig.digibrain.interfaces.IAddLesson
import com.dig.digibrain.interfaces.IChapterChanged
import com.dig.digibrain.interfaces.ILessonSelected
import com.dig.digibrain.models.postModels.subject.ChapterPostModel
import com.dig.digibrain.models.postModels.subject.LessonPostModel
import com.dig.digibrain.models.subject.ChapterModel
import com.dig.digibrain.models.subject.LessonModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnViewModel
import com.dig.digibrain.viewModels.SubjectViewModel
import com.dig.digibrain.viewModels.ViewModelFactory


class SubjectActivity : AppCompatActivity(), IChapterChanged, ILessonSelected, IAddChapter {

    private lateinit var binding: ActivitySubjectBinding
    private lateinit var viewModel: SubjectViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var chapterAdapter: ChapterAdapter

    private var chapterList: MutableList<ChapterModel>? = mutableListOf()

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
        sessionManager = SessionManager(applicationContext)
        setupVisibility()

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

        binding.addChapterButton.setOnClickListener {
            val dialog = AddChapterDialog()
            dialog.addListener(this)
            dialog.show(this.supportFragmentManager, "Add chapter")
        }

        getChapters()

        // TODO - loading screen if data not get yet
    }

    private fun setupVisibility() {
        if(sessionManager.getUserRole() == "admin" || sessionManager.getUserRole() == "teacher") {
            binding.addChapterButton.visibility = View.VISIBLE
        }
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
                                    chapterList = resource.data.toMutableList()
                                } else {
                                    binding.recyclerView.visibility = View.GONE
                                    binding.subjectNoContent.visibility = View.VISIBLE
                                }

                                chapterAdapter = ChapterAdapter(this, sessionManager.getUserRole() == "admin" || sessionManager.getUserRole() == "teacher", chapterList!!)
                                chapterAdapter.addListener(this)
                                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                                binding.recyclerView.adapter = chapterAdapter
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
                                    chapterAdapter.addChapterLessons(chapterId, resource.data)

                                    recyclerView.layoutManager = LinearLayoutManager(this)
                                    recyclerView.adapter = adapter
                                } else {
                                    recyclerView.visibility = View.GONE
                                    noContent.visibility = View.VISIBLE

                                    val adapter = LessonAdapter(this, mutableListOf())
                                    adapter.addListener(this)
                                    chapterAdapter.addChapterLessons(chapterId, mutableListOf())

                                    recyclerView.layoutManager = LinearLayoutManager(this)
                                    recyclerView.adapter = adapter
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

    override fun addChapter(name: String) {
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(authToken != null && chapterList != null && subjectId != 0L) {
            val model = ChapterPostModel(name, chapterList!!.size.toLong() + 1, subjectId)
            viewModel.addChapter(authToken, model).observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            chapterList!!.add(resource.data!!)
                            if(binding.recyclerView.visibility == View.GONE) {
                                binding.recyclerView.visibility = View.VISIBLE
                                binding.subjectNoContent.visibility = View.GONE
                            }
                            chapterAdapter.notifyDataSetChanged()
                        }
                        Status.ERROR -> {

                        }
                        Status.LOADING -> {

                        }
                    }
                }
            }
        }
    }

    override fun addLesson(chapterId: Long, title: String, text: String, adapter: ChapterAdapter, list: View, noList: View) {
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(authToken != null) {
            val model = LessonPostModel(title, text, chapterId)
            viewModel.addLesson(authToken, model).observe(this) {
                it.let { resource ->
                    when(resource.status) {
                        Status.SUCCESS -> {
                            adapter.addChapterOneLesson(chapterId, resource.data!!)
                            if(list.visibility == View.GONE) {
                                list.visibility = View.VISIBLE
                                noList.visibility = View.GONE
                            }
                            adapter.notifyDataSetChanged()
                        }
                        Status.ERROR -> {

                        }
                        Status.LOADING -> {

                        }
                    }
                }
            }
        }
    }
}