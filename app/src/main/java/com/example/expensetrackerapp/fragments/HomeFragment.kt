package com.example.expensetrackerapp.fragments

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
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
import com.google.firebase.firestore.model.Values
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

        getMonthExpenses()

        val dayMonthYearTriple = getDate()
        binding.tvTimespan.text = "${dayMonthYearTriple.second} ${dayMonthYearTriple.third}"

        binding.tvUserName.text = FirebaseAuth.getInstance().currentUser?.email

        var pieChartEntryWeek: ArrayList<PieEntry> = arrayListOf()
        var pieChartEntryMonth: ArrayList<PieEntry> = arrayListOf()
        var pieChartEntryYear: ArrayList<PieEntry> = arrayListOf()

        // Pie Chart instantiation
        pieChart = binding.pieChart
        setPieChartValuesMonth(pieChartEntryMonth)
        setUpPieChart(pieChartEntryMonth)

        setPieChartValuesWeek(pieChartEntryWeek)

        setPieChartValuesYear(pieChartEntryYear)

        binding.btnThisMonth.isEnabled = false

        binding.btnThisWeek.setOnClickListener {
            binding.btnThisMonth.isEnabled = true
            binding.btnThisYear.isEnabled = true
            binding.btnThisWeek.isEnabled = false

            // change color of buttons
            val blueColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.blue_1)
            val greenColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.green_1)
            binding.btnThisWeek.setBackgroundColor(blueColor)
            binding.btnThisMonth.setBackgroundColor(greenColor)
            binding.btnThisYear.setBackgroundColor(greenColor)

            // Pie Chart instantiation
            setUpPieChart(pieChartEntryWeek)

            val startOfWeek = getStartOfWeek().first
            val endOfWeek = getEndOfWeek().first
            binding.tvTimespan.text = "$startOfWeek - $endOfWeek"
            val startOfWeekInt = getStartOfWeek().second.toInt()
            val endOfWeekInt = getEndOfWeek().second.toInt()
            getWeekExpenses(startOfWeekInt, endOfWeekInt)
        }

        binding.btnThisMonth.setOnClickListener {
            binding.btnThisMonth.isEnabled = false
            binding.btnThisWeek.isEnabled = true
            binding.btnThisYear.isEnabled = true

            // change color of buttons
            val blueColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.blue_1)
            val greenColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.green_1)
            binding.btnThisWeek.setBackgroundColor(greenColor)
            binding.btnThisMonth.setBackgroundColor(blueColor)
            binding.btnThisYear.setBackgroundColor(greenColor)

            // Pie Chart instantiation
            setUpPieChart(pieChartEntryMonth)

            binding.tvTimespan.text = "${dayMonthYearTriple.second} ${dayMonthYearTriple.third}"
            getMonthExpenses()
        }

        binding.btnThisYear.setOnClickListener {
            binding.btnThisMonth.isEnabled = true
            binding.btnThisWeek.isEnabled = true
            binding.btnThisYear.isEnabled = false

            // change color of buttons
            val blueColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.blue_1)
            val greenColor = ContextCompat.getColor(requireActivity().applicationContext, R.color.green_1)
            binding.btnThisWeek.setBackgroundColor(greenColor)
            binding.btnThisMonth.setBackgroundColor(greenColor)
            binding.btnThisYear.setBackgroundColor(blueColor)

            // Pie Chart instantiation
            setUpPieChart(pieChartEntryYear)

            binding.tvTimespan.text = dayMonthYearTriple.third
            getYearlyExpenses()
        }

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
    private fun getDateCode(): Triple<String, String, String> {
        // get current date
        val now = LocalDateTime.now()
        val day = now.dayOfMonth.toString()
        val monthName = now.month.toString()
        var month = "Jan"
        when (monthName) {
            "JANUARY" -> month = "01"
            "FEBRUARY" -> month = "02"
            "MARCH" -> month = "03"
            "APRIL" -> month = "04"
            "MAY" -> month = "05"
            "JUNE" -> month = "06"
            "JULY" -> month = "07"
            "AUGUST" -> month = "08"
            "SEPTEMBER" -> month = "09"
            "OCTOBER" -> month = "10"
            "NOVEMBER" -> month = "11"
            "DECEMBER" -> month = "12"
        }
        val year = now.year.toString()

        return Triple(day, month, year)
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
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 100)
        pieDataSet.valueTextSize = 20f
        pieDataSet.setDrawValues(false)

        // pie chart setup
        var pieData: PieData = PieData(pieDataSet)
        pieChart.data = pieData

        // pie chart text settings setup
        pieChart.setEntryLabelTextSize(0f)
        pieChart.description.isEnabled = false
        pieChart.setTouchEnabled(false)
        pieChart.extraRightOffset = 50f

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
        pieChart.holeRadius = 0f
        pieChart.setTransparentCircleAlpha(0)

        pieChart.invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPieChartValuesMonth(pieChartEntry: ArrayList<PieEntry>) {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val month = getDateCode().second
        val year = getDateCode().third
        val monthQueryCode = "$year$month" + "__"
        Log.d("monthQuery", "$monthQueryCode")

        var foodTotalExpense = 0
        var utilityTotalExpense = 0
        var rentTotalExpense = 0
        var schoolWorkTotalExpense = 0
        var leisureTotalExpense = 0
        var travelTotalExpense = 0
        var giftTotalExpense = 0
        var miscTotalExpense = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Food", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    foodTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Utility", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    utilityTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Rent", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    rentTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("School/Work", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    schoolWorkTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Leisure", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    leisureTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Travel", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    travelTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Gift", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    giftTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Misc", "$monthQueryCode")) {
                if (userUid == expense.user) {
                    miscTotalExpense += expense.price
                }
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
    val user = FirebaseAuth.getInstance().currentUser
    val userUid = user?.uid.toString()

    private fun setPieChartValuesYear(pieChartEntry: ArrayList<PieEntry>) {

        val year = getDateCode().third
        val yearQueryCode = "$year" + "____"
        Log.d("yearQuery", "$yearQueryCode")

        var foodTotalExpense = 0
        var utilityTotalExpense = 0
        var rentTotalExpense = 0
        var schoolWorkTotalExpense = 0
        var leisureTotalExpense = 0
        var travelTotalExpense = 0
        var giftTotalExpense = 0
        var miscTotalExpense = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Food", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    foodTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Utility", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    utilityTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Rent", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    rentTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("School/Work", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    schoolWorkTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Leisure", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    leisureTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Travel", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    travelTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Gift", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    giftTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDate("Misc", "$yearQueryCode")) {
                if (userUid == expense.user) {
                    miscTotalExpense += expense.price
                }
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
    private fun setPieChartValuesWeek(pieChartEntry: ArrayList<PieEntry>) {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val weekStart = getStartOfWeek().second
        val weekEnd = getEndOfWeek().second
        Log.d("StartAndEndOfWeek", "$weekStart $weekEnd")

        var foodTotalExpense = 0
        var utilityTotalExpense = 0
        var rentTotalExpense = 0
        var schoolWorkTotalExpense = 0
        var leisureTotalExpense = 0
        var travelTotalExpense = 0
        var giftTotalExpense = 0
        var miscTotalExpense = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("Food", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    foodTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("Utility", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    utilityTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("Rent", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    rentTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("School/Work", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    schoolWorkTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("Leisure", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    leisureTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("Travel", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    travelTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("Gift", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    giftTotalExpense += expense.price
                }
            }
            for (expense in appDB.getExpenses().getAllExpensesSortedByCategoryDateSpan("Misc", "$weekStart", "$weekEnd")) {
                if (userUid == expense.user) {
                    miscTotalExpense += expense.price
                }
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
    private fun getYearlyExpenses() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        val currentDate = getDate()

        var yearExpense: Int = 0
        var yearIncome: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByMonth("${currentDate.third}____")) {
                if (userUid == expense.user) {
                    yearExpense += expense.price
                }
            }
            for (income in appDB.getExpenses().getAllIncomeSortedByMonth("${currentDate.third}____")) {
                if (userUid == income.user) {
                    yearIncome += income.price
                }
            }
            withContext(Dispatchers.Main) {
                // UI setup
                binding.tvBalance.text = yearExpense.toString()
                binding.tvExpensesMonth.text = yearExpense.toString()
                binding.tvIncomeMonth.text = yearIncome.toString()

                // progress bar setup
                binding.progressBar.max =  yearIncome
                val currentProgress = yearExpense

                ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress)
                    .start()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMonthExpenses() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

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

        var monthExpense: Int = 0
        var monthIncome: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByMonth("${currentDate.third}${monthCode}__")) {
                if (userUid == expense.user) {
                    monthExpense += expense.price
                }
            }
            for (income in appDB.getExpenses().getAllIncomeSortedByMonth("${currentDate.third}${monthCode}__")) {
                if (userUid == income.user) {
                    monthIncome += income.price
                }
            }
            withContext(Dispatchers.Main) {
                // UI setup
                binding.tvBalance.text = monthExpense.toString()
                binding.tvExpensesMonth.text = monthExpense.toString()
                binding.tvIncomeMonth.text = monthIncome.toString()

                // progress bar setup
                binding.progressBar.max =  monthIncome
                val currentProgress = monthExpense

                ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress)
                    .start()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getWeekExpenses(startOfWeek: Int, endOfWeek: Int) {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()

        var weekExpense: Int = 0
        var weekIncome: Int = 0
        GlobalScope.launch(Dispatchers.IO) {
            for (expense in appDB.getExpenses().getAllExpensesSortedByWeek(startOfWeek, endOfWeek)) {
                if (userUid == expense.user) {
                    weekExpense += expense.price
                }
            }
            for (income in appDB.getExpenses().getAllIncomeSortedByWeek(startOfWeek, endOfWeek)) {
                if (userUid == income.user) {
                    weekIncome += income.price
                }
            }
            withContext(Dispatchers.Main) {
                // UI setup
                binding.tvBalance.text = weekExpense.toString()
                binding.tvExpensesMonth.text = weekExpense.toString()
                binding.tvIncomeMonth.text = weekIncome.toString()

                // progress bar setup
                binding.progressBar.max = weekIncome
                val currentProgress = weekExpense

                ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress)
                    .start()
            }
        }
    }

}