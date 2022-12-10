package com.dig.digibrain.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.dig.digibrain.R
import com.dig.digibrain.databinding.ActivityLearnBinding
import com.dig.digibrain.dialogs.ChooseClassDialog
import com.dig.digibrain.dialogs.ChooseDomainDialog
import com.dig.digibrain.dialogs.ChooseSubjectDialog
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.interfaces.ISubjectChanged
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.models.subject.SubjectModel

class LearnActivity : AppCompatActivity(), IClassChanged, IDomainChanged, ISubjectChanged {

    private lateinit var binding: ActivityLearnBinding

    private var selectedClass: Int? = null
    private var isUniversity: Boolean = false
    private var selectedDomain: DomainModel? = null
    private var selectedSubject: SubjectModel? = null

    private var domainClickable: Boolean = false
    private var subjectClickable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.chooseClassButton.setOnClickListener {
            val dialog = ChooseClassDialog(selectedClass, isUniversity)
            dialog.addListener(this)
            dialog.show(this.supportFragmentManager, "Choose class")
        }

        binding.chooseDomainButton.setOnClickListener {
            if(domainClickable) {
                val dialog = ChooseDomainDialog(application, selectedDomain, isUniversity)
                dialog.addListener(this)
                dialog.show(this.supportFragmentManager, "Choose domain")
            }
        }

        binding.chooseSubjectButton.setOnClickListener {
            if(subjectClickable) {
                selectedClass?.apply {
                    val dialog = ChooseSubjectDialog(application, selectedSubject, selectedDomain, this, isUniversity)
                    dialog.addListener(this@LearnActivity)
                    dialog.show(this@LearnActivity.supportFragmentManager, "Choose subject")
                }
//                if(!isUniversity) {
//                    val dialog = ChooseSubjectDialog(application, selectedSubject,)
//                    dialog.addListener(this@LearnActivity)
//                    dialog.show(this@LearnActivity.supportFragmentManager, "Choose subject")
//                } else {
//                    selectedDomain?.apply {
//                        val dialog = ChooseSubjectDialog(application, selectedSubject, this.id)
//                        dialog.addListener(this@LearnActivity)
//                        dialog.show(this@LearnActivity.supportFragmentManager, "Choose subject")
//                    }
//                }
            }
        }
    }

    override fun changeClass(classNumber: Int, isUniversity: Boolean) {
        this.selectedClass = classNumber
        this.isUniversity = isUniversity

        // Update text fields
        binding.chooseClassText.text = "${resources.getString(R.string.class_)}:"
        binding.chooseClassValue.text = classNumber.toString()

        // Change Domain visibility (only visible if High-school or University)
        if(isUniversity) {
            binding.chooseDomainButton.visibility = View.VISIBLE

            // Disable subject selection
            subjectClickable = false
            setViewClickable(binding.chooseSubjectButton, false)
        } else if (classNumber > 8) {
            binding.chooseDomainButton.visibility = View.VISIBLE

            // Disable subject selection
            subjectClickable = false
            setViewClickable(binding.chooseSubjectButton, false)
        } else {
            binding.chooseDomainButton.visibility = View.GONE

            // Enable subject selection
            subjectClickable = true
            setViewClickable(binding.chooseSubjectButton, true)
        }

        // Enable domain selection
        domainClickable = true
        setViewClickable(binding.chooseDomainButton, true)

        // Initialize domain text fields
        binding.chooseDomainText.text = resources.getString(R.string.choose_domain)
        binding.chooseDomainValue.text = ""
        selectedDomain = null

        // Initialize subject text fields
        binding.chooseSubjectText.text = resources.getString(R.string.choose_subject)
        binding.chooseSubjectValue.text = ""
        selectedSubject = null
    }

    override fun changeDomain(value: DomainModel) {
        selectedDomain = value

        // Enable subject selection
        subjectClickable = true
        setViewClickable(binding.chooseSubjectButton, true)

        // Update text fields
        binding.chooseDomainText.text = "${resources.getString(R.string.domain)}:"
        binding.chooseDomainValue.text = value.name

        // Initialize subject text fields
        binding.chooseSubjectText.text = resources.getString(R.string.choose_subject)
        binding.chooseSubjectValue.text = ""
        selectedSubject = null
    }

    override fun disableErrorMessage() {}

    override fun changeSubject(value: SubjectModel) {
        selectedSubject = value

        // Update text fields
        binding.chooseSubjectText.text = "${resources.getString(R.string.subject)}:"
        binding.chooseSubjectValue.text = value.name
    }

    private fun setViewClickable(view: View, isClickable: Boolean) {
        if(isClickable) {
            view.backgroundTintList = AppCompatResources.getColorStateList(
                applicationContext,
                R.color.gray
            )
        } else {
            view.backgroundTintList = AppCompatResources.getColorStateList(
                applicationContext,
                R.color.light_gray
            )
        }
    }
}