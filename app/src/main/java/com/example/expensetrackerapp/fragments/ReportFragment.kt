package com.example.expensetrackerapp.fragments

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.DialogAddEntryLayoutBinding
import com.example.expensetrackerapp.databinding.FragmentReportBinding
import com.example.expensetrackerapp.recyclerview.ExpenseAdapter
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding

    private lateinit var appDB: AppDatabase

    private lateinit var expenseRecyclerView: RecyclerView
    lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseList: MutableList<Expenses>

    private lateinit var incomeRecyclerView: RecyclerView
    lateinit var incomeAdapter: ExpenseAdapter
    private lateinit var incomeList: MutableList<Expenses>

    private lateinit var expensesIncomeRecyclerView: RecyclerView
    lateinit var expensesIncomeAdapter: ExpenseAdapter
    private lateinit var expensesIncomeList: MutableList<Expenses>

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)

        // database instantiation
        appDB = AppDatabase.invoke(requireActivity().applicationContext)

        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        // default recyclerview setup
        expenseRecyclerView = binding.rvExpensesReport
        expenseList = viewExpenses()
        expenseAdapter = ExpenseAdapter(expenseList)
        expenseRecyclerView.adapter = expenseAdapter
        expenseRecyclerView.layoutManager = LinearLayoutManager(requireActivity().applicationContext)

        // adapter setup
        incomeList = viewIncome()
        incomeAdapter = ExpenseAdapter(incomeList)

        expensesIncomeList = viewExpensesAndIncome()
        expensesIncomeAdapter = ExpenseAdapter(expensesIncomeList)

        var monthQuery: String = "__"
        var yearQuery: String = "____"

        getMonthlyFreeBudget()

        firestore = FirebaseFirestore.getInstance()

        binding.floatingActionButton.setOnClickListener {
            showAddDialog()
        }

        binding.autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                // expense item
                0 -> {
                    viewExpenses()
                    expenseRecyclerView = binding.rvExpensesReport
                    expenseRecyclerView.adapter = expenseAdapter
                }
                // income item
                1 -> {
                    viewIncome()
                    incomeRecyclerView = binding.rvExpensesReport
                    incomeRecyclerView.adapter = incomeAdapter
                }
                // expense & income item
                2 -> {
                    viewExpensesAndIncome()
                    expensesIncomeRecyclerView = binding.rvExpensesReport
                    expensesIncomeRecyclerView.adapter = expensesIncomeAdapter
                }
            }
        }

        binding.monthAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val month = parent.getItemAtPosition(position).toString()
            var monthQuery: String = "__"
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
            viewExpensesAndIncome()
            getMonthlyFreeBudget()
        }

        binding.yearAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            yearQuery = parent.getItemAtPosition(position).toString()
            if (yearQuery == "--") {
                yearQuery = "____"
            }

            searchQuery = "$yearQuery$monthQuery%"
            Log.d("YearQuery", "$yearQuery")
            Log.d("ReportFragment", "searchQuery = $searchQuery")

            viewExpensesSortedByMonth(searchQuery)
            viewIncomeSortedByMonth(searchQuery)
            viewExpensesAndIncome()
            getMonthlyFreeBudget()
        }
        
        expenseAdapter.onExpenseClick = { expenses ->
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Expense Item Edit")

            val dialogLayout = layoutInflater.inflate(R.layout.dialog_add_entry_layout, null)
            val dialogBinding = DialogAddEntryLayoutBinding.bind(dialogLayout)
            alertDialogBuilder.setView(dialogLayout)

            dialogBinding.tfNameDialog.editText?.setText(expenses.name)
            dialogBinding.tfPriceDialog.editText?.setText(expenses.price.toString())
            dialogBinding.tvDate.setText(expenses.dateInt.toString())

            // setting radio button default
            dialogBinding.rbExpense.isVisible = false
            dialogBinding.rbIncome.isVisible = false

            // setting dropdown menu for expense and income
            dialogBinding.expenseAutoCompleteTextView.setText(expenses.category)

            val expenseCategoryOptions = resources.getStringArray(R.array.expenses_options)
            val expenseDropdownArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, expenseCategoryOptions)
            dialogBinding.expenseAutoCompleteTextView.setAdapter(expenseDropdownArrayAdapter)

            var expenseOrIncomeCategory = ""
            dialogBinding.expenseAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                expenseOrIncomeCategory = parent.getItemAtPosition(position).toString()
            }

            // get current date
            val calendar = Calendar.getInstance().time
            val calendarDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar)
            // setting default date
            val defaultDate = expenses.dateInt.toString()
            val defaultDay = defaultDate.substring(6, 8)
            val defaultMonthCode = defaultDate.substring(4, 6)
            var defaultMonth = "Jan"
            when (defaultMonthCode) {
                "01" -> defaultMonth = "Jan"
                "02" -> defaultMonth = "Feb"
                "03" -> defaultMonth = "Mar"
                "04" -> defaultMonth = "Apr"
                "05" -> defaultMonth = "May"
                "06" -> defaultMonth = "Jun"
                "07" -> defaultMonth = "Jul"
                "08" -> defaultMonth = "Aug"
                "09" -> defaultMonth = "Sep"
                "10" -> defaultMonth = "Oct"
                "11" -> defaultMonth = "Nov"
                "12" -> defaultMonth = "Dec"
            }
            val defaultYear = defaultDate.substring(0, 4)
            dialogBinding.tvDate.setText("$defaultMonth $defaultDay, $defaultYear")
            // converting selected date format
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
                var name = dialogBinding.tfNameDialog.editText?.text.toString()
                var priceDialog = dialogBinding.tfPriceDialog.editText?.text.toString()
                var price = 0

                // null check
                if (name.isNullOrEmpty()) {
                    name = "NA"
                }

                if (priceDialog.isNullOrEmpty()) {
                    price = 0
                } else {
                    price = priceDialog.toInt()
                }

                // get date and format
                val dateInt = selectedDateInt
                val dateString = selectedDateString

                // default category
                if (expenseOrIncomeCategory == "") {
                    expenseOrIncomeCategory = expenses.category
                }

                // add new item to database table
                val expenseItem = Expenses(expenses.id, name, price, expenseOrIncomeCategory, dateInt, dateString, isExpense = true, userUid)
                updateExpense(expenseItem)
                viewExpenses()
                viewExpensesAndIncome()
                getMonthlyFreeBudget()
            }
                alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        expenseAdapter.onDeleteClick = { expenses ->
            val expenseItem = Expenses(expenses.id, "", 0, "", 0, "", true, userUid)
            deleteExpense(expenseItem)
            viewExpenses()
            viewExpensesAndIncome()
            getMonthlyFreeBudget()
        }

        incomeAdapter.onExpenseClick = { income ->
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Income Item Edit")

            val dialogLayout = layoutInflater.inflate(R.layout.dialog_add_entry_layout, null)
            val dialogBinding = DialogAddEntryLayoutBinding.bind(dialogLayout)
            alertDialogBuilder.setView(dialogLayout)

            dialogBinding.tfNameDialog.editText?.setText(income.name)
            dialogBinding.tfPriceDialog.editText?.setText(income.price.toString())
            dialogBinding.tvDate.setText(income.dateInt.toString())

            // setting radio button default
            dialogBinding.rbExpense.isVisible = false
            dialogBinding.rbIncome.isVisible = false

            // setting dropdown menu for expense and income
            dialogBinding.expenseAutoCompleteTextView.setText(income.category)

            val incomeCategoryOptions = resources.getStringArray(R.array.income_options)
            val incomeDropdownArrayAdapter =
                ArrayAdapter(requireContext(), R.layout.dropdown_item, incomeCategoryOptions)
            dialogBinding.expenseAutoCompleteTextView.setAdapter(incomeDropdownArrayAdapter)

            var expenseOrIncomeCategory = ""
            dialogBinding.expenseAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
                expenseOrIncomeCategory = parent.getItemAtPosition(position).toString()
            }

            // get current date
            val calendar = Calendar.getInstance().time
            val calendarDateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM).format(calendar)
            // setting default date
            val defaultDate = income.dateInt.toString()
            val defaultDay = defaultDate.substring(6, 8)
            val defaultMonthCode = defaultDate.substring(4, 6)
            var defaultMonth = "Jan"
            when (defaultMonthCode) {
                "01" -> defaultMonth = "Jan"
                "02" -> defaultMonth = "Feb"
                "03" -> defaultMonth = "Mar"
                "04" -> defaultMonth = "Apr"
                "05" -> defaultMonth = "May"
                "06" -> defaultMonth = "Jun"
                "07" -> defaultMonth = "Jul"
                "08" -> defaultMonth = "Aug"
                "09" -> defaultMonth = "Sep"
                "10" -> defaultMonth = "Oct"
                "11" -> defaultMonth = "Nov"
                "12" -> defaultMonth = "Dec"
            }
            val defaultYear = defaultDate.substring(0, 4)
            dialogBinding.tvDate.setText("$defaultMonth $defaultDay, $defaultYear")
            // converting selected date format
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
                var name = dialogBinding.tfNameDialog.editText?.text.toString()
                var priceDialog = dialogBinding.tfPriceDialog.editText?.text.toString()
                var price = 0

                // null check
                if (name.isNullOrEmpty()) {
                    name = "NA"
                }

                if (priceDialog.isNullOrEmpty()) {
                    price = 0
                } else {
                    price = priceDialog.toInt()
                }

                // get date and format
                val dateInt = selectedDateInt
                val dateString = selectedDateString
                // default category
                if (expenseOrIncomeCategory == "") {
                    expenseOrIncomeCategory = income.category
                }

                // add new item to database table
                val incomeItem = Expenses(income.id, name, price, expenseOrIncomeCategory, dateInt, dateString, false, userUid)
                updateIncome(incomeItem)
                viewIncome()
                viewExpensesAndIncome()
                getMonthlyFreeBudget()
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        incomeAdapter.onDeleteClick = { expenses ->
            val incomeItem = Expenses(expenses.id, "", 0, "", 0, "", false, userUid)
            deleteIncome(incomeItem)
            viewIncome()
            viewExpensesAndIncome()
            getMonthlyFreeBudget()
        }

        expensesIncomeAdapter.onExpenseClick = { expensesAndIncome ->
            val alertDialogBuilder = AlertDialog.Builder(requireContext())
            alertDialogBuilder.setTitle("Income Item Edit")

            val dialogLayout = layoutInflater.inflate(R.layout.dialog_add_entry_layout, null)
            val dialogBinding = DialogAddEntryLayoutBinding.bind(dialogLayout)
            alertDialogBuilder.setView(dialogLayout)

            dialogBinding.tfNameDialog.editText?.setText(expensesAndIncome.name)
            dialogBinding.tfPriceDialog.editText?.setText(expensesAndIncome.price.toString())
            dialogBinding.tvDate.setText(expensesAndIncome.dateInt.toString())

            // setting radio button default
            dialogBinding.rbExpense.isVisible = false
            dialogBinding.rbIncome.isVisible = false

            // setting dropdown menu for expense and income
            dialogBinding.expenseAutoCompleteTextView.setText(expensesAndIncome.category)

            if (expensesAndIncome.isExpense) {
                val incomeCategoryOptions = resources.getStringArray(R.array.expenses_options)
                val incomeDropdownArrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.dropdown_item, incomeCategoryOptions)
                dialogBinding.expenseAutoCompleteTextView.setAdapter(incomeDropdownArrayAdapter)
            } else {
                val incomeCategoryOptions = resources.getStringArray(R.array.income_options)
                val incomeDropdownArrayAdapter =
                    ArrayAdapter(requireContext(), R.layout.dropdown_item, incomeCategoryOptions)
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
            val defaultDate = expensesAndIncome.dateInt.toString()
            val defaultDay = defaultDate.substring(6, 8)
            val defaultMonthCode = defaultDate.substring(4, 6)
            var defaultMonth = "Jan"
            when (defaultMonthCode) {
                "01" -> defaultMonth = "Jan"
                "02" -> defaultMonth = "Feb"
                "03" -> defaultMonth = "Mar"
                "04" -> defaultMonth = "Apr"
                "05" -> defaultMonth = "May"
                "06" -> defaultMonth = "Jun"
                "07" -> defaultMonth = "Jul"
                "08" -> defaultMonth = "Aug"
                "09" -> defaultMonth = "Sep"
                "10" -> defaultMonth = "Oct"
                "11" -> defaultMonth = "Nov"
                "12" -> defaultMonth = "Dec"
            }
            val defaultYear = defaultDate.substring(0, 4)
            dialogBinding.tvDate.setText("$defaultMonth $defaultDay, $defaultYear")
            // converting selected date format
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
                var name = dialogBinding.tfNameDialog.editText?.text.toString()
                var priceDialog = dialogBinding.tfPriceDialog.editText?.text.toString()
                var price = 0

                // null check
                if (name.isNullOrEmpty()) {
                    name = "NA"
                }

                if (priceDialog.isNullOrEmpty()) {
                    price = 0
                } else {
                    price = priceDialog.toInt()
                }

                // get date and format
                val dateInt = selectedDateInt
                val dateString = selectedDateString
                // default category
                if (expenseOrIncomeCategory == "") {
                    expenseOrIncomeCategory = expensesAndIncome.category
                }

                // add new item to database table
                val incomeItem = Expenses(expensesAndIncome.id, name, price, expenseOrIncomeCategory, dateInt, dateString, expensesAndIncome.isExpense, userUid)
                updateIncome(incomeItem)
                viewIncome()
                viewExpensesAndIncome()
                getMonthlyFreeBudget()
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        expensesIncomeAdapter.onDeleteClick = { expensesAndIncome ->
            val incomeItem = Expenses(expensesAndIncome.id, "", 0, "", 0, "", false, userUid)
            deleteIncome(incomeItem)
            viewIncome()
            viewExpensesAndIncome()
            getMonthlyFreeBudget()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(): Triple<String, String, String> {
        // get current date
        val now = LocalDateTime.now()
        val day = now.dayOfMonth.toString()
        val month = now.month.toString()
        val year = now.year.toString()

        val firstCharacter = month[0]
        val restOfString = month.substring(1)

        val formattedMonth = firstCharacter.uppercase() + restOfString.lowercase()

        return Triple(day, formattedMonth, year)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonthlyFreeBudget() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val currentDate = getDate()
        var monthCode = "__"
        when (currentDate.second) {
            "January" -> { monthCode = "01" }
            "February" -> { monthCode = "02" }
            "March" -> { monthCode = "03" }
            "April" -> { monthCode = "04" }
            "May" -> { monthCode = "05" }
            "June" -> { monthCode = "06" }
            "July" -> { monthCode = "07" }
            "August" -> { monthCode = "08" }
            "September" -> { monthCode = "09" }
            "October" -> { monthCode = "10" }
            "November" -> { monthCode = "11" }
            "December" -> { monthCode = "12" }
        }

        var monthExpense: Int = 0
        var monthIncome: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByMonth("${currentDate.third}${monthCode}__")) {
                if (userUid == expense.user) {
                    monthExpense += expense.price
                }
            }
            for (income in appDB.getExpenses().getAllIncomeSortedByMonth("${currentDate.third}${monthCode}__")) {
                if (userUid == income.user) {
                    monthIncome += income.price
                }
            }
            withContext(Dispatchers.Main) {
                // UI setup
                val freeBudget = monthIncome - monthExpense
                binding.tvBudget.text = freeBudget.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddDialog() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

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
        // converting selected date format
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
            var name = dialogBinding.tfNameDialog.editText?.text.toString()
            var priceDialog = dialogBinding.tfPriceDialog.editText?.text.toString()
            var price = 0

            // null check
            if (name.isNullOrEmpty()) {
                name = "NA"
            }

            if (priceDialog.isNullOrEmpty()) {
                price = 0
            } else {
                price = priceDialog.toInt()
            }

            // get date and format
            val dateInt = selectedDateInt
            val dateString = selectedDateString
            val isExpense = true

            // add new item to database table
            if (dialogBinding.rbExpense.isChecked) {
                val newItem = Expenses(0, name, price, expenseOrIncomeCategory, dateInt, dateString, isExpense, userUid)
                saveExpense(newItem)
                viewExpenses()
                viewExpensesAndIncome()
                getMonthlyFreeBudget()

            } else {
                val newItem = Expenses(0, name, price, expenseOrIncomeCategory, dateInt, dateString, false, userUid)
                saveExpense(newItem)
                viewIncome()
                viewExpensesAndIncome()
                getMonthlyFreeBudget()
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
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val newExpenses = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpenses()) {
                if (expense.user == userUid) {
                    newExpenses.add(expense)
                }
            }
            withContext(Dispatchers.Main) {
                expenseAdapter.expenses = newExpenses
                expenseAdapter.notifyDataSetChanged()
            }
        }
        return newExpenses
    }

    private fun viewIncome(): MutableList<Expenses> {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val newIncome = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (income in appDB.getExpenses().getAllIncome()) {
                if (income.user == userUid) {
                    newIncome.add(income)
                }

            }
            withContext(Dispatchers.Main) {
                incomeAdapter.expenses = newIncome
                incomeAdapter.notifyDataSetChanged()
            }
        }
        return newIncome
    }

    private fun viewExpensesAndIncome(): MutableList<Expenses> {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val newExpensesIncome = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (expensesAndIncome in appDB.getExpenses().getAllExpenseAndIncome()) {
                if (expensesAndIncome.user == userUid) {
                    newExpensesIncome.add(expensesAndIncome)
                }
            }
            withContext(Dispatchers.Main) {
                expensesIncomeAdapter.expenses = newExpensesIncome
                expensesIncomeAdapter.notifyDataSetChanged()
            }
        }
        return newExpensesIncome
    }

    private fun saveExpense(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().addExpense(expenses)
        }
        Toast.makeText(requireActivity().applicationContext, "Entry Saved", Toast.LENGTH_SHORT).show()
    }

    private fun updateExpense(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().updateExpense(expenses)
        }
        Toast.makeText(requireActivity().applicationContext, "Entry Updated", Toast.LENGTH_SHORT).show()
    }

    private fun updateIncome(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().updateExpense(expenses)
        }
        Toast.makeText(requireActivity().applicationContext, "Entry Updated", Toast.LENGTH_SHORT).show()
    }

    private fun deleteExpense(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().deleteExpense(expenses)
        }
        Toast.makeText(requireActivity().applicationContext, "Entry Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun deleteIncome(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().deleteExpense(expenses)
        }
        Toast.makeText(requireActivity().applicationContext, "Entry Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun convertHeaderTextToString(headerText: String): Int {
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
        val date = LocalDate.parse(headerText, formatter)
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInt()
    }

    private fun viewExpensesSortedByMonth(searchQuery: String): MutableList<Expenses> {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val newExpenses = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByMonth(searchQuery)) {
                if (expense.user == userUid) {
                    newExpenses.add(expense)
                }

            }
            withContext(Dispatchers.Main) {
                expenseAdapter.expenses = newExpenses
                expenseAdapter.notifyDataSetChanged()
            }
        }
        return newExpenses
    }

    private fun viewIncomeSortedByMonth(searchQuery: String): MutableList<Expenses> {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val newIncome = mutableListOf<Expenses>()

        GlobalScope.launch(Dispatchers.IO) {
            for (income in appDB.getExpenses().getAllIncomeSortedbyMonth(searchQuery)) {
                if (income.user == userUid) {
                    newIncome.add(income)
                }
            }
            withContext(Dispatchers.Main) {
                incomeAdapter.expenses = newIncome
                incomeAdapter.notifyDataSetChanged()
            }
        }
        return newIncome
    }

}