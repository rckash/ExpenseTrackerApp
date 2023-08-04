package com.example.expensetrackerapp.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExpensesIncome(
    @PrimaryKey (autoGenerate = true) val id: Int,
    val name: String,
    val price: Int,
    val dateInt: Int,
    val dateString: String,
    val isExpense: Boolean
)
