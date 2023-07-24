package com.example.expensetrackerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        var pieChartEntry: ArrayList<PieEntry> = arrayListOf()

        // Pie Chart instantiation
        pieChart = binding.pieChart
        setValues(pieChartEntry)
        setUpChart(pieChartEntry)


        return binding.root
    }

    private fun setUpChart(pieChartEntry: ArrayList<PieEntry>) {
        // pie chart data setup
        var pieDataSet: PieDataSet = PieDataSet(pieChartEntry, "My Pie Chart")
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS, 100)
        pieDataSet.valueTextSize = 20f
        pieDataSet.setDrawValues(false)

        // pie chart setup
        var pieData: PieData = PieData(pieDataSet)
        pieChart.data = pieData

        // pie chart text settings setup
        pieChart.setEntryLabelTextSize(20f)
        pieChart.legend.isEnabled = false
        pieChart.description.isEnabled = false
        pieChart.setTouchEnabled(false)

        // pie chart center hole settings setup
        pieChart.center
        pieChart.holeRadius = 60f
        pieChart.setHoleColor(R.color.black)
        pieChart.setTransparentCircleAlpha(0)

        pieChart.invalidate()
    }

    private fun setValues(pieChartEntry: ArrayList<PieEntry>) {
        pieChartEntry.add(PieEntry(250F, "Food"))
        pieChartEntry.add(PieEntry(50F, "Medicine"))
        pieChartEntry.add(PieEntry(360F, "Food"))
        pieChartEntry.add(PieEntry(160F, "Misc"))
    }

}