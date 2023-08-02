package com.example.expensetrackerapp.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GoalsDao {
    // Create
    @Insert
    fun addGoal(goals: Goals)

    // Read
    @Query("SELECT * FROM goals")
    fun getAllGoals(): List<Goals>

    // Update
    @Update
    fun updateGoal(goals: Goals)

    //Delete
    @Delete
    fun deleteGoal(goals: Goals)
}