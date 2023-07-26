package com.example.expensetrackerapp.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpenseItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses

class ExpenseItemViewHolder (val binding: ExpenseItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind (expense: Expenses) {
        binding.tvNameExpense.text = expense.name
        binding.tvPriceExpense.text = expense.price.toString()
    }
}