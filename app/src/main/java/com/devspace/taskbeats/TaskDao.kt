package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {

    @Query("SELECT * FROM taskentity")
    fun getAll(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(taskEntities: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertORupdate(taskEntities: TaskEntity)

    @Delete
    fun deletetask(taskEntities: TaskEntity)

}