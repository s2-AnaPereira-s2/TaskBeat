package com.devspace.taskbeats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "isSelected") val isSelected: Boolean
)


