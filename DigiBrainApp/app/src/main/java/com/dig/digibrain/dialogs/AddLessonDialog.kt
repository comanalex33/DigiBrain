package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.databinding.DialogAddLessonBinding
import com.dig.digibrain.interfaces.IAddLesson

class AddLessonDialog(var chapterId: Long): DialogFragment() {

    private lateinit var binding: DialogAddLessonBinding
    private lateinit var listener: IAddLesson

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAddLessonBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        binding.addButton.setOnClickListener {
            if(binding.lessonTitle.text.toString() == "") {
                binding.errorMessage.text = "Enter a lesson title"
                binding.errorMessage.visibility = View.VISIBLE
            } else if (binding.lessonText.text.toString() == "") {
                binding.errorMessage.text = "Enter lesson text"
                binding.errorMessage.visibility = View.VISIBLE
            } else {
                listener.addLesson(chapterId, binding.lessonTitle.text.toString(), binding.lessonText.text.toString())
                dialog!!.dismiss()
            }
        }

        return binding.root
    }

    fun addListener(listener: IAddLesson) {
        this.listener = listener
    }
}