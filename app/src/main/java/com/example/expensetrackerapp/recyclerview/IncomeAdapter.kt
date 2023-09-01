package com.example.expensetrackerapp.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.ExpenseItemLayoutBinding
import com.example.expensetrackerapp.databinding.IncomeItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.example.expensetrackerapp.roomdatabase.Goals
import com.example.expensetrackerapp.roomdatabase.Income

class IncomeAdapter (var income: List<Income>): RecyclerView.Adapter<IncomeItemViewHolder>() {

    var onIncomeClick: ((Income) -> Unit)? = null
    var onDeleteClick: ((Income) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = IncomeItemLayoutBinding.inflate(inflater, parent, false)
        return IncomeItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return income.size
    }

    override fun onBindViewHolder(holder: IncomeItemViewHolder, position: Int) {
        val currentItem = income[position]
        holder.bind(currentItem)
        holder.incomeBinding.apply {
            cardView.setOnClickListener {
                onIncomeClick?.invoke(income[position])
            }
            btnDeleteGoal.setOnClickListener {
                onDeleteClick?.invoke(income[position])
            }
        }
    }


}