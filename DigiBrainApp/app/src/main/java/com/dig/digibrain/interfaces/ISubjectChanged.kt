package com.dig.digibrain.interfaces

import com.dig.digibrain.models.subject.SubjectModel

interface ISubjectChanged {
    fun changeSubject(value: SubjectModel)
    fun disableErrorMessage()
}