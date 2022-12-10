package com.dig.digibrain.interfaces

import com.dig.digibrain.models.subject.DomainModel

interface IDomainChanged {
    fun changeDomain(value: DomainModel)
    fun disableErrorMessage()
}