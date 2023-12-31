package com.example.expensetrackerapp.recyclerview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.ExpenseItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.Goals

class ExpenseAdapter (var expenses: List<Expenses>): RecyclerView.Adapter<ExpenseItemViewHolder>() {

    var onExpenseClick: ((Expenses) -> Unit)? = null
    var onDeleteClick: ((Expenses) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExpenseItemLayoutBinding.inflate(inflater, parent, false)
        return ExpenseItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    override fun onBindViewHolder(holder: ExpenseItemViewHolder, position: Int) {
        val currentItem = expenses[position]
        holder.bind(currentItem)

        if (!currentItem.isExpense) {
            val incomeCardColor = ContextCompat.getColor(holder.itemView.context, R.color.blue_1)
            holder.expenseBinding.cardView.setCardBackgroundColor(incomeCardColor)
        } else {
            val incomeCardColor = ContextCompat.getColor(holder.itemView.context, R.color.red_1)
            holder.expenseBinding.cardView.setCardBackgroundColor(incomeCardColor)
        }

        holder.expenseBinding.apply {
            cardView.setOnClickListener {
                onExpenseClick?.invoke(currentItem)
            }
            btnDeleteGoal.setOnClickListener {
                onDeleteClick?.invoke(currentItem)
            }
        }
    }
}