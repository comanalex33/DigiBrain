package com.dig.digibrain.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.dig.digibrain.R
import com.dig.digibrain.adapters.SubjectSpinnerItemAdapter
import com.dig.digibrain.databinding.DialogBottomSheetBinding
import com.dig.digibrain.models.subject.SubjectModel

class BottomSheetDialog(var subjects: List<SubjectModel>): DialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: DialogBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBottomSheetBinding.inflate(layoutInflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        dialog?.window?.setGravity(Gravity.BOTTOM)
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation)

        val adapter = SubjectSpinnerItemAdapter(requireContext(), subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.subjectsSpinner.adapter = adapter
        binding.subjectsSpinner.onItemSelectedListener = this

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}