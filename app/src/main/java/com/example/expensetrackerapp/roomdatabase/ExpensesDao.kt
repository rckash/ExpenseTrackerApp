package com.example.expensetrackerapp.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpensesDao {
    // Create
    @Insert
    fun addExpense(expenses: Expenses)

    // Read
    @Query ("SELECT * FROM expenses")
    fun getAllExpenses(): List<Expenses>

    // Update
    @Update
    fun updateExpense(expenses: Expenses)

    //Delete
    @Delete
    fun deleteExpense(expenses: Expenses)
}