package com.dig.digibrain.dialogs

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.adapters.LanguageSelectorAdapter
import com.dig.digibrain.databinding.DialogLanguagesBinding
import com.dig.digibrain.interfaces.ILanguageChanged
import com.dig.digibrain.models.LanguageModel

class ChooseLanguageDialog(var currentLanguageCode: String?, var languages: List<LanguageModel>): DialogFragment(), ILanguageChanged {

    private lateinit var binding: DialogLanguagesBinding
    private lateinit var listener: ILanguageChanged

//    private var selectedLanguageCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogLanguagesBinding.inflate(layoutInflater)

//        selectedLanguageCode = currentLanguageCode

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val languageAdapter = LanguageSelectorAdapter(requireContext(), currentLanguageCode, languages)
        languageAdapter.addListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.adapter = languageAdapter

        return binding.root
    }

    fun addListener(listener: ILanguageChanged) {
        this.listener = listener
    }

    override fun changeLanguage(languageCode: String) {
        listener.changeLanguage(languageCode)
        dialog!!.dismiss()
    }
}