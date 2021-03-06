package com.droid.tolulope.week_9_task.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Post(
    var body: String? = null,
    var id: Int? = null,
    var title: String? = null,
    @PrimaryKey
    var userId: Int? = null,
    var liked : Boolean = false,
    var bookmarked : Boolean = false
)