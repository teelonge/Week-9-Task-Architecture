package com.droid.tolulope.week_9_task.utils

import android.view.View
import com.droid.tolulope.week_9_task.model.Post

// Interface for handling click events of recyclerView contents
interface RecyclerViewClickListener {
    fun onRecyclerViewItemClicked(view : View, post : Post)
}