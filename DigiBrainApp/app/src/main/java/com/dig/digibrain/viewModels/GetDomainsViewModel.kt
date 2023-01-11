package com.dig.digibrain.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dig.digibrain.models.subject.DomainModel
import com.dig.digibrain.utils.Resource

abstract class GetDomainsViewModel: ViewModel() {
    abstract fun getDomainsForClass(number: Int, atUniversity: Boolean, languageId: Long): LiveData<Resource<List<DomainModel>?>>
}