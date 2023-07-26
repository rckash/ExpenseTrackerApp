package com.example.expensetrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expensetrackerapp.databinding.ActivityMainBinding
import com.example.expensetrackerapp.fragments.GoalsFragment
import com.example.expensetrackerapp.fragments.HomeFragment
import com.example.expensetrackerapp.fragments.ReportFragment
import com.example.expensetrackerapp.recyclerview.ExpenseAdapter
import com.example.expensetrackerapp.recyclerview.IncomeAdapter
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.example.expensetrackerapp.roomdatabase.Income
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appDB: AppDatabase
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseList: MutableList<Expenses>
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var incomeList: MutableList<Income>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // database instantiation
        appDB = AppDatabase.invoke(applicationContext)

        expenseList = viewExpenses()
        incomeList = viewIncome()

        // adapter setup
        expenseAdapter = ExpenseAdapter(expenseList)
        incomeAdapter = IncomeAdapter(incomeList)

        // Fragments Instantiation
        val HomeFragment = HomeFragment()
        val ReportFragment = ReportFragment()
        val GoalsFragment = GoalsFragment()

        // Default Fragment Setting
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, HomeFragment)
            commit()
        }

        // Fragments Navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_home -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, HomeFragment)
                        commit()
                    }
                }
                R.id.item_report -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, ReportFragment)
                        commit()
                    }
                }
                R.id.item_goals -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainerView, GoalsFragment)
                        commit()
                    }
                }
            }
            true
        }

        // save
        val toSave = Expenses(0, "Medicine", 5000.35f)
        val toSave2 = Expenses(0, "Food", 10250.99f)
        saveExpense(toSave)
        saveExpense(toSave2)

    }

    private fun saveExpense(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().addExpense(expenses)
            expenseAdapter.notifyDataSetChanged()
        }
        Toast.makeText(applicationContext, "Expense Saved", Toast.LENGTH_SHORT).show()
    }

    private fun viewExpenses(): MutableList<Expenses> {
        lateinit var expenses: MutableList<Expenses>
        val newExpenses = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpenses()) {
                newExpenses.add(expense)
            }
            withContext(Dispatchers.Main) {
                expenseAdapter.expenses = newExpenses
                expenseAdapter.notifyDataSetChanged()
            }
        }
        return newExpenses
    }

    private fun updateEntry(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().updateExpense(expenses)
            expenseAdapter.notifyDataSetChanged()
        }
        Toast.makeText(applicationContext, "Entry Updated", Toast.LENGTH_SHORT).show()
    }

    private fun deleteEntry(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().deleteExpense(expenses)
            expenseAdapter.notifyDataSetChanged()
        }
        Toast.makeText(applicationContext, "Entry Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun saveIncome(income: Income) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getIncome().addIncome(income)
            incomeAdapter.notifyDataSetChanged()
        }
        Toast.makeText(applicationContext, "Expense Saved", Toast.LENGTH_SHORT).show()
    }

    private fun viewIncome(): MutableList<Income> {
        lateinit var income: MutableList<Income>
        val newIncome = mutableListOf<Income>()

        GlobalScope.launch(Dispatchers.IO) {
            for (income in appDB.getIncome().getAllIncome()) {
                newIncome.add(income)
            }
            withContext(Dispatchers.Main) {
                incomeAdapter.income = newIncome
                incomeAdapter.notifyDataSetChanged()
            }
        }
        return newIncome
    }

}