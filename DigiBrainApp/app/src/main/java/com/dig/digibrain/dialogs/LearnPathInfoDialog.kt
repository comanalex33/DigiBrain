package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.databinding.DialogLearnPathInfoBinding
import com.dig.digibrain.models.learnPaths.LearnPathDetailedModel
import com.dig.digibrain.utils.Helper
import com.dig.digibrain.utils.Helper.Companion.getInitials

class LearnPathInfoDialog(var model: LearnPathDetailedModel): DialogFragment() {

    private lateinit var binding: DialogLearnPathInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogLearnPathInfoBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        initializeUIComponents()

        return binding.root
    }

    private fun initializeUIComponents() {
        binding.title.text = model.title
        binding.learnPathInitials.text = model.title.getInitials()
        binding.authorValue.text = model.author
        binding.updatedValue.text = Helper.convertTimestampToDateFormat(model.date)
        binding.subjectValue.text = model.subject
        binding.descriptionValue.text = model.description
    }
}