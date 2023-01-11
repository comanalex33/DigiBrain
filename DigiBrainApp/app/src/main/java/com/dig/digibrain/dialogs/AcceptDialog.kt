package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.databinding.DialogAcceptBinding
import com.dig.digibrain.interfaces.IAcceptListener

class AcceptDialog(var message: String): DialogFragment() {

    private lateinit var binding: DialogAcceptBinding
    private lateinit var listener: IAcceptListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAcceptBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        binding.messageText.text = message

        binding.yesButton.setOnClickListener {
            listener.accept()
        }

        binding.noButton.setOnClickListener {
            listener.reject()
        }

        return binding.root
    }

    fun addListener(listener: IAcceptListener) {
        this.listener = listener
    }
}