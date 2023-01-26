package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.R
import com.dig.digibrain.databinding.DialogChooseClassBinding
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.utils.Helper.Companion.forAllChildren

class ChooseClassDialog(var currentClass: Int?, var isUniversity: Boolean): DialogFragment() {

    private lateinit var binding: DialogChooseClassBinding
    private var currentSelectedButton: Button? = null

    private lateinit var listener: IClassChanged

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChooseClassBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        binding.root.forAllChildren { view ->
            if(view.id == binding.selectButton.id)
                return@forAllChildren
            when(view) {
                is Button -> {
                    // Set current value
                    currentClass?.apply {
                        if(isUniversity) {
                            if(getClassNumberIfUniversity(view.text.toString()) == this) {
                                view.backgroundTintList = AppCompatResources.getColorStateList(
                                    requireContext(),
                                    R.color.blue_light
                                )
                                currentSelectedButton = view
                            }
                        } else {
                            if (view.text == this.toString()) {
                                view.backgroundTintList = AppCompatResources.getColorStateList(
                                    requireContext(),
                                    R.color.blue_light
                                )
                                currentSelectedButton = view
                            }
                        }
                    }
                    // Update value
                    view.setOnClickListener {
                        binding.errorMessage.visibility = View.GONE
                        if (currentSelectedButton != null) {
                            currentSelectedButton!!.backgroundTintList =
                                AppCompatResources.getColorStateList(requireContext(), R.color.gray)
                        }
                        currentSelectedButton = view
                        view.backgroundTintList = AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.blue_light
                        )
                    }
                }
            }
        }

        binding.selectButton.setOnClickListener {
            if(currentSelectedButton == null) {
                binding.errorMessage.text = resources.getString(R.string.select_class_number)
                binding.errorMessage.visibility = View.VISIBLE
            } else {
                if(currentSelectedButton!!.text.all { char -> char.isDigit() }) {
                    val classNumber = Integer.parseInt(currentSelectedButton!!.text.toString())
                    listener.changeClass(classNumber = classNumber, isUniversity = false)
                } else {
                    val classNumber = getClassNumberIfUniversity(currentSelectedButton!!.text.toString())
                    listener.changeClass(classNumber = classNumber, isUniversity = true)
                }
                dialog!!.dismiss()
            }
        }

        return binding.root
    }

    private fun getClassNumberIfUniversity(number: String): Int {
        return when(number) {
            "I" -> 1
            "II" -> 2
            "III" -> 3
            "IV" -> 4
            "V" -> 5
            "VI" -> 6
            else -> 0
        }
    }

    fun addListener(listener: IClassChanged) {
        this.listener = listener
    }

}