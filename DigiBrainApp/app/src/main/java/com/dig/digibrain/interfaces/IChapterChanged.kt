package com.dig.digibrain.interfaces

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface IChapterChanged {
    fun getLessons(chapterId: Long, recyclerView: RecyclerView, noContent: View)
}