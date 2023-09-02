package com.example.expensetrackerapp.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface IncomeDao {
    // Create
    @Insert
    fun addIncome(income: Income)

    // Read
    @Query ("SELECT * FROM income ORDER BY dateInt DESC")
    fun getAllIncome(): List<Income>

    // Update
    @Update
    fun updateIncome(income: Income)

    //Delete
    @Delete
    fun deleteIncome(income: Income)

    // Get data for this week
    @Query ("SELECT * FROM income WHERE dateInt BETWEEN :searchQueryfirst AND :searchQuerySecond")
    fun getAllIncomeSortedByWeek(searchQueryfirst: Int, searchQuerySecond: Int): List<Income>

    // Sort by Date Code
    @Query ("SELECT * FROM income WHERE dateInt LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllIncomeSortedByMonth(searchQuery: String): List<Income>

    // Get data by category
    @Query ("SELECT * FROM income WHERE category LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllIncomeSortedByCategory(searchQuery: String): List<Income>
}