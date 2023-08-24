package com.example.expensetrackerapp.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.FragmentHomeBinding
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendOrientation
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pieChart: PieChart
    private lateinit var appDB: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // database instantiation
        appDB = AppDatabase.invoke(requireActivity().applicationContext)

        getTotalExpenses()
        getTotalIncome()

        binding.tvUserName.text = FirebaseAuth.getInstance().currentUser?.email

        var pieChartEntry: ArrayList<PieEntry> = arrayListOf()

        // Pie Chart instantiation
        pieChart = binding.pieChart
        setPieChartValues(pieChartEntry)
        setUpPieChart(pieChartEntry)

        setUpProgressBar()

        return binding.root
    }

    private fun setUpPieChart(pieChartEntry: ArrayList<PieEntry>) {
        // pie chart data setup
        var pieDataSet: PieDataSet = PieDataSet(pieChartEntry, null)
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS, 100)
        pieDataSet.valueTextSize = 20f
        pieDataSet.setDrawValues(false)

        // pie chart setup
        var pieData: PieData = PieData(pieDataSet)
        pieChart.data = pieData

        // pie chart text settings setup
        pieChart.setEntryLabelTextSize(0f)
        pieChart.description.isEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.extraLeftOffset = -80f

        // pie chart legend setup
        val plegend = pieChart.legend
        plegend.isEnabled = true
        val dmSansTypeface = ResourcesCompat.getFont(requireActivity().applicationContext, R.font.dm_sans_medium);
        plegend.typeface = dmSansTypeface
        plegend.textColor = R.color.black
        plegend.textSize = 20f
        plegend.formSize = 20f
        plegend.orientation = LegendOrientation.VERTICAL
        plegend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        plegend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        plegend.xOffset = 0f

        // pie chart center hole setup
        pieChart.holeRadius = 60f
        pieChart.setTransparentCircleAlpha(0)

        pieChart.invalidate()
    }

    private fun setPieChartValues(pieChartEntry: ArrayList<PieEntry>) {
        pieChartEntry.add(PieEntry(250F, "Food"))
        pieChartEntry.add(PieEntry(50F, "Rent"))
        pieChartEntry.add(PieEntry(360F, "Utility"))
        pieChartEntry.add(PieEntry(160F, "School/Work"))
        pieChartEntry.add(PieEntry(160F, "Family"))
        pieChartEntry.add(PieEntry(250F, "Tax"))
        pieChartEntry.add(PieEntry(50F, "Entertainment"))
        pieChartEntry.add(PieEntry(360F, "Travel"))
        pieChartEntry.add(PieEntry(160F, "Gift"))
        pieChartEntry.add(PieEntry(160F, "Misc"))
    }

    private fun getTotalExpenses() {
        var totalExpense: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpenses()) {
                totalExpense += expense.price
            }
            withContext(Dispatchers.IO) {
                binding.tvBalance.text = totalExpense.toString()
                binding.tvExpensesMonth.text = totalExpense.toString()
            }
        }
    }

    private fun getTotalIncome() {
        var totalIncome: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (income in appDB.getIncome().getAllIncome()) {
                totalIncome += income.price
            }
            withContext(Dispatchers.IO) {
                binding.tvIncomeMonth.text = totalIncome.toString()
            }
        }
    }

    private fun setUpProgressBar() {
        var totalExpense: Int = 0
        var totalIncome: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpenses()) {
                totalExpense += expense.price
            }
            for (income in appDB.getIncome().getAllIncome()) {
                totalIncome += income.price
            }
            withContext(Dispatchers.Main) {
                binding.progressBar.max = totalExpense + totalIncome
                val currentProgress = totalIncome

                ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress)
                    .start()
            }
        }
    }

}