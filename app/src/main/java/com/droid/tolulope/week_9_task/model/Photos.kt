package com.droid.tolulope.week_9_task.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photos(
    val albumId: Int,
    @PrimaryKey
    val id: Int,
    val thumbnailUrl: String,
    val title: String,
    val url: String
)