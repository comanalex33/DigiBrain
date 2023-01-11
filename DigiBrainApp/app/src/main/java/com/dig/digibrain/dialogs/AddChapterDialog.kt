package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.databinding.DialogAddChapterBinding
import com.dig.digibrain.interfaces.IAddChapter

class AddChapterDialog: DialogFragment() {

    private lateinit var binding: DialogAddChapterBinding

    private lateinit var listener: IAddChapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAddChapterBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        binding.addButton.setOnClickListener {
            if(binding.chapterName.text.toString() == "") {
                binding.errorMessage.text = "Enter a chapter name"
                binding.errorMessage.visibility = View.VISIBLE
            } else {
                listener.addChapter(binding.chapterName.text.toString())
                dialog!!.dismiss()
            }
        }

        return binding.root
    }

    fun addListener(listener: IAddChapter) {
        this.listener = listener
    }

}