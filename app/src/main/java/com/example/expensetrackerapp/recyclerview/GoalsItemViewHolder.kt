package com.example.expensetrackerapp.recyclerview

import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.GoalItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Goals

class GoalsItemViewHolder (val goalsBinding: GoalItemLayoutBinding): RecyclerView.ViewHolder(goalsBinding.root) {

    fun bind (goals: Goals) {
        goalsBinding.tvNameGoal.text = goals.name
        goalsBinding.tvAmountInvestedGoal.text = goals.amountInvested.toString()
        goalsBinding.tvPriceGoal.text = goals.price.toString()
    }
}