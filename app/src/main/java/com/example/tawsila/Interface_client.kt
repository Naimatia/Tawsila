package com.example.tawsila

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class Interface_client : AppCompatActivity() {
    private lateinit var editTextDate: EditText
    private lateinit var editTextSource: EditText
    private lateinit var editTextDestination: EditText
    private lateinit var buttonRecherche: Button


    private lateinit var sourceAutoCompleteTextView: AutoCompleteTextView
    private lateinit var destinationAutoCompleteTextView: AutoCompleteTextView

    private val cities = ArrayList<String>()


    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,

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

        editTextDate.setHintTextColor(Color.WHITE)


        // Assuming you have the TextInputLayout and AutoCompleteTextView in your XML layout
        sourceAutoCompleteTextView = findViewById(R.id.editTextSource)
        destinationAutoCompleteTextView = findViewById(R.id.editTextDestination)

        // Fetch the list of Tunisian cities asynchronously
        FetchCitiesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)

        // Set an item click listener if needed
        sourceAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = sourceAutoCompleteTextView.adapter.getItem(position).toString()
            // Do something with the selected city
            Toast.makeText(this, "Selected city: $selectedCity", Toast.LENGTH_SHORT).show()
        }
        destinationAutoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = destinationAutoCompleteTextView.adapter.getItem(position).toString()
            // Do something with the selected city
            Toast.makeText(this, "Selected city: $selectedCity", Toast.LENGTH_SHORT).show()
        }

// Add a click listener to the "Recherche" button
        buttonRecherche.setOnClickListener {
            // Get the input values
            val source = sourceAutoCompleteTextView.text.toString()
            val destination = destinationAutoCompleteTextView.text.toString()
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
                    val intent = Intent(this, ListeReservationActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_maps -> {
                    val intent = Intent(this, Maps::class.java)
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
        intent.putExtra("SOURCE", source)
        intent.putExtra("DESTINATION", destination)
        intent.putExtra("DATE", currentDate)
        // For example, create an Intent and pass the values as extras
        val intent = Intent(this, ListeCovoiturageActivity::class.java)
        intent.putExtra("SOURCE", source)
        intent.putExtra("DESTINATION", destination)
        intent.putExtra("DATE", currentDate)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    override fun onBackPressed() {
        val userId = intent.getLongExtra("USER_ID", -1)
        val intent = Intent(this, Interface_client::class.java)
        intent.putExtra("USER_ID", userId)

        // Retrieve input values from Intent
        val source = intent.getStringExtra("SOURCE") ?: ""
        val destination = intent.getStringExtra("DESTINATION") ?: ""
        val date = intent.getStringExtra("DATE") ?: ""

        // Save input values in the intent before finishing
        intent.putExtra("SOURCE", source)
        intent.putExtra("DESTINATION", destination)
        intent.putExtra("DATE", date)

        startActivity(intent)
        finish()
    }



    private inner class FetchCitiesTask : AsyncTask<Void, Void, List<String>>() {

        override fun doInBackground(vararg params: Void?): List<String>? {
            try {
                val url = URL("https://overpass-api.de/api/interpreter?data=%5Bout%3Ajson%5D%5Btimeout%3A25%5D%3Barea%5B%22ISO3166-1%22%3D%22TN%22%5D%5Badmin_level%3D2%5D-%3E.searchArea%3B(node%5B%22place%22%3D%22city%22%5D%28area.searchArea%29%3Bway%5B%22place%22%3D%22city%22%5D%28area.searchArea%29%3Brel%5B%22place%22%3D%22city%22%5D%28area.searchArea%29%3B%29%3Bout%20center%3B")
                val urlConnection = url.openConnection() as HttpURLConnection
                val inputStream = urlConnection.inputStream

                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String? = reader.readLine()

                while (line != null) {
                    response.append(line)
                    line = reader.readLine()
                }

                val json = JSONObject(response.toString())
                val elements = json.getJSONArray("elements")

                val cities = mutableListOf<String>()  // Create a new list to hold cities

                for (i in 0 until elements.length()) {
                    val cityName = elements.getJSONObject(i).getJSONObject("tags").optString("name:fr")
                    if (!cityName.isNullOrBlank()) {
                        cities.add(cityName)
                    }
                }

                Log.d("FetchCitiesTask", "Cities: $cities")

                return cities

            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        override fun onPostExecute(result: List<String>?) {
            if (result != null) {
                // Update the AutoCompleteTextView adapters with the new list of cities
                sourceAutoCompleteTextView.setAdapter(ArrayAdapter(this@Interface_client, android.R.layout.simple_dropdown_item_1line, result))
                destinationAutoCompleteTextView.setAdapter(ArrayAdapter(this@Interface_client, android.R.layout.simple_dropdown_item_1line, result))
                sourceAutoCompleteTextView.threshold = 1
                destinationAutoCompleteTextView.threshold = 1


            } else {
                // Handle the case where the data fetching failed
                Toast.makeText(this@Interface_client, "Failed to fetch cities", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
