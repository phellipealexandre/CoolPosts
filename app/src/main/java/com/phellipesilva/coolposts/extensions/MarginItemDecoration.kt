package com.phellipesilva.coolposts.extensions

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.phellipesilva.coolposts.R

class MarginItemDecoration : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val margin = view.context.resources.getDimension(R.dimen.recyclerview_margin).toInt()

        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = margin
            }
            left =  margin
            right = margin
            bottom = margin
        }
    }
}