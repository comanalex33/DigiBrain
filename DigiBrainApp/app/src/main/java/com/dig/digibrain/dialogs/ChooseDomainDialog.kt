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

class ChooseDomainDialog(var application: Application,  var currentDomain: DomainModel?, var atUniversity: Boolean): DialogFragment(), IDomainChanged {

    private lateinit var binding: DialogChooseDomainBinding

    private lateinit var domainAdapter: DomainAdapter
    private var selectedDomain: DomainModel? = null

    private lateinit var listener: IDomainChanged

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogChooseDomainBinding.inflate(layoutInflater)

        selectedDomain = currentDomain

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)

        val domains = listOf(
            DomainModel(1, "Domain 1", 5, true),
            DomainModel(2, "Domain 2", 57, true),
            DomainModel(3, "Domain 3", 8, true),
            DomainModel(4, "Domain 1", 5, false),
            DomainModel(5, "Domain 2", 57, false),
            DomainModel(6, "Domain 3", 8, true),
            DomainModel(7, "Domain 1", 5, true),
            DomainModel(8, "Domain 2", 57, true),
            DomainModel(9, "Domain 3", 8, true)
        )

        val domainsForRecyclerView = getDomainsForRecyclerView(domains)

        if (domainsForRecyclerView.isNotEmpty()) {
            domainAdapter = DomainAdapter(
                activity as LearnActivity,
                application,
                this,
                currentDomain,
                domainsForRecyclerView
            )
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
            binding.recyclerView.addItemDecoration(SpacingItemDecorator(30, 10))
            binding.recyclerView.adapter = domainAdapter
        } else {
            binding.errorMessage.text = resources.getString(R.string.no_domains_defined_for_this_class)
            binding.errorMessage.visibility = View.VISIBLE
            binding.selectButton.text = "Ok"
        }

        binding.selectButton.setOnClickListener {
            if (domainsForRecyclerView.isNotEmpty()) {
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

    private fun getDomainsForRecyclerView(list: List<DomainModel>): List<DomainModel> {
        val filteredList = ArrayList<DomainModel>()
        for(domain in list) {
            if(domain.atUniversity == this.atUniversity) {
                filteredList.add(domain)
            }
        }
        return filteredList
    }
}