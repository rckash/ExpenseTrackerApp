package com.example.expensetrackerapp.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpenseItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses

class ExpenseAdapter (var expenses: List<Expenses>): RecyclerView.Adapter<ExpenseItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExpenseItemLayoutBinding.inflate(inflater, parent, false)
        return ExpenseItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    override fun onBindViewHolder(holder: ExpenseItemViewHolder, position: Int) {
        holder.bind(expenses[position])
    }


}