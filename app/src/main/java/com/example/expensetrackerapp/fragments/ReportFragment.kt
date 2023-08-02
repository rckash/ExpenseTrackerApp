package com.example.expensetrackerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.DialogAddEntryLayoutBinding
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
    lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseList: MutableList<Expenses>

    private lateinit var incomeRecyclerView: RecyclerView
    lateinit var incomeAdapter: IncomeAdapter
    private lateinit var incomeList: MutableList<Income>


    override fun onResume() {
        super.onResume()
        // dropdown menu setup
        val viewOptions = resources.getStringArray(R.array.view_options)
        val dropdownArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, viewOptions)
        binding.autoCompleteTextView.setAdapter(dropdownArrayAdapter)
    }

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

        // adapter setup
        expenseList = viewExpenses()
        incomeList = viewIncome()

        expenseAdapter = ExpenseAdapter(expenseList)
        expenseRecyclerView.adapter = expenseAdapter
        incomeAdapter = IncomeAdapter(incomeList)
        incomeRecyclerView.adapter = incomeAdapter


        binding.floatingActionButton.setOnClickListener {
            showAddDialog()
        }

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(requireContext(), "${parent.getItemAtPosition(position)} clicked", Toast.LENGTH_SHORT).show()
        }


        return binding.root
    }

    private fun showAddDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Add")

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_add_entry_layout, null)
        val dialogBinding = DialogAddEntryLayoutBinding.bind(dialogLayout)
        alertDialogBuilder.setView(dialogLayout)

        alertDialogBuilder.setPositiveButton("Done") { dialog, _ ->
            // get data from edittexts
            val name = dialogBinding.tfNameDialog.editText?.text.toString()
            val price = dialogBinding.tfPriceDialog.editText?.text.toString().toFloat()

            // add new item to database table
            if (dialogBinding.rbExpense.isChecked) {
                val newItem = Expenses(0, name, price, "")
                saveExpense(newItem)
                viewExpenses()
                expenseList.add(newItem)
                expenseRecyclerView.adapter?.notifyDataSetChanged()
            } else {
                val newItem = Income(0, name, price, "")
                saveIncome(newItem)
                viewIncome()
                incomeList.add(newItem)
                incomeRecyclerView.adapter?.notifyDataSetChanged()
            }
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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

    private fun saveExpense(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().addExpense(expenses)
        }
        Toast.makeText(requireActivity().applicationContext, "Expense Saved", Toast.LENGTH_SHORT).show()
    }

    private fun updateEntry(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().updateExpense(expenses)
            expenseAdapter.notifyDataSetChanged()
        }
        Toast.makeText(requireActivity().applicationContext, "Entry Updated", Toast.LENGTH_SHORT).show()
    }

    private fun deleteEntry(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().deleteExpense(expenses)
            expenseAdapter.notifyDataSetChanged()
        }
        Toast.makeText(requireActivity().applicationContext, "Entry Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun saveIncome(income: Income) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getIncome().addIncome(income)
        }
        Toast.makeText(requireActivity().applicationContext, "Income Saved", Toast.LENGTH_SHORT).show()
    }


}