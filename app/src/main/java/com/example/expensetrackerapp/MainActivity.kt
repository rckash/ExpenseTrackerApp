package com.example.expensetrackerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import com.example.expensetrackerapp.databinding.ActivityMainBinding
import com.example.expensetrackerapp.databinding.DialogAddEntryLayoutBinding
import com.example.expensetrackerapp.fragments.GoalsFragment
import com.example.expensetrackerapp.fragments.HomeFragment
import com.example.expensetrackerapp.fragments.ReportFragment
import com.example.expensetrackerapp.recyclerview.ExpenseAdapter
import com.example.expensetrackerapp.recyclerview.GoalAdapter
import com.example.expensetrackerapp.recyclerview.IncomeAdapter
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.example.expensetrackerapp.roomdatabase.Goals
import com.example.expensetrackerapp.roomdatabase.Income
import com.google.android.material.navigation.NavigationView
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

    private lateinit var goalAdapter: GoalAdapter
    private lateinit var goalList: MutableList<Goals>

    lateinit var drawerToggle: ActionBarDrawerToggle

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
            binding.floatingActionButton.hide()
            replace(R.id.fragmentContainerView, HomeFragment)
            commit()
        }

        // FAB function
        binding.floatingActionButton.setOnClickListener {
            showAddDialog()
        }

        // nav drawer setup
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        drawerToggle = ActionBarDrawerToggle( this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // fragments navigation
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {

                    supportFragmentManager.beginTransaction().apply {
                        binding.floatingActionButton.hide()
                        replace(R.id.fragmentContainerView, HomeFragment)
                        commit()
                    }
                    drawerLayout.close()

                }
                R.id.nav_report -> {

                    supportFragmentManager.beginTransaction().apply {
                        binding.floatingActionButton.show()
                        replace(R.id.fragmentContainerView, ReportFragment)
                        commit()
                    }
                    drawerLayout.close()

                }
                R.id.nav_goals -> {

                    supportFragmentManager.beginTransaction().apply {
                        binding.floatingActionButton.show()
                        replace(R.id.fragmentContainerView, GoalsFragment)
                        commit()
                    }
                    drawerLayout.close()

                }
            }
            true
        }

    }

    private fun showAddDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
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
            } else {
                val newItem = Income(0, name, price, "")
                saveIncome(newItem)
            }
            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
        Toast.makeText(applicationContext, "Income Saved", Toast.LENGTH_SHORT).show()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}