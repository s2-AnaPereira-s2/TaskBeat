package com.devspace.taskbeats

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val task: String,
    val category: String,
)