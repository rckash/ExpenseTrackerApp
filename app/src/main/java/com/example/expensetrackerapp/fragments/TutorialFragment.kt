package com.example.expensetrackerapp.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.FragmentGoalsBinding
import com.example.expensetrackerapp.databinding.FragmentTutorialBinding


class TutorialFragment : Fragment() {

    private lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTutorialBinding.inflate(layoutInflater, container, false)

        val imageList = ArrayList<SlideModel>() // Create image list

        imageList.add(SlideModel(R.drawable.navtutorial, "Tap the Hamburger Icon to navigate between tabs."))
        imageList.add(SlideModel(R.drawable.addingitemstutorial, "On Report Tab, tap the Floating Action Button to add expense or income items to your database..."))
        imageList.add(SlideModel(R.drawable.addexpensedialog, "...you can then select if the item is an expense or income, its name, price, category and date."))
        imageList.add(SlideModel(R.drawable.editingitemtutorial, "Tap on an expense or income item to edit its details."))
        imageList.add(SlideModel(R.drawable.viewdropdowntutorial, "You can change the items you wish to view from the View Dropdown Menu..."))
        imageList.add(SlideModel(R.drawable.viewstutorial, "You can view expense or income items uniquely..."))
        imageList.add(SlideModel(R.drawable.expensesincometab, "...Or together."))
        imageList.add(SlideModel(R.drawable.querytutorial, "You can view items by month or year using the Month and Year Dropdown Menu"))
        imageList.add(SlideModel(R.drawable.goalsfragment, "The Goals Tab items work similar to items on the Report Tab."))
        imageList.add(SlideModel(R.drawable.progressbartutorial, "On Goals Tab, you can track your goals as you allocate a budget for each with a Progress Bar."))
        imageList.add(SlideModel(R.drawable.goaledit, "You can edit the budget for each goal item as your budget changes for each..."))
        imageList.add(SlideModel(R.drawable.goalfragmentedited, "...and the Progress Bar will adjust accordingly"))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.startSliding(10000)


        return binding.root
    }

}