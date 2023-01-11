package com.dig.digibrain.activities

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dig.digibrain.databinding.ActivityProfileBinding
import com.dig.digibrain.databinding.ActivitySettingsBinding
import com.dig.digibrain.dialogs.AcceptDialog
import com.dig.digibrain.dialogs.ChooseClassDialog
import com.dig.digibrain.dialogs.ChooseDomainDialog
import com.dig.digibrain.dialogs.LoadingDialog
import com.dig.digibrain.interfaces.IAcceptListener
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnViewModel
import com.dig.digibrain.viewModels.ProfileViewModel
import com.dig.digibrain.viewModels.ViewModelFactory

class ProfileActivity : AppCompatActivity(), IClassChanged, IDomainChanged, IAcceptListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sessionManager: SessionManager

    private lateinit var acceptDialog: AcceptDialog

    private var currentClass: Int? = null
    private var selectedClass: Int? = null
    private var currentDomain: DomainModel? = null
    private var selectedDomain: DomainModel? = null
    private var currentAtUniversity: Boolean = false
    private var atUniversity: Boolean = false

    private var classChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        sessionManager = SessionManager(applicationContext)

        getUserData()

        binding.editClass.setOnClickListener {
            val dialog = ChooseClassDialog(selectedClass, atUniversity)
            dialog.addListener(this)
            dialog.show(this.supportFragmentManager, "Choose class")
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.updateButton.setOnClickListener {
            updateClassNumber()
        }

        binding.deleteButton.setOnClickListener {
            acceptDialog = AcceptDialog("Are you sure you want to delete this account?")
            acceptDialog.addListener(this)
            acceptDialog.show(this.supportFragmentManager, "Accept delete account")
        }
    }

    private fun getUserData() {
        val username = sessionManager.getUserName()
        if(username != null) {
            val loadingDialog = LoadingDialog(this)
            viewModel.getUserDetailsByUsername(username)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                loadingDialog.dismiss()
                                if(resource.data != null) {
                                    val email = resource.data.email
                                    val classId = resource.data.classId
                                    getClass(email, classId)
                                }
                            }
                            Status.ERROR -> {
                                loadingDialog.dismiss()
                            }
                            Status.LOADING -> {
                                loadingDialog.show()
                            }
                        }
                    }
                }
        }
    }

    private fun getClass(email: String, classId: Long) {
        val loadingDialog = LoadingDialog(this)
        viewModel.getClassById(classId)
            .observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            loadingDialog.dismiss()
                            if(resource.data != null) {
                                val classNumber = resource.data!!.number
                                selectedClass = classNumber
                                currentClass = classNumber
                                atUniversity = resource.data.atUniversity
                                currentAtUniversity = resource.data.atUniversity
                                updateFields(email, classNumber)
                                getDomain(resource.data.domainId)
                            }
                        }
                        Status.ERROR -> {
                            loadingDialog.dismiss()
                        }
                        Status.LOADING -> {
                            loadingDialog.show()
                        }
                    }
                }
            }
    }

    private fun getDomain(id: Long) {
        if(id != 0L) {
            viewModel.getDomainById(id)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                if (resource.data != null) {
                                    selectedDomain = resource.data
                                    currentDomain = resource.data
                                }
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

    private fun updateFields(email: String, classNumber: Int) {
        binding.username.text = sessionManager.getUserName()
        binding.email.text = email
        binding.role.text = sessionManager.getUserRole()
        binding.classNumber.text = classNumber.toString()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(ApiClient.getService())
        )[ProfileViewModel::class.java]
    }

    override fun changeClass(classNumber: Int, isUniversity: Boolean) {
        selectedClass = classNumber
        atUniversity = isUniversity

        if(classNumber > 8 || isUniversity) {
            val dialog = ChooseDomainDialog(application, selectedDomain, selectedClass!!, isUniversity, 2)
            dialog.addListener(this)
            dialog.setViewModel(viewModel)
            dialog.show(this.supportFragmentManager, "Choose domain")
        } else {
            binding.classNumber.text = classNumber.toString()
            selectedDomain = null
            setClassStatus()
        }
    }

    override fun changeDomain(value: DomainModel) {
        selectedDomain = value
        binding.classNumber.text = selectedClass.toString()
        setClassStatus()
    }

    private fun setClassStatus() {
        if(currentClass == selectedClass && currentDomain == selectedDomain && currentAtUniversity == atUniversity) {
            classChanged = false
            binding.classNumber.setTextColor(Color.BLACK)
        } else {
            classChanged = true
            binding.classNumber.setTextColor(Color.GREEN)
        }
    }

    private fun updateClassNumber() {
        if(classChanged) {
            val domainId = if(selectedDomain != null) selectedDomain!!.id else 0
            viewModel.getClassByNumberAndDomain(selectedClass!!, atUniversity, domainId)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                if (resource.data != null) {
                                    updateClassNumberAfterClassFinding(resource.data.id)
                                }
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

    private fun updateClassNumberAfterClassFinding(id: Long) {
        val username = sessionManager.getUserName()
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(username != null && authToken != null) {
            viewModel.updateClass(authToken, username, id)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                if (resource.data != null) {
                                    currentClass = selectedClass
                                    currentDomain = selectedDomain
                                    currentAtUniversity = atUniversity
                                    setClassStatus()
                                    Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING -> {
                            }
                        }
                    }
                }
        }
    }

    override fun disableErrorMessage() {}

    // Accept to delete account
    override fun accept() {
        val username = sessionManager.getUserName()
        val authToken: String? = sessionManager.getBearerAuthToken()

        if(username != null && authToken != null) {
            viewModel.deleteAccount(authToken, username)
                .observe(this) {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            if (resource.data != null) {
                                val intent = Intent(applicationContext, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)     // Finish all activities
                                startActivity(intent)
                            }
                        }
                        Status.ERROR -> {
                            Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            }
        }

    }

    // Refuse to delete account
    override fun reject() {
        acceptDialog.dismiss()
    }
}