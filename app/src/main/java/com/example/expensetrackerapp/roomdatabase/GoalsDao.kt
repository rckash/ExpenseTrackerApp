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
    fun addExpense(goals: Goals)

    // Read
    @Query("SELECT * FROM goals")
    fun getAllExpenses(): List<Goals>

    // Update
    @Update
    fun updateExpense(goals: Goals)

    //Delete
    @Delete
    fun deleteExpense(goals: Goals)
}