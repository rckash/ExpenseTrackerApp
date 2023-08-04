package com.example.expensetrackerapp.recyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpensesIncomeItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.ExpensesIncome

class ExpensesIncomeAdapter (var expensesIncome: List<ExpensesIncome>): RecyclerView.Adapter<ExpensesIncomeItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesIncomeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExpensesIncomeItemLayoutBinding.inflate(inflater, parent, false)
        return ExpensesIncomeItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return expensesIncome.size
    }

    override fun onBindViewHolder(holder: ExpensesIncomeItemViewHolder, position: Int) {
        val currentItem = expensesIncome[position]
        holder.bind(currentItem)

        if (currentItem.isExpense == true) {
            holder.expensesIncomeBinding.cardView.setCardBackgroundColor(Color.rgb(159, 20, 32))
        }
    }

}