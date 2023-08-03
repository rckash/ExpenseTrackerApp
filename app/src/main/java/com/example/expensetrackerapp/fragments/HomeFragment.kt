package com.example.expensetrackerapp.fragments

import android.animation.ObjectAnimator
import android.content.res.AssetManager
import android.content.res.loader.AssetsProvider
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.Legend.LegendOrientation
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

        binding.progressBar.max = 100
        val currentProgress = 45

        ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress)
            .start()

        return binding.root
    }

    private fun setUpChart(pieChartEntry: ArrayList<PieEntry>) {
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

    private fun setValues(pieChartEntry: ArrayList<PieEntry>) {
        pieChartEntry.add(PieEntry(250F, "Food"))
        pieChartEntry.add(PieEntry(50F, "Medicine"))
        pieChartEntry.add(PieEntry(360F, "Food"))
        pieChartEntry.add(PieEntry(160F, "Misc"))
    }

}