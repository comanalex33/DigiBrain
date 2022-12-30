package com.dig.digibrain.dialogs

import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dig.digibrain.R
import com.dig.digibrain.activities.LearnActivity
import com.dig.digibrain.adapters.DomainAdapter
import com.dig.digibrain.adapters.SpacingItemDecorator
import com.dig.digibrain.databinding.DialogChooseDomainBinding
import com.dig.digibrain.interfaces.IClassChanged
import com.dig.digibrain.interfaces.IDomainChanged
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.utils.Status
import com.dig.digibrain.viewModels.LearnViewModel

class ChooseDomainDialog(
    var application: Application,
    var currentDomain: DomainModel?,
    var classNumber: Int,
    var atUniversity: Boolean,
    var languageId: Long): DialogFragment(), IDomainChanged {

    private lateinit var binding: DialogChooseDomainBinding
    private lateinit var listener: IDomainChanged
    private lateinit var viewModel: LearnViewModel

    private lateinit var domainAdapter: DomainAdapter
    private var selectedDomain: DomainModel? = null
    private var domainsUpdated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChooseDomainBinding.inflate(layoutInflater)

        selectedDomain = currentDomain

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        updateDomains()

        binding.selectButton.setOnClickListener {
            if (domainsUpdated) {
                if (selectedDomain == null) {
                    binding.errorMessage.text = resources.getString(R.string.select_domain)
                    binding.errorMessage.visibility = View.VISIBLE
                } else {
                    listener.changeDomain(selectedDomain!!)
                    dialog!!.dismiss()
                }
            } else {
                dialog!!.dismiss()
            }
        }

        return binding.root
    }

    override fun changeDomain(value: DomainModel) {
        selectedDomain = value
    }

    override fun disableErrorMessage() {
        binding.errorMessage.visibility = View.GONE
    }

    fun addListener(listener: IDomainChanged) {
        this.listener = listener
    }

    fun setViewModel(viewModel: LearnViewModel) {
        this.viewModel = viewModel
    }

    private fun setDomains(domains: List<DomainModel>) {
        domainAdapter = DomainAdapter(
            activity as LearnActivity,
            application,
            this,
            currentDomain,
            domains
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        binding.recyclerView.addItemDecoration(SpacingItemDecorator(30, 10))
        binding.recyclerView.adapter = domainAdapter
    }

    private fun setMessageForNoDomains(message: String) {
        binding.errorMessage.text = message
        binding.errorMessage.visibility = View.VISIBLE
        binding.selectButton.text = "Ok"
    }

    private fun updateDomains() {
        viewModel.getDomainsForClass(classNumber, atUniversity, languageId).observe(this) {
            it.let { resource ->
                when(resource.status) {
                    Status.SUCCESS -> {
                        if(resource.data != null) {
                            if(resource.data.isNotEmpty()) {
                                domainsUpdated = true
                                setDomains(resource.data)
                            } else {
                                setMessageForNoDomains(resources.getString(R.string.no_domains_defined_for_this_class))
                            }
                        } else {
                            setMessageForNoDomains(resources.getString(R.string.no_domains_defined_for_this_class))
                        }
                    }
                    Status.ERROR -> {
                        setMessageForNoDomains(resource.message!!)
                    }
                    Status.LOADING -> {
                        //TODO - Loading dialog
                    }
                }
            }
        }
    }
}