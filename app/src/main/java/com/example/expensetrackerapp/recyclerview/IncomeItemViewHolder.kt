package com.example.expensetrackerapp.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpenseItemLayoutBinding
import com.example.expensetrackerapp.databinding.IncomeItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.Income

class IncomeItemViewHolder (val incomeBinding: IncomeItemLayoutBinding): RecyclerView.ViewHolder(incomeBinding.root) {

    fun bind (income: Income) {
        incomeBinding.tvNameIncome.text = income.name
        incomeBinding.tvPriceIncome.text = income.price.toString()
        incomeBinding.tvDateIncome.text = income.dateString
    }
}