package com.example.expensetrackerapp.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpensesIncomeItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.ExpensesIncome

class ExpensesIncomeItemViewHolder (val expensesIncomeBinding: ExpensesIncomeItemLayoutBinding): RecyclerView.ViewHolder(expensesIncomeBinding.root) {

    fun bind(expensesIncome: ExpensesIncome) {
        expensesIncomeBinding.tvNameExpensesIncome.text = expensesIncome.name
        expensesIncomeBinding.tvPriceExpensesIncome.text = expensesIncome.price.toString()
        expensesIncomeBinding.tvDateExpensesIncome.text = expensesIncome.dateString
    }
}