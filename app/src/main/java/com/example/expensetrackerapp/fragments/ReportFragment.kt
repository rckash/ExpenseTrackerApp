package com.example.expensetrackerapp.fragments

import android.app.ProgressDialog.show
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    private lateinit var searchQuery: String

    private lateinit var firestore: FirebaseFirestore

    override fun onResume() {
        super.onResume()
        // dropdown menu setup
        val viewOptions = resources.getStringArray(R.array.view_options)
        val dropdownArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, viewOptions)
        binding.autoCompleteTextView.setAdapter(dropdownArrayAdapter)

        val monthOptions = resources.getStringArray(R.array.month_options)
        val monthDropdownArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, monthOptions)
        binding.monthAutoCompleteTextView.setAdapter(monthDropdownArrayAdapter)

        val yearOptions = resources.getStringArray(R.array.year_options)
        val yearDropdownArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, yearOptions)
        binding.yearAutoCompleteTextView.setAdapter(yearDropdownArrayAdapter)

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

        var monthQuery: String = "__"
        var yearQuery: String = "____"

        firestore = FirebaseFirestore.getInstance()

        binding.floatingActionButton.setOnClickListener {
            showAddDialog()
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

        binding.monthAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val month = parent.getItemAtPosition(position).toString()
            var monthQuery = "__"
            when (month) {
                "January" -> {
                    monthQuery = "01"
                }
                "February" -> {
                    monthQuery = "02"
                }
                "March" -> {
                    monthQuery = "03"
                }
                "April" -> {
                    monthQuery = "04"
                }
                "May" -> {
                    monthQuery = "05"
                }
                "June" -> {
                    monthQuery = "06"
                }
                "July" -> {
                    monthQuery = "07"
                }
                "August" -> {
                    monthQuery = "08"
                }
                "September" -> {
                    monthQuery = "09"
                }
                "October" -> {
                    monthQuery = "10"
                }
                "November" -> {
                    monthQuery = "11"
                }
                "December" -> {
                    monthQuery = "12"
                }
                else -> {
                    monthQuery = "__"
                }
            }

            searchQuery = "$yearQuery$monthQuery%"
            Log.d("ReportFragment", monthQuery)
            Log.d("ReportFragment", "searchQuery = $searchQuery")

            viewExpensesSortedByMonth(searchQuery)
            viewIncomeSortedByMonth(searchQuery)
            viewExpensesIncomeSortedByMonth(searchQuery)

            Toast.makeText(requireContext(), "${parent.getItemAtPosition(position)} clicked", Toast.LENGTH_SHORT).show()
        }

        binding.yearAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            yearQuery = parent.getItemAtPosition(position).toString()

            searchQuery = "$yearQuery$monthQuery%"
            Log.d("ReportFragment", monthQuery)
            Log.d("ReportFragment", "searchQuery = $searchQuery")

            viewExpensesSortedByMonth(searchQuery)
            viewIncomeSortedByMonth(searchQuery)
            viewExpensesIncomeSortedByMonth(searchQuery)

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

        // setting radio button default
        dialogBinding.rbExpense.isChecked = true

        // setting dropdown menu for expense and income
        val expenseCategoryOptions = resources.getStringArray(R.array.expenses_options)
        val expenseDropdownArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, expenseCategoryOptions)
        dialogBinding.expenseAutoCompleteTextView.setAdapter(expenseDropdownArrayAdapter)

        dialogBinding.rbExpense.setOnClickListener {
            dialogBinding.expenseAutoCompleteTextView.setAdapter(expenseDropdownArrayAdapter)
        }
        dialogBinding.rbIncome.setOnClickListener {
            val incomeCategoryOptions = resources.getStringArray(R.array.income_options)
            val incomeDropdownArrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, incomeCategoryOptions)
            dialogBinding.expenseAutoCompleteTextView.setAdapter(incomeDropdownArrayAdapter)
        }

        var expenseOrIncomeCategory = ""
        dialogBinding.expenseAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            expenseOrIncomeCategory = parent.getItemAtPosition(position).toString()
        }

        // get current date
        val calendar = Calendar.getInstance().time
        val calendarDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar)
        // setting default date
        dialogBinding.tvDate.setText(calendarDateFormat)
        // selected date
        var selectedDateInt = convertHeaderTextToString(calendarDateFormat)
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
                val newItem = Expenses(0, name, price, expenseOrIncomeCategory, dateInt, dateString)
                saveExpense(newItem)
                viewExpenses()

                val newExInItem = ExpensesIncome(0, name, price, dateInt, dateString, isExpense = true)
                saveExpensesIncome(newExInItem)
                viewExpensesIncome()

                // start

//                val user = FirebaseAuth.getInstance().currentUser
//                val userUid = user?.uid.toString()
//                val db = Firebase.firestore
//
//                val city = hashMapOf(
//                    "name" to name,
//                    "price" to price,
//                    "category" to "",
//                    "dateInt" to dateInt,
//                    "dateString" to dateString,
//                    "itemType" to "expense"
//                )
//
//                // Add a new document with a generated ID
//                db.collection("$userUid")
//                    .add(city)
//                    .addOnSuccessListener { documentReference ->
//                        Toast.makeText(requireActivity().applicationContext, "Uploaded to Firebase", Toast.LENGTH_SHORT).show()
//                        Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(requireActivity().applicationContext, "Failed to upload to Firebase", Toast.LENGTH_SHORT).show()
//                        Log.w("TAG", "Error adding document", e)
//                    }

            } else {
                val newItem = Income(0, name, price, expenseOrIncomeCategory, dateInt, dateString)
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
        Toast.makeText(requireActivity().applicationContext, "${expenses.id.toString()}", Toast.LENGTH_SHORT).show()
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

    private fun convertHeaderTextToString(headerText: String): Int {
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        val date = LocalDate.parse(headerText, formatter)
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
    }

    private fun viewExpensesSortedByMonth(searchQuery: String): MutableList<Expenses> {
        val newExpenses = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByMonth(searchQuery)) {
                newExpenses.add(expense)
            }
            withContext(Dispatchers.Main) {
                expenseAdapter.expenses = newExpenses
                expenseAdapter.notifyDataSetChanged()
            }
        }
        return newExpenses
    }

    private fun viewIncomeSortedByMonth(searchQuery: String): MutableList<Income> {
        val newIncome = mutableListOf<Income>()

        GlobalScope.launch(Dispatchers.IO) {
            for (income in appDB.getIncome().getAllIncomeSortedByMonth(searchQuery)) {
                newIncome.add(income)
            }
            withContext(Dispatchers.Main) {
                incomeAdapter.income = newIncome
                incomeAdapter.notifyDataSetChanged()
            }
        }
        return newIncome
    }

    private fun viewExpensesIncomeSortedByMonth(searchQuery: String): MutableList<ExpensesIncome> {
        val newExpensesIncome = mutableListOf<ExpensesIncome>()

        GlobalScope.launch(Dispatchers.IO) {
            for (expensesIncome in appDB.getExpensesIncome().getAllExpensesIncomeSortedByMonth(searchQuery)) {
                newExpensesIncome.add(expensesIncome)
            }
            withContext(Dispatchers.Main) {
                expensesIncomeAdapter.expensesIncome = newExpensesIncome
                expensesIncomeAdapter.notifyDataSetChanged()
            }
        }
        return newExpensesIncome
    }

}