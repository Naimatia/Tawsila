package com.example.tawsila

import android.app.DatePickerDialog
import android.content.AttributionSource
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

class Interface_client : AppCompatActivity() {
    private lateinit var editTextDate: EditText
    private lateinit var editTextSource: EditText
    private lateinit var editTextDestination: EditText
    private lateinit var buttonRecherche: Button




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_interface_client)
        // Call the function to set up userId and BottomNavigationView
        setUpBottomNavigationView()
        val  userId = intent.getLongExtra("USER_ID", -1)
        Log.e("id", "user id: ${userId}")
        // Initialize views
        editTextDate = findViewById(R.id.editTextDate)
        editTextSource = findViewById(R.id.editTextSource)
        editTextDestination = findViewById(R.id.editTextDestination)
        buttonRecherche = findViewById(R.id.buttonRecherche)

        // Add a click listener to the "Recherche" button
        buttonRecherche.setOnClickListener {
            // Get the input values
            val source = editTextSource.text.toString()
            val destination = editTextDestination.text.toString()
            val date = editTextDate.text.toString()

            // Pass the values to the next activity
            navigateToNextActivity(source, destination, date)
        }
        editTextDate.setOnClickListener {
            showDatePickerDialog()
        }



    }
    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    val intent = Intent(this, Interface_client::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_trajet -> {
                    val intent = Intent(this, ListeCovoiturageActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.carpooling -> {
                    val intent = Intent(this, Profil::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_profil -> {
                    val intent = Intent(this, Profil::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Mettez à jour le champ EditText avec la date sélectionnée
                val formattedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                editTextDate.setText(formattedDate)
            },
            year,
            month,
            dayOfMonth
        )

        // Définissez la date minimale (facultatif)
        // datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        datePickerDialog.show()
    }
    private fun navigateToNextActivity(source: String, destination: String, date: String ) {
        // If no date is provided, use the current date
        val currentDate = if (date.isEmpty()) {
            val calendar = Calendar.getInstance()
            String.format("%02d-%02d-%04d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR))
        } else {
            date
        }
        val  userId = intent.getLongExtra("USER_ID", -1)
        Log.e("id", "user id: ${userId}")

        // For example, create an Intent and pass the values as extras
        val intent = Intent(this, ListeCovoiturageActivity::class.java)
        intent.putExtra("SOURCE", source)
        intent.putExtra("DESTINATION", destination)
        intent.putExtra("DATE", currentDate)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }


}