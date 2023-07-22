package com.example.expensetrackerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.expensetrackerapp.databinding.FragmentReportBinding

class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

}