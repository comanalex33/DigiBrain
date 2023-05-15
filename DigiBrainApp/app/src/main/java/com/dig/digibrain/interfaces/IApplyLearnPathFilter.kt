package com.dig.digibrain.interfaces

interface IApplyLearnPathFilter: IClassChanged {
    fun applyFilter(subjectIds: List<Long>, name: String)
}