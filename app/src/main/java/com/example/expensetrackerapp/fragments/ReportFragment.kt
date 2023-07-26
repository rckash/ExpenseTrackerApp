package com.example.expensetrackerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.FragmentReportBinding
import com.example.expensetrackerapp.recyclerview.ExpenseAdapter
import com.example.expensetrackerapp.recyclerview.IncomeAdapter
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.example.expensetrackerapp.roomdatabase.Income
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding

    private lateinit var appDB: AppDatabase

    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseList: MutableList<Expenses>

    private lateinit var incomeRecyclerView: RecyclerView
    private lateinit var incomeAdapter: IncomeAdapter
    private lateinit var incomeList: MutableList<Income>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)

        // database instantiation
        appDB = AppDatabase.invoke(requireActivity().applicationContext)

        // recyclerview setup
        expenseRecyclerView = binding.rvExpensesReport
        expenseRecyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        incomeRecyclerView = binding.rvIncomeReport
        incomeRecyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        expenseList = viewExpenses()
        incomeList = viewIncome()

        // adapter setup
        expenseAdapter = ExpenseAdapter(expenseList)
        expenseRecyclerView.adapter = expenseAdapter
        incomeAdapter = IncomeAdapter(incomeList)
        incomeRecyclerView.adapter = incomeAdapter


        return binding.root
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