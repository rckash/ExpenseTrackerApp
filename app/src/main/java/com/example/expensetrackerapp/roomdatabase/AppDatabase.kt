package com.example.expensetrackerapp.roomdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (
    entities = [Expenses::class, Income::class, Goals::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getExpenses(): ExpensesDao
    abstract fun getIncome(): IncomeDao
    abstract fun getGoals(): GoalsDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        private val LOCK = Any()

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).build()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
    }
}