package com.example.tawsila

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.util.Calendar

class driver_trajet : AppCompatActivity() {
    private val selectedDays = mutableListOf<String>()
    private lateinit var selectedDaysTextView: TextView
    private lateinit var allerLayout: LinearLayout
    private lateinit var tableLayout: LinearLayout
    private lateinit var allerCell: TextView
    private lateinit var regulierCell: TextView
    private lateinit var selectedTimeTextView: TextView
    private lateinit var selectedTimeAllerTextView: TextView
    private lateinit var selectedTimeRetourTextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_trajet)

        allerLayout = findViewById(R.id.allerLayout1)
        tableLayout = findViewById(R.id.tableLayout1)
        selectedDaysTextView = findViewById(R.id.selectedDaysTextView1)
        selectedTimeAllerTextView = findViewById(R.id.selectedTimeAllerTextView)

        selectedTimeTextView = findViewById(R.id.selectedTimeAllerTextView) // Initialize selectedTimeTextView

        // Add "Aller" and "Regulier" cells to the "Aller" layout
        allerCell = createCell("Aller", true)
        regulierCell = createCell("Regulier", false)
        allerLayout.addView(allerCell)
        allerLayout.addView(regulierCell)

        // Simulate a click on the "Regulier" cell
        toggleCellState(regulierCell, "Regulier")




        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    val intent = Intent(this, Interface_driver::class.java)

                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_trajet -> {
                    val intent = Intent(this, DriverCovoiturageActivity::class.java)

                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_Add -> {
                    val intent = Intent(this, driver_trajet::class.java)

                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_notification -> {
                    val intent = Intent(this, profil_image::class.java)

                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_profil -> {
                    val intent = Intent(this, Profil::class.java)

                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }


    // Days of the week
        val daysOfWeek = listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim")

        for (day in daysOfWeek) {
            val cell = createCell(day, false)
            tableLayout.addView(cell)
        }

        // Set click listener for "Aller" and "Retour" TextViews
        val allerTextView: TextView = findViewById(R.id.allerTextView1)


        allerTextView.setOnClickListener {
            showTimePickerDialog(isAller = true)
        }



        setAllerRegulierCellClickListeners()
    }

    private fun createCell(label: String, isAller: Boolean): TextView {
        val textView = TextView(this)
        textView.text = label
        textView.isClickable = true

        // Set a click listener for the cell
        textView.setOnClickListener {
            toggleCellState(textView, label)
        }

        val cellWidth = if (isAller) {
            resources.getDimensionPixelSize(R.dimen.reg_width)
        } else {
            resources.getDimensionPixelSize(R.dimen.cell_width)
        }

        val layoutParams = LinearLayout.LayoutParams(
            cellWidth,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Set gravity to center
        textView.gravity = Gravity.CENTER

        // Set layout parameters for the TextView
        textView.layoutParams = layoutParams

        // Set the initial state colors
        if (isAller) {
            textView.setBackgroundResource(R.drawable.cell_border_clicked)
            textView.setTextColor(Color.WHITE)
            textView.tag = true
            selectedDays.add(label)
        } else {
            textView.setBackgroundResource(R.drawable.cell_border)
            textView.setTextColor(Color.BLUE)
            textView.tag = false
        }

        // Add padding to the TextView
        textView.setPadding(
            resources.getDimensionPixelSize(R.dimen.cell_padding),
            resources.getDimensionPixelSize(R.dimen.cell_padding),
            resources.getDimensionPixelSize(R.dimen.cell_padding),
            resources.getDimensionPixelSize(R.dimen.cell_padding)
        )

        return textView
    }

    private fun toggleCellState(cell: TextView, label: String) {
        val isClicked = cell.tag as? Boolean ?: false

        if (!isClicked) {
            // Append the selected label to the list only if it's a day of the week
            if (!label.equals("Aller", ignoreCase = true) && !label.equals("Regulier", ignoreCase = true)) {
                selectedDays.add(label)
            }
        } else {
            // Remove the unselected label from the list only if it's a day of the week
            if (!label.equals("Aller", ignoreCase = true) && !label.equals("Regulier", ignoreCase = true)) {
                selectedDays.remove(label)
            }
        }

        // Update the text view with the selected labels excluding "Aller" and "Regulier"
        if (!selectedDays.contains("Aller") && !selectedDays.contains("Regulier")) {
            updateSelectedDaysTextView(selectedDays)
        } else {
            // If "Aller" or "Regulier" is present, update the text view with only the days
            val selectedDaysWithoutAllerRegulier = selectedDays.filter { it != "Aller" && it != "Regulier" }
            updateSelectedDaysTextView(selectedDaysWithoutAllerRegulier)
        }

        // Toggle the click state
        cell.tag = !isClicked

        // Update background colors based on the click state
        updateAllerRegulierCellColors()

        // Update day colors
        updateDayColors()
    }

    private fun updateSelectedDaysTextView(days: List<String>) {
        val selectedText = days.joinToString(", ")
        selectedDaysTextView.text = selectedText
    }

    private fun setAllerRegulierCellClickListeners() {
        allerCell.setOnClickListener {
            toggleCellState(allerCell, "Aller")
            toggleCellState(regulierCell, "Regulier")
        }

        regulierCell.setOnClickListener {
            toggleCellState(regulierCell, "Regulier")
            toggleCellState(allerCell, "Aller")
        }
    }

    private fun updateAllerRegulierCellColors() {
        // Update background colors based on the click state
        if (allerCell.tag as? Boolean == true) {
            allerCell.setBackgroundResource(R.drawable.cell_border_clicked)
            allerCell.setTextColor(Color.WHITE)

            val layoutParams = regulierCell.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = resources.getDimensionPixelSize(R.dimen.reg_width)
            regulierCell.layoutParams = layoutParams

            regulierCell.setBackgroundResource(R.drawable.cell_border)
            regulierCell.setTextColor(Color.BLUE)
        } else {
            allerCell.setBackgroundResource(R.drawable.cell_border)
            allerCell.setTextColor(Color.BLUE)

            val layoutParams = regulierCell.layoutParams as LinearLayout.LayoutParams
            layoutParams.width = resources.getDimensionPixelSize(R.dimen.reg_width)
            regulierCell.layoutParams = layoutParams

            regulierCell.setBackgroundResource(R.drawable.cell_border_clicked)
            regulierCell.setTextColor(Color.WHITE)
        }
    }

    private fun updateDayColors() {
        // Update the colors of the days based on the selected state
        for (i in 0 until tableLayout.childCount) {
            val dayCell = tableLayout.getChildAt(i) as? TextView
            val dayLabel = dayCell?.text.toString()

            if (selectedDays.contains(dayLabel)) {
                dayCell?.setBackgroundResource(R.drawable.cell_border_clicked)
                dayCell?.setTextColor(Color.WHITE)
            } else {
                dayCell?.setBackgroundResource(R.drawable.cell_border)
                dayCell?.setTextColor(Color.BLUE)
            }
        }
    }

    private fun showTimePickerDialog(isAller: Boolean) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                updateTime(selectedHour, selectedMinute, isAller)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun updateTime(hour: Int, minute: Int, isAller: Boolean) {
        val formattedHour = if (hour < 10) "0$hour" else "$hour"
        val formattedMinute = if (minute < 10) "0$minute" else "$minute"

        val selectedTime = "$formattedHour:$formattedMinute"

        // Use isAller to determine which TextView to update
        if (isAller) {
            selectedTimeAllerTextView.text = selectedTime
        } else {
            selectedTimeRetourTextView.text = selectedTime
        }
    }

    // Fonction pour sauvegarder les données en utilisant Volley
    private fun saveDataWithVolley() {
        val url = "URL_DE_VOTRE_API" // Remplacez cela par l'URL réelle de votre API

        // Créez le corps de la requête avec les données à sauvegarder
        val requestBody = JSONObject().apply {
            put("selectedDays", selectedDays)
            put("allerTime", selectedTimeAllerTextView.text.toString())
            put("retourTime", selectedTimeRetourTextView.text.toString())
        }

        // Créez une requête POST avec Volley
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            Response.Listener { response ->
                // Gérez la réponse de l'API en cas de succès
                val successMessage = response.getString("message")
                Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()

                // Vous pouvez également rediriger l'utilisateur vers une autre activité si nécessaire
                // val intent = Intent(this, NextActivity::class.java)
                // startActivity(intent)
            },
            Response.ErrorListener { error: VolleyError ->
                // Gérez les erreurs de l'API ici
                val errorMessage = "Erreur lors de la sauvegarde des données: ${error.message}"
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        )

        // Ajoutez la requête à la file d'attente de Volley pour l'exécution
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    fun onSuivantClick(view: View) {
        // Afficher une alerte de confirmation
        showConfirmationAlert()
    }

    private fun showConfirmationAlert() {
        Toast.makeText(this, "Données sauvegardées avec succès!", Toast.LENGTH_SHORT).show()
    }



}









