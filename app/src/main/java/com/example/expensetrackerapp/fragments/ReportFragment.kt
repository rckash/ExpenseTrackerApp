package com.example.expensetrackerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.FragmentReportBinding
import com.example.expensetrackerapp.recyclerview.ExpenseAdapter
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.ExpensesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.EmptyMap.entries

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private lateinit var appDB: ExpensesDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter = ExpenseAdapter
    private lateinit var expenseList: MutableList<Expenses>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)

        // database instantiation
        appDB = ExpensesDatabase.invoke(this)

        // recyclerview setup
        recyclerView = binding.rvExpensesReport
        recyclerView.layoutManager = LinearLayoutManager(this)

        expenseList = viewEntries()

        // adapter setup
        adapter = ExpenseAdapter(expenseList)
        recyclerView.adapter = adapter

        return binding.root
    }

    private fun saveExpense(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().addExpense(expenses)
        }
        Toast.makeText(this, "Expense Saved", Toast.LENGTH_SHORT).show()
    }

    private fun viewEntries(): MutableList<Expenses> {
        lateinit var expenses: MutableList<Expenses>
        val newExpenses = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (entry in appDB.getExpenses().getAllExpenses()) {
                newExpenses.add(expenses)
            }
            withContext(Dispatchers.Main) {
                adapter.expenses = newExpenses
                adapter.notifyDataSetChanged()
            }
        }
        return newExpenses
    }

    private fun updateEntry(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().updateExpense(entries)
        }
        Toast.makeText(this, "Entry Updated", Toast.LENGTH_SHORT).show()
    }

    private fun deleteEntry(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().deleteExpense(entries)
        }
        Toast.makeText(this, "Entry Deleted", Toast.LENGTH_SHORT).show()
    }

}