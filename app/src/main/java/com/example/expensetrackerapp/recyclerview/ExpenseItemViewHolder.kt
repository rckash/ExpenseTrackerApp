package com.example.expensetrackerapp.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpenseItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses

class ExpenseItemViewHolder (val expenseBinding: ExpenseItemLayoutBinding): RecyclerView.ViewHolder(expenseBinding.root) {

    fun bind (expense: Expenses) {
        expenseBinding.tvNameExpense.text = expense.name
        expenseBinding.tvPriceExpense.text = expense.price.toString()
        expenseBinding.tvDateExpense.text = expense.dateString
    }
}