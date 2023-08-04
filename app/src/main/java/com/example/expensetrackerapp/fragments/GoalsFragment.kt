package com.example.expensetrackerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.DialogAddEntryLayoutBinding
import com.example.expensetrackerapp.databinding.DialogAddGoalLayoutBinding
import com.example.expensetrackerapp.databinding.FragmentGoalsBinding
import com.example.expensetrackerapp.recyclerview.ExpenseAdapter
import com.example.expensetrackerapp.recyclerview.GoalAdapter
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.Goals
import com.example.expensetrackerapp.roomdatabase.Income
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoalsFragment : Fragment() {
    private lateinit var binding: FragmentGoalsBinding

    private lateinit var appDB: AppDatabase

    private lateinit var goalsRecyclerView: RecyclerView
    lateinit var goalsAdapter: GoalAdapter
    private lateinit var goalsList: MutableList<Goals>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoalsBinding.inflate(layoutInflater, container, false)

        // database instantiation
        appDB = AppDatabase.invoke(requireActivity().applicationContext)

        // recyclerview setup
        goalsRecyclerView = binding.rvGoals
        goalsRecyclerView.layoutManager = LinearLayoutManager (requireActivity().applicationContext)

        // adapter setup
        goalsList = viewGoals()

        goalsAdapter = GoalAdapter(goalsList)
        goalsRecyclerView.adapter = goalsAdapter

        binding.floatingActionButton.setOnClickListener {
            showAddDialog()
        }

        return binding.root
    }

    private fun showAddDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Add")

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_add_goal_layout, null)
        val dialogBinding = DialogAddGoalLayoutBinding.bind(dialogLayout)
        alertDialogBuilder.setView(dialogLayout)

        alertDialogBuilder.setPositiveButton("Done") { dialog, _ ->
            // get data from edittexts
            val name = dialogBinding.tfNameGoalDialog.editText?.text.toString()
            val price = dialogBinding.tfPriceGoalDialog.editText?.text.toString().toInt()
            val amountInvested = dialogBinding.tfAmountInvestedGoalDialog.editText?.text.toString().toInt()

            val newItem = Goals(0, name, price, amountInvested)
            saveGoal(newItem)
            viewGoals()
            goalsList.add(newItem)
            goalsRecyclerView.adapter?.notifyDataSetChanged()

            dialog.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun saveGoal(goals: Goals) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getGoals().addGoal(goals)
        }
        Toast.makeText(requireContext(), "Goal Saved", Toast.LENGTH_SHORT).show()
    }

    private fun viewGoals(): MutableList<Goals> {
        val newGoals = mutableListOf<Goals>()

        GlobalScope.launch(Dispatchers.IO) {
            for (goals in appDB.getGoals().getAllGoals()) {
                newGoals.add(goals)
            }
            withContext(Dispatchers.Main) {
                goalsAdapter.goals = newGoals
                goalsAdapter.notifyDataSetChanged()
            }
        }
        return newGoals
    }



}