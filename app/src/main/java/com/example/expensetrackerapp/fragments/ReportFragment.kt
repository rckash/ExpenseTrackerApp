package com.example.expensetrackerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.DialogAddEntryLayoutBinding
import com.example.expensetrackerapp.databinding.FragmentReportBinding
import com.example.expensetrackerapp.recyclerview.ExpenseAdapter
import com.example.expensetrackerapp.recyclerview.ExpensesIncomeAdapter
import com.example.expensetrackerapp.recyclerview.IncomeAdapter
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.example.expensetrackerapp.roomdatabase.ExpensesIncome
import com.example.expensetrackerapp.roomdatabase.Income
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding

    private lateinit var appDB: AppDatabase

    private lateinit var expenseRecyclerView: RecyclerView
    lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseList: MutableList<Expenses>

    private lateinit var incomeRecyclerView: RecyclerView
    lateinit var incomeAdapter: IncomeAdapter
    private lateinit var incomeList: MutableList<Income>

    private lateinit var expensesIncomeRecyclerView: RecyclerView
    lateinit var expensesIncomeAdapter: ExpensesIncomeAdapter
    private lateinit var expensesIncomeList: MutableList<ExpensesIncome>

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

        // default recyclerview setup
        expenseRecyclerView = binding.rvExpensesReport
        expenseList = viewExpenses()
        expenseAdapter = ExpenseAdapter(expenseList)
        expenseRecyclerView.adapter = expenseAdapter
        expenseRecyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        // adapter setup
        incomeList = viewIncome()
        incomeAdapter = IncomeAdapter(incomeList)

        expensesIncomeList = viewExpensesIncome()
        expensesIncomeAdapter = ExpensesIncomeAdapter(expensesIncomeList)


        binding.floatingActionButton.setOnClickListener {
            showAddDialog()
        }

        binding.btnThisYear.setOnClickListener {

        }

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                // expense item
                0 -> {
                    expenseRecyclerView = binding.rvExpensesReport
                    expenseRecyclerView.adapter = expenseAdapter
                }
                // income item
                1 -> {
                    incomeRecyclerView = binding.rvExpensesReport
                    incomeRecyclerView.adapter = incomeAdapter
                }
                // expense & income item
                2 -> {
                    expensesIncomeRecyclerView = binding.rvExpensesReport
                    expensesIncomeRecyclerView.adapter = expensesIncomeAdapter
                }
            }
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

        // get current date
        val calendar = Calendar.getInstance().time
        val calendarDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar)
        // setting default date
        dialogBinding.tvDate.setText(calendarDateFormat)
        // selected date
        var selectedDateInt = 20230803   //convertHeaderTextToString(calendarDateFormat)
        var selectedDateString = calendarDateFormat

        // for changing date
        dialogBinding.btnSetDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()

            datePicker.show(requireActivity().supportFragmentManager, "DATE_PICKER")
            datePicker.addOnPositiveButtonClickListener {
                selectedDateString = datePicker.headerText
                dialogBinding.tvDate.setText(selectedDateString)
                var formattedDate = convertHeaderTextToString(selectedDateString)
                selectedDateInt = formattedDate
            }
        }

        alertDialogBuilder.setPositiveButton("Done") { dialog, _ ->
            // get data from edittexts
            val name = dialogBinding.tfNameDialog.editText?.text.toString()
            val price = dialogBinding.tfPriceDialog.editText?.text.toString().toInt()
            // get date and format
            val dateInt = selectedDateInt
            val dateString = selectedDateString

            // add new item to database table
            if (dialogBinding.rbExpense.isChecked) {
                val newItem = Expenses(0, name, price, "", dateInt, dateString)
                saveExpense(newItem)
                viewExpenses()

                val newExInItem = ExpensesIncome(0, name, price, dateInt, dateString, isExpense = true)
                saveExpensesIncome(newExInItem)
                viewExpensesIncome()
            } else {
                val newItem = Income(0, name, price, "", dateInt, dateString)
                saveIncome(newItem)
                viewIncome()

                val newExInItem = ExpensesIncome(0, name, price, dateInt, dateString, isExpense = false)
                saveExpensesIncome(newExInItem)
                viewExpensesIncome()
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

    private fun viewYearExpenses(): MutableList<Expenses> {
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

    private fun viewExpensesIncome(): MutableList<ExpensesIncome> {
        val newExpensesIncome = mutableListOf<ExpensesIncome>()

        GlobalScope.launch(Dispatchers.IO) {
            for (expensesIncome in appDB.getExpensesIncome().getAllExpensesIncome()) {
                newExpensesIncome.add(expensesIncome)
            }
            withContext(Dispatchers.Main) {
                expensesIncomeAdapter.expensesIncome = newExpensesIncome
                expensesIncomeAdapter.notifyDataSetChanged()
            }
        }
        return newExpensesIncome
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

    private fun saveExpensesIncome(expensesIncome: ExpensesIncome) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpensesIncome().addExpensesIncome(expensesIncome)
        }
        Toast.makeText(requireActivity().applicationContext, "ExpensesIncome Saved", Toast.LENGTH_SHORT).show()
    }

    private fun convertHeaderTextToString(headerText: String): Int {
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        val date = LocalDate.parse(headerText, formatter)
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
    }

}