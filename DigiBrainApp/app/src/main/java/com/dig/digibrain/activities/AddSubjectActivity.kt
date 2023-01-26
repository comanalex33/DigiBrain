package com.dig.digibrain.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.App
import com.dig.digibrain.databinding.ActivityAddSubjectBinding
import com.dig.digibrain.models.postModels.subject.SubjectPostModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.AddSubjectViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack

class AddSubjectActivity : AppCompatActivity(), IconDialog.Callback {

    private lateinit var binding: ActivityAddSubjectBinding
    private lateinit var viewModel: AddSubjectViewModel
    private lateinit var sessionManager: SessionManager

    private var classId: Long? = null
    private var selectedIconId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        sessionManager = SessionManager(applicationContext)

        val bundle = intent.extras
        if(bundle != null) {
            classId = bundle.getLong("classId")
        } else {
            Toast.makeText(this, "Class could not be extracted", Toast.LENGTH_SHORT).show()
        }

        val iconDialog = supportFragmentManager.findFragmentByTag("icon-dialog") as IconDialog?
            ?: IconDialog.newInstance(IconDialogSettings())
        iconDialog.settings = IconDialogSettings {
            searchVisibility = IconDialog.SearchVisibility.ALWAYS
            maxSelection = 1
        }

        binding.chooseIcon.setOnClickListener {
            iconDialog.show(supportFragmentManager, "icon-dialog")
        }

        binding.addSubjectButton.setOnClickListener {
            if(binding.subjectTitle.text.toString() == "") {
                Toast.makeText(applicationContext, "No subject title", Toast.LENGTH_SHORT).show()
            }
            if(classId != null && selectedIconId != null) {
                addSubject()
            }
        }

        binding.backArrow.setOnClickListener {
            val intent = Intent(this, LearnActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[AddSubjectViewModel::class.java]
    }

    private fun addSubject() {
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(authToken != null) {
            val languageId = this.getSharedPreferences("application", Context.MODE_PRIVATE)
                .getLong("languageId", 2)
            val model = SubjectPostModel(
                name = binding.subjectTitle.text.toString(),
                iconId = selectedIconId!!.toLong(),
                classId = classId!!,
                languageId = languageId
            )
            viewModel.addSubject(authToken, model)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                val intent = Intent(this, LearnActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            Status.ERROR -> {
                                Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING -> {}
                        }
                    }
                }
        }
    }

    override val iconDialogIconPack: IconPack?
        get() = (application as App).iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        binding.iconFrame.background = icons[0].drawable
        selectedIconId = icons[0].id
    }
}