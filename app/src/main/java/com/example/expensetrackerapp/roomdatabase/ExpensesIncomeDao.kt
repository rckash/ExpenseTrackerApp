package com.example.expensetrackerapp.roomdatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpensesIncomeDao {
    // create
    @Insert
    fun addExpensesIncome(expensesIncome: ExpensesIncome)

    // read
    @Query ("SELECT * FROM expensesIncome")
    fun getAllExpensesIncome(): List<ExpensesIncome>

    // update
    @Update
    fun updateExpensesIncome(expensesIncome: ExpensesIncome)

    // delete
    @Delete
    fun deleteExpensesIncome(expensesIncome: ExpensesIncome)
}