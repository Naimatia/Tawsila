package com.example.tawsila



import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tawsila.MicroServiceApi.Companion.BASE_URL
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class driver_trajet: AppCompatActivity() {
    private lateinit var editDepart: EditText
    private lateinit var editDestination: EditText
    private lateinit var editPrice: EditText
    private lateinit var editDate: EditText
    private lateinit var editPlace: EditText
    private lateinit var editPhone: EditText
    private lateinit var editBagage: EditText
    private lateinit var editMarque: EditText
    private lateinit var editHeureDepart: EditText
    private lateinit var editHeureArrivee: EditText
    private lateinit var sourceAutoCompleteTextView: AutoCompleteTextView
    private lateinit var destinationAutoCompleteTextView: AutoCompleteTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_trajet)

        editDepart = findViewById(R.id.editDepart)
        editDestination = findViewById(R.id.editDestination)
        editPrice = findViewById(R.id.editPrice)
        editDate = findViewById(R.id.editDate)
        editPlace = findViewById(R.id.editPlace)
        editPhone = findViewById(R.id.editPhone)
        editBagage = findViewById(R.id.editBagage)
        editMarque = findViewById(R.id.editMarque)
        editHeureDepart = findViewById(R.id.editheureDepart)
        editHeureArrivee = findViewById(R.id.editheureArrivee)
        // Call the function to set up userId and BottomNavigationView
        setUpBottomNavigationView()
        // Assuming you have the TextInputLayout and AutoCompleteTextView in your XML layout
        sourceAutoCompleteTextView = findViewById(R.id.editDepart)
        destinationAutoCompleteTextView = findViewById(R.id.editDestination)

        // Fetch the list of Tunisian cities asynchronously
        FetchCitiesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)





        editDate.setOnClickListener {
            showDatePicker()
        }

        editHeureDepart.setOnClickListener {
            showTimePicker(editHeureDepart)
        }

        editHeureArrivee.setOnClickListener {
            showTimePicker(editHeureArrivee)
        }


        val btnAddCovoiturage: Button = findViewById(R.id.buttonAjouterCours)
        btnAddCovoiturage.setOnClickListener {
            val depart = sourceAutoCompleteTextView.text.toString().trim()
            val destination = destinationAutoCompleteTextView.text.toString().trim()
            val price = editPrice.text.toString().trim().toInt()
            val date = editDate.text.toString().trim()
            val place = editPlace.text.toString().trim().toInt()
            val phone = editPhone.text.toString().trim()
            val bagage = editBagage.text.toString().trim()
            val marque = editMarque.text.toString().trim()
            val heureDepart = editHeureDepart.text.toString().trim()
            val heureArrive = editHeureArrivee.text.toString().trim()

            if (depart.isNotEmpty() && destination.isNotEmpty()  && phone.isNotEmpty() && bagage.isNotEmpty() && marque.isNotEmpty()) {
                val covoiturageData = JSONObject()
                covoiturageData.put("driver",intent.getLongExtra("USER_ID",0))
                covoiturageData.put("depart", depart)
                covoiturageData.put("destination", destination)
                covoiturageData.put("price", price)
                covoiturageData.put("date", date.toString())
                covoiturageData.put("place", place.toString())
                covoiturageData.put("phone", phone.toString())
                covoiturageData.put("bagage", bagage.toString())
                covoiturageData.put("marque", marque)
                covoiturageData.put("heureDepart", heureDepart.toString())
                covoiturageData.put("heureArrive", heureArrive.toString())

                val queue = Volley.newRequestQueue(this)
                val url = "${BASE_URL}/driver/covoiturages"
                Log.d("COVOITURAGE_DATA : ", covoiturageData.toString())

                val request = JsonObjectRequest(
                    Request.Method.POST, url, covoiturageData,
                    { response ->
                        val  userId = intent.getLongExtra("USER_ID", -1)
                        Toast.makeText(this, "Covoiturage ajouté avec succès!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Interface_driver::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                        // Réinitialiser les champs du formulaire si nécessaire
                    },
                    { error ->
                        if (error.networkResponse != null) {
                            val statusCode = error.networkResponse.statusCode
                            val data = String(error.networkResponse.data, Charset.defaultCharset())
                            Toast.makeText(this, "Erreur lors de l'ajout du covoiturage: Status code $statusCode, Data: $data", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erreur lors de l'ajout du covoiturage: ${error.message}", Toast.LENGTH_SHORT).show()
                        }})

                queue.add(request)

            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showDatePicker() {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            editDate.setText(dateFormat.format(selectedDate.time))
        }, year, month, day)

        datePickerDialog.show()
    }
    private fun showTimePicker(editText: EditText) {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            editText.setText(timeFormat.format(selectedTime.time))
        }, hour, minute, true)

        timePickerDialog.show()
    }

    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_Add
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    val intent = Intent(this, Interface_driver::class.java)
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
                R.id.bottom_Add -> {
                    val intent = Intent(this, driver_trajet::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_notification -> {
                    val intent = Intent(this, Profil::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_profil -> {
                    // Update userId if needed
                    startActivity(Intent(applicationContext, Profil::class.java).apply {
                        putExtra("USER_ID", userId)
                    })
                    finish()
                    true
                }
                else -> false
            }
        }
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
                sourceAutoCompleteTextView.setAdapter(ArrayAdapter(this@driver_trajet, android.R.layout.simple_dropdown_item_1line, result))
                destinationAutoCompleteTextView.setAdapter(ArrayAdapter(this@driver_trajet, android.R.layout.simple_dropdown_item_1line, result))
                sourceAutoCompleteTextView.threshold = 1
                destinationAutoCompleteTextView.threshold = 1


            } else {
                // Handle the case where the data fetching failed
                Toast.makeText(this@driver_trajet, "Failed to fetch cities", Toast.LENGTH_SHORT).show()
            }
        }
    }

}



