package com.example.expensetrackerapp.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.ExpensesIncomeItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.ExpensesIncome

class ExpensesIncomeAdapter (var expensesIncome: List<ExpensesIncome>): RecyclerView.Adapter<ExpensesIncomeItemViewHolder>() {

    var onExpensesIncomeClick: ((ExpensesIncome) -> Unit)? = null
    var onDeleteClick: ((ExpensesIncome) -> Unit)? = null

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

        if (!currentItem.isExpense) {
            val incomeCardColor = ContextCompat.getColor(holder.itemView.context, R.color.blue_1)
            holder.expensesIncomeBinding.cardView.setCardBackgroundColor(incomeCardColor)
        }

        holder.expensesIncomeBinding.apply {
            cardView.setOnClickListener {
                onExpensesIncomeClick?.invoke(currentItem)
            }
            btnDeleteGoal.setOnClickListener {
                onDeleteClick?.invoke(currentItem)
            }
        }
    }

}