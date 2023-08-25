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
    @Query ("SELECT * FROM expenses ORDER BY dateInt DESC")
    fun getAllExpenses(): List<Expenses>

    // Update
    @Update
    fun updateExpense(expenses: Expenses)

    // Delete
    @Delete
    fun deleteExpense(expenses: Expenses)

    // Sort by Month
    @Query ("SELECT * FROM expenses WHERE dateInt LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllExpensesSortedByMonth(searchQuery: String): List<Expenses>

    // Get data by category
    @Query ("SELECT * FROM expenses WHERE category LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllExpensesSortedByCategory(searchQuery: String): List<Expenses>

}