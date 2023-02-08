package com.dig.digibrain.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityProfileBinding
import com.dig.digibrain.dialogs.AcceptDialog
import com.dig.digibrain.dialogs.ChooseClassDialog
import com.dig.digibrain.dialogs.ChooseDomainDialog
import com.dig.digibrain.dialogs.LoadingDialog
import com.dig.digibrain.interfaces.IAcceptListener
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.models.UserModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.services.SessionManager
import com.dig.digibrain.services.server.ApiClient
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.ProfileViewModel
import com.dig.digibrain.viewModels.ViewModelFactory
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

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
    private var profileImageName: String? = null

    private var filePart: MultipartBody.Part? = null
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

        binding.editImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1)
        }

        binding.updateButton.setOnClickListener {
            updateClassNumber()
            changeProfilePicture()
        }

        binding.deleteButton.setOnClickListener {
            acceptDialog = AcceptDialog("Are you sure you want to delete this account?")
            acceptDialog.addListener(this)
            acceptDialog.show(this.supportFragmentManager, "Accept delete account")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                filePart = getMultipartFromUri(uri)
                binding.profileImage.setImageURI(uri)
                // Make the API call to upload the image
            }
        }
    }

    private fun getMultipartFromUri(uri: Uri): MultipartBody.Part {
        val inputStream = contentResolver.openInputStream(uri)
        val file = File.createTempFile("image", "jpg")
        file.outputStream().use { inputStream?.copyTo(it) }
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
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
                                    profileImageName = resource.data.profileImageName
                                    getClass(email, classId)

                                    val scope = CoroutineScope(Dispatchers.Main)
                                    scope.launch {
                                        if(profileImageName != null) {
                                            val obj = getProfilePicture(profileImageName!!)
                                            if(obj != null) {
                                                binding.profileImage.setImageBitmap(obj)
                                                binding.profileImage.visibility = View.VISIBLE
                                                binding.loadingProfileImage.visibility = View.GONE
                                            } else {
                                                binding.profileImage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_profile, null))
                                                binding.profileImage.visibility = View.VISIBLE
                                                binding.loadingProfileImage.visibility = View.GONE
                                                Toast.makeText(applicationContext, "Profile image couldn't be downloaded", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            binding.profileImage.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_profile, null))
                                            binding.profileImage.visibility = View.VISIBLE
                                            binding.loadingProfileImage.visibility = View.GONE
                                        }
                                    }
                                }
                            }
                            Status.ERROR -> {
                                Toast.makeText(applicationContext, resource.message, Toast.LENGTH_SHORT).show()
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
        if(classId != 0L) {
            viewModel.getClassById(classId)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                if (resource.data != null) {
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
                            }
                            Status.LOADING -> {
                            }
                        }
                    }
                }
        } else {
            updateFields(email, 0)
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
        if(classNumber != 0)
            binding.classNumber.text = classNumber.toString()
        else
            binding.classNumber.text = "-"
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
            binding.classNumber.setTextColor(resources.getColor(R.color.dark_gray, theme))
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
            Toast.makeText(applicationContext, "$id", Toast.LENGTH_SHORT).show()
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
                                Toast.makeText(applicationContext, "haha", Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING -> {
                            }
                        }
                    }
                }
        }
    }

    private fun changeProfilePicture() {
        val username = sessionManager.getUserName()

        if(filePart != null && username != null) {
            viewModel.changeProfilePicture(username, filePart!!)
                .observe(this) {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                Toast.makeText(applicationContext, "Image updated", Toast.LENGTH_SHORT).show()
                            }
                            Status.ERROR -> {
                                Toast.makeText(applicationContext, "Image couldn't be uploaded", Toast.LENGTH_SHORT).show()
                            }
                            Status.LOADING -> {
                            }
                        }
                    }
                }
        }
    }

    private suspend fun getProfilePicture(objectKey: String): Bitmap? {

        return withContext(Dispatchers.IO) {
            val objectStorageInfo = sessionManager.getObjectStorageInfo() ?: return@withContext null
            val credentials = BasicAWSCredentials(objectStorageInfo.readAccessKey, objectStorageInfo.readSecretKey)
            val s3Client = AmazonS3Client(credentials, Region.getRegion(objectStorageInfo.bucketRegion))

            // Create a GetObjectRequest
            val getObjectRequest = GetObjectRequest(objectStorageInfo.bucketName, objectKey)

            // Download the object
            val obj: S3Object
            try {
                obj = s3Client.getObject(getObjectRequest)
            } catch (e: Exception) {
                return@withContext null
            }
            return@withContext BitmapFactory.decodeStream(obj.objectContent)
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