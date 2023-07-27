package com.example.expensetrackerapp.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Expenses (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var price: Float,
    var category: String
        )