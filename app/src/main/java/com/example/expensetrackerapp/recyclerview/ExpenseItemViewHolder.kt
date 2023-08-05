package com.example.expensetrackerapp.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpenseItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses

class ExpenseItemViewHolder (val expenseBinding: ExpenseItemLayoutBinding): RecyclerView.ViewHolder(expenseBinding.root) {

    fun bind (expense: Expenses) {
        expenseBinding.tvNameExpenses.text = expense.name
        expenseBinding.tvPriceExpenses.text = "-₱" + expense.price.toString()
        expenseBinding.tvDateExpenses.text = expense.dateString
    }
}