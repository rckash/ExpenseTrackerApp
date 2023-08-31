package com.example.expensetrackerapp.fragments

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import java.time.LocalDateTime

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var pieChart: PieChart
    private lateinit var appDB: AppDatabase

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        // database instantiation
        appDB = AppDatabase.invoke(requireActivity().applicationContext)

        getTotalExpenses()
        getTotalIncome()

        val dayMonthYearTriple = getDate()
        binding.tvTimespan.text = "${dayMonthYearTriple.second} ${dayMonthYearTriple.third}"

        binding.tvUserName.text = FirebaseAuth.getInstance().currentUser?.email

        var pieChartEntry: ArrayList<PieEntry> = arrayListOf()

        // Pie Chart instantiation
        pieChart = binding.pieChart
        setPieChartValues(pieChartEntry)
        setUpPieChart(pieChartEntry)

        binding.btnThisMonth.isEnabled = false

        binding.btnThisMonth.setOnClickListener {
            binding.btnThisMonth.isEnabled = false
            binding.btnThisWeek.isEnabled = true
            binding.btnThisYear.isEnabled = true

            binding.tvTimespan.text = "${dayMonthYearTriple.second} ${dayMonthYearTriple.third}"
            getTotalExpenses()
        }

        binding.btnThisYear.setOnClickListener {
            binding.btnThisMonth.isEnabled = true
            binding.btnThisWeek.isEnabled = true
            binding.btnThisYear.isEnabled = false

            binding.tvTimespan.text = dayMonthYearTriple.third
        }

        binding.btnThisWeek.setOnClickListener {
            binding.btnThisMonth.isEnabled = true
            binding.btnThisWeek.isEnabled = false
            binding.btnThisYear.isEnabled = true

            val startOfWeek = getStartOfWeek().first
            val endOfWeek = getEndOfWeek().first
            binding.tvTimespan.text = "$startOfWeek - $endOfWeek"
            val startOfWeekInt = getStartOfWeek().second.toInt()
            val endOfWeekInt = getEndOfWeek().second.toInt()
            Log.d("WeekDates", "$startOfWeekInt and $endOfWeekInt")
            getWeekExpenses(startOfWeekInt, endOfWeekInt)
        }

        setUpProgressBar()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(): Triple<String, String, String> {
        // get current date
        val now = LocalDateTime.now()
        val day = now.dayOfMonth.toString()
        val month = now.month.toString()
        val year = now.year.toString()

        val firstCharacter = month[0]
        val restOfString = month.substring(1)

        val formattedMonth = firstCharacter.uppercase() + restOfString.lowercase()

        return Triple(day, formattedMonth, year)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getStartOfWeek(): Pair<String, String> {
        val today = LocalDateTime.now()
        val dayOfWeekToday = today.dayOfWeek.toString()

        // get start of week
        var sundayBefore = today
        when (dayOfWeekToday) {
            "MONDAY" -> sundayBefore = today.minusDays(1)
            "TUESDAY" -> sundayBefore = today.minusDays(2)
            "WEDNESDAY" -> sundayBefore = today.minusDays(3)
            "THURSDAY" -> sundayBefore = today.minusDays(4)
            "FRIDAY" -> sundayBefore = today.minusDays(5)
            "SATURDAY" -> sundayBefore = today.minusDays(6)
            else -> sundayBefore = today
        }
        val startDay = sundayBefore.dayOfMonth.toString()
        var formattedStartDay = "00"
        when (startDay) {
            "1" -> formattedStartDay = "01"
            "2" -> formattedStartDay = "02"
            "3" -> formattedStartDay = "03"
            "4" -> formattedStartDay = "04"
            "5" -> formattedStartDay = "05"
            "6" -> formattedStartDay = "06"
            "7" -> formattedStartDay = "07"
            "8" -> formattedStartDay = "08"
            "9" -> formattedStartDay = "09"
            else -> formattedStartDay = startDay
        }

        val startMonth = sundayBefore.month.toString().substring(0,3)
        var startMonthInt = "00"
        when (startMonth) {
            "JAN" -> startMonthInt = "01"
            "FEB" -> startMonthInt = "02"
            "MAR" -> startMonthInt = "03"
            "APR" -> startMonthInt = "04"
            "MAY" -> startMonthInt = "05"
            "JUN" -> startMonthInt = "06"
            "JUL" -> startMonthInt = "07"
            "AUG" -> startMonthInt = "08"
            "SEP" -> startMonthInt = "09"
            "OCT" -> startMonthInt = "10"
            "NOV" -> startMonthInt = "11"
            "DEC" -> startMonthInt = "12"
        }
        val startYear = sundayBefore.year.toString()

        // start of week date in String
        val startOfWeek = "$startDay $startMonth $startYear"
        // start of week date in Int
        val startOfWeekInt = "$startYear$startMonthInt$formattedStartDay"

        return startOfWeek to startOfWeekInt
    }

    private fun getEndOfWeek(): Pair<String, String> {
        val today = LocalDateTime.now()
        var dayOfWeekToday = today.dayOfWeek.toString()

        // get end of week
        var saturdayAfter = today
        when (dayOfWeekToday) {
            "SUNDAY" -> saturdayAfter = today.plusDays(6)
            "MONDAY" -> saturdayAfter = today.plusDays(5)
            "TUESDAY" -> saturdayAfter = today.plusDays(4)
            "WEDNESDAY" -> saturdayAfter = today.plusDays(3)
            "THURSDAY" -> saturdayAfter = today.plusDays(2)
            "FRIDAY" -> saturdayAfter = today.plusDays(1)
            else -> saturdayAfter = today
        }

        val endDay = saturdayAfter.dayOfMonth.toString()
        var formattedEndDay = "00"
        when (endDay) {
            "1" -> formattedEndDay = "01"
            "2" -> formattedEndDay = "02"
            "3" -> formattedEndDay = "03"
            "4" -> formattedEndDay = "04"
            "5" -> formattedEndDay = "05"
            "6" -> formattedEndDay = "06"
            "7" -> formattedEndDay = "07"
            "8" -> formattedEndDay = "08"
            "9" -> formattedEndDay = "09"
            else -> formattedEndDay = endDay
        }

        val endMonth = saturdayAfter.month.toString().substring(0,3)
        var endMonthInt = "00"
        when (endMonth) {
            "JAN" -> endMonthInt = "01"
            "FEB" -> endMonthInt = "02"
            "MAR" -> endMonthInt = "03"
            "APR" -> endMonthInt = "04"
            "MAY" -> endMonthInt = "05"
            "JUN" -> endMonthInt = "06"
            "JUL" -> endMonthInt = "07"
            "AUG" -> endMonthInt = "08"
            "SEP" -> endMonthInt = "09"
            "OCT" -> endMonthInt = "10"
            "NOV" -> endMonthInt = "11"
            "DEC" -> endMonthInt = "12"
            else -> formattedEndDay
        }
        val endYear = saturdayAfter.year.toString()

        // start of week date in String
        val endOfWeek = "$endDay $endMonth $endYear"
        // start of week date in Int
        val endOfWeekInt = "$endYear$endMonthInt$formattedEndDay"

        return endOfWeek to endOfWeekInt
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

        var foodTotalExpense = 0
        var utilityTotalExpense = 0
        var rentTotalExpense = 0
        var schoolWorkTotalExpense = 0
        var leisureTotalExpense = 0
        var travelTotalExpense = 0
        var giftTotalExpense = 0
        var miscTotalExpense = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("Food")) {
                foodTotalExpense += expense.price
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("Utility")) {
                utilityTotalExpense += expense.price
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("Rent")) {
                rentTotalExpense += expense.price
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("School/Work")) {
                schoolWorkTotalExpense += expense.price
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("Leisure")) {
                leisureTotalExpense += expense.price
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("Travel")) {
                travelTotalExpense += expense.price
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("Gift")) {
                giftTotalExpense += expense.price
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategory("Misc")) {
                miscTotalExpense += expense.price
            }
            withContext(Dispatchers.IO) {
                if (foodTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(foodTotalExpense.toFloat(), "Food"))
                }
                if (utilityTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(utilityTotalExpense.toFloat(), "Utility"))
                }
                if (rentTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(rentTotalExpense.toFloat(), "Rent"))
                }
                if (schoolWorkTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(schoolWorkTotalExpense.toFloat(), "School/Work"))
                }
                if (leisureTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(leisureTotalExpense.toFloat(), "Leisure"))
                }
                if (travelTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(travelTotalExpense.toFloat(), "Travel"))
                }
                if (giftTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(giftTotalExpense.toFloat(), "Gift"))
                }
                if (miscTotalExpense != 0) {
                    pieChartEntry.add(PieEntry(miscTotalExpense.toFloat(), "Misc"))
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTotalExpenses() {
        val currentDate = getDate()
        var monthCode = "__"
        when (currentDate.second) {
            "January" -> { monthCode = "01" }
            "February" -> { monthCode = "02" }
            "March" -> { monthCode = "03" }
            "April" -> { monthCode = "04" }
            "May" -> { monthCode = "05" }
            "June" -> { monthCode = "06" }
            "July" -> { monthCode = "07" }
            "August" -> { monthCode = "08" }
            "September" -> { monthCode = "09" }
            "October" -> { monthCode = "10" }
            "November" -> { monthCode = "11" }
            "December" -> { monthCode = "12" }
        }

        var totalExpense: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByMonth("${currentDate.third}${monthCode}__")) {
                totalExpense += expense.price
            }
            withContext(Dispatchers.IO) {
                binding.tvBalance.text = totalExpense.toString()
                binding.tvExpensesMonth.text = totalExpense.toString()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeekExpenses(startOfWeek: Int, endOfWeek: Int) {

        var weekExpense: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByWeek(startOfWeek, endOfWeek)) {
                weekExpense += expense.price
            }
            withContext(Dispatchers.Main) {
                binding.tvBalance.text = weekExpense.toString()
                binding.tvExpensesMonth.text = weekExpense.toString()
            }
        }
    }

    private fun getTotalIncome() {
        var totalIncome: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (income in appDB.getIncome().getAllIncome()) {
                totalIncome += income.price
            }
            withContext(Dispatchers.Main) {
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
                binding.progressBar.max = totalExpense //+ totalIncome
                val currentProgress = totalIncome

                ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress)
                    .start()
            }
        }
    }

}