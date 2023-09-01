package com.example.expensetrackerapp.recyclerview

import android.animation.ObjectAnimator
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.GoalItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.example.expensetrackerapp.roomdatabase.Goals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.checkerframework.checker.units.qual.g

class GoalsItemViewHolder (val goalsBinding: GoalItemLayoutBinding): RecyclerView.ViewHolder(goalsBinding.root) {

    fun bind (goals: Goals) {
        goalsBinding.tvNameGoal.text = goals.name
        goalsBinding.tvPriceGoal.text = goals.price.toString()
        goalsBinding.tvAmountInvestedGoal.text = goals.amountInvested.toString()

        val amountInvested: Int = goals.amountInvested
        val price: Int = goals.price

        goalsBinding.progressBarGoal.max = price
        val currentProgress = amountInvested

        ObjectAnimator.ofInt(goalsBinding.progressBarGoal, "progress", currentProgress)
            .start()
    }
}