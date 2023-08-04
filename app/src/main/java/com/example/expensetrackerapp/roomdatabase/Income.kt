package com.example.expensetrackerapp.roomdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Income (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var name: String,
    var price: Int,
    var category: String,
    var dateInt: Int,
    var dateString: String
    )