package com.dig.digibrain.interfaces

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.dig.digibrain.adapters.ChapterAdapter

interface IChapterChanged {
    fun getLessons(chapterId: Long, recyclerView: RecyclerView, noContent: View)
    fun addLesson(chapterId: Long, title: String, text: String, adapter: ChapterAdapter, list: View, noList: View)
}