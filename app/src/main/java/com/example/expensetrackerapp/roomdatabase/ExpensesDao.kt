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

    // Read Expenses
    @Query ("SELECT * FROM expenses WHERE isExpense = 1 ORDER BY dateInt DESC")
    fun getAllExpenses(): List<Expenses>

    // Read Income
    @Query ("SELECT * FROM expenses WHERE isExpense = 0 ORDER BY dateInt DESC")
    fun getAllIncome(): List<Expenses>

    // Read Expenses
    @Query ("SELECT * FROM expenses ORDER BY dateInt DESC")
    fun getAllExpenseAndIncome(): List<Expenses>

    // Read Income
    @Query ("SELECT * FROM expenses WHERE isExpense = 0 AND dateInt LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllIncomeSortedbyMonth(searchQuery: String): List<Expenses>

    // Update
    @Update
    fun updateExpense(expenses: Expenses)

    // Delete
    @Delete
    fun deleteExpense(expenses: Expenses)

    // Get data for this week
    @Query ("SELECT * FROM expenses WHERE isExpense = 1 AND dateInt BETWEEN :searchQueryfirst AND :searchQuerySecond")
    fun getAllExpensesSortedByWeek(searchQueryfirst: Int, searchQuerySecond: Int): List<Expenses>

    // Sort by Date Code
    @Query ("SELECT * FROM expenses WHERE isExpense = 1 AND dateInt LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllExpensesSortedByMonth(searchQuery: String): List<Expenses>

    // Get data by category and date
    @Query ("SELECT * FROM expenses WHERE isExpense = 1 AND category LIKE :searchQuery AND dateInt Like :date ORDER BY dateInt DESC")
    fun getAllExpensesSortedByCategoryDate(searchQuery: String, date: String): List<Expenses>

    // Get data by category and date span
    @Query ("SELECT * FROM expenses WHERE isExpense = 1 AND category LIKE :searchQuery AND dateInt BETWEEN :searchQueryFirst AND :searchQuerySecond ORDER BY dateInt DESC")
    fun getAllExpensesSortedByCategoryDateSpan(searchQuery: String, searchQueryFirst: String, searchQuerySecond: String): List<Expenses>

}