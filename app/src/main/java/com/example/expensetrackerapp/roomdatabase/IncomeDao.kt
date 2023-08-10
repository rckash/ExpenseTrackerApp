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

    @Query ("SELECT * FROM income WHERE dateInt LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllIncomeSortedByMonth(searchQuery: String): List<Income>
}