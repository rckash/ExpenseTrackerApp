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

    // Get data for this week
    @Query ("SELECT * FROM expenses WHERE dateInt BETWEEN :searchQueryfirst AND :searchQuerySecond")
    fun getAllExpensesSortedByWeek(searchQueryfirst: Int, searchQuerySecond: Int): List<Expenses>

    // Sort by Date Code
    @Query ("SELECT * FROM expenses WHERE dateInt LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllExpensesSortedByMonth(searchQuery: String): List<Expenses>

    // Get data by category and date
    @Query ("SELECT * FROM expenses WHERE category LIKE :searchQuery AND dateInt Like :date ORDER BY dateInt DESC")
    fun getAllExpensesSortedByCategoryDate(searchQuery: String, date: String): List<Expenses>

    // Get data by category and date span
    @Query ("SELECT * FROM expenses WHERE category LIKE :searchQuery AND dateInt BETWEEN :searchQueryFirst AND :searchQuerySecond ORDER BY dateInt DESC")
    fun getAllExpensesSortedByCategoryDateSpan(searchQuery: String, searchQueryFirst: String, searchQuerySecond: String): List<Expenses>

}