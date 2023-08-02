package com.example.expensetrackerapp.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetrackerapp.databinding.GoalItemLayoutBinding
import com.example.expensetrackerapp.roomdatabase.Goals

class GoalAdapter (var goals: List<Goals>): RecyclerView.Adapter<GoalsItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalsItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GoalItemLayoutBinding.inflate(inflater, parent, false)
        return GoalsItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return goals.size
    }

    override fun onBindViewHolder(holder: GoalsItemViewHolder, position: Int) {
        holder.bind(goals[position])
    }
}