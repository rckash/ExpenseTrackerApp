package com.example.expensetrackerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.example.expensetrackerapp.LoginActivity
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.databinding.DialogBackupBinding
import com.example.expensetrackerapp.databinding.FragmentBackupAndSyncBinding
import com.example.expensetrackerapp.roomdatabase.AppDatabase
import com.example.expensetrackerapp.roomdatabase.Expenses
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.exp


class BackupAndSyncFragment : Fragment() {
    private lateinit var binding: FragmentBackupAndSyncBinding

    private lateinit var appDB: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBackupAndSyncBinding.inflate(layoutInflater, container, false)

        // Initialize Database
        appDB = AppDatabase.invoke(requireActivity().applicationContext)

        binding.btnUpload.setOnClickListener {

            showBackupDialog()

        }

        binding.btnDownload.setOnClickListener {

            showSyncDialog()

        }

        return binding.root
    }


    private fun upload() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()
        val db = Firebase.firestore

        GlobalScope.launch(Dispatchers.IO) {
            var documentNameCtr = 0

            for (expense in appDB.getExpenses().getAllExpenseAndIncome()) {
                val city = hashMapOf(
                    "name" to expense.name,
                    "price" to expense.price,
                    "category" to expense.category,
                    "dateInt" to expense.dateInt,
                    "dateString" to expense.dateString,
                    "isExpense" to expense.isExpense
                )

                val expenses = db.collection("$userUid")
                    .document("myData")
                    .collection("expenseAndIncomeData")
                    .document("item$documentNameCtr")

                // Add a new document with a generated ID
                expenses.set(city)
                    .addOnSuccessListener { documentReference ->
                        Toast.makeText(requireActivity().applicationContext, "Uploaded to Firebase", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireActivity().applicationContext, "Failed to upload to Online Database", Toast.LENGTH_SHORT).show()
                        Log.w("TAG", "Error adding document", e)
                    }

                documentNameCtr++

            }
            withContext(Dispatchers.Main) {
                Toast.makeText(requireActivity().applicationContext, "Synced", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun download() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()
        val db = Firebase.firestore

        // Get the collection of expenses
        val expenses = db.collection("$userUid")
            .document("myData")
            .collection("expenseAndIncomeData")

        // Get all the documents in the collection
        expenses
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val name =  document.data["name"].toString()
                    val price=  document.data["price"] as Long
                    val category =  document.data["category"].toString()
                    val dateInt = document.data["dateInt"] as Long
                    val dateString =  document.data["dateString"].toString()
                    val isExpense = document.data["isExpense"] as Boolean

                    saveExpense(
                        Expenses(0, name, price.toInt(), category, dateInt.toInt(), dateString, isExpense, userUid)
                    )
                    Log.d("TAG", "${document.id} => ${document.data}")

                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }


    }

    private fun saveExpense(expenses: Expenses) {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().addExpense(expenses)
        }
        Toast.makeText(requireActivity().applicationContext, "Expense Saved", Toast.LENGTH_SHORT).show()
        Toast.makeText(requireActivity().applicationContext, "${expenses.id.toString()}", Toast.LENGTH_SHORT).show()
    }

    private fun deleteCollectionDocument() {
        val user = FirebaseAuth.getInstance().currentUser
        val userUid = user?.uid.toString()
        val db = Firebase.firestore

        // Get the reference to the collection to delete.
        val expenses = db.collection("$userUid")
            .document("myData")

        // Get a list of all the documents in the collection.
        expenses.delete()

        Toast.makeText(requireActivity().applicationContext, "document deleted", Toast.LENGTH_SHORT).show()
    }

    private fun deleteAll() {
        GlobalScope.launch(Dispatchers.IO) {
            appDB.getExpenses().deleteAll()
        }
    }

    private fun showBackupDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Upload to Cloud")

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_backup, null)
        val dialogBinding = DialogBackupBinding.bind(dialogLayout)
        alertDialogBuilder.setView(dialogLayout)



        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->

            binding.btnDownload.isEnabled = false
            binding.btnUpload.isEnabled = false
            binding.progressBar2.isVisible = true

            deleteCollectionDocument()
            upload()
            dialog.dismiss()

            binding.progressBar2.isVisible = false
            binding.btnDownload.isEnabled = true
            binding.btnUpload.isEnabled = true

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->

            dialog.dismiss()

        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun showSyncDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Upload to Cloud")

        val dialogLayout = layoutInflater.inflate(R.layout.dialog_backup, null)
        val dialogBinding = DialogBackupBinding.bind(dialogLayout)
        alertDialogBuilder.setView(dialogLayout)

        dialogBinding.tvDescription.setText("This will overwrite your current local database. Do you wish to proceed?")

        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->

            binding.btnDownload.isEnabled = false
            binding.btnUpload.isEnabled = false
            binding.progressBar2.isVisible = true

            deleteAll()
            download()
            dialog.dismiss()

            binding.progressBar2.isVisible = false
            binding.btnDownload.isEnabled = true
            binding.btnUpload.isEnabled = true

        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->

            dialog.dismiss()

        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



}