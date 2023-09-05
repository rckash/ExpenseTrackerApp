package com.example.expensetrackerapp.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.google.common.net.HttpHeaders.FROM

@Dao
interface ExpensesIncomeDao {
    // create
    @Insert
    fun addExpensesIncome(expensesIncome: ExpensesIncome)

    // read
    @Query ("SELECT * FROM expensesIncome ORDER BY dateInt DESC")
    fun getAllExpensesIncome(): List<ExpensesIncome>

    // update
    @Update
    fun updateExpensesIncome(expensesIncome: ExpensesIncome)

    // delete
    @Delete
    fun deleteExpensesIncome(expensesIncome: ExpensesIncome)

    @Query ("SELECT * FROM expensesIncome WHERE dateInt LIKE :searchQuery ORDER BY dateInt DESC")
    fun getAllExpensesIncomeSortedByMonth(searchQuery: String): List<ExpensesIncome>

//    @Query ("SELECT * FROM expenses UNION SELECT * FROM income")
//    fun uniteExpensesAndIncome(): List<ExpensesIncome>
}