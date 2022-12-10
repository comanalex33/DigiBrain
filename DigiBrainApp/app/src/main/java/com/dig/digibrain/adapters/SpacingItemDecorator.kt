package com.dig.digibrain.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecorator(var verticalSpace: Int, var horizintalSpace: Int = 0): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = verticalSpace
        outRect.left = horizintalSpace
        outRect.right = horizintalSpace
    }
}