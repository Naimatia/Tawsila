package com.example.tawsila



import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_trajet)
        setUpBottomNavigationView()
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

        val datePicker = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            editDate.setText(dateFormat.format(selectedDate.time))
        }

        editDate.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val year = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val day = currentDate.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, datePicker, day, month, year)
            datePickerDialog.show()
        }

        // Configuration du TimePicker pour l'heure de départ
        val timePicker = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            editHeureDepart.setText(timeFormat.format(selectedTime.time))
        }

        editHeureDepart.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, timePicker, hour, minute, true)
            timePickerDialog.show()
        }

        val timePicker2 = TimePickerDialog.OnTimeSetListener { _: TimePicker, hourOfDay: Int, minute: Int ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            editHeureArrivee.setText(timeFormat.format(selectedTime.time))
        }

        editHeureArrivee.setOnClickListener {
            val currentTime = Calendar.getInstance()
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)

            val timePickerDialog2 = TimePickerDialog(this, timePicker2, hour, minute, true)
            timePickerDialog2.show()
        }


        val btnAddCovoiturage: Button = findViewById(R.id.buttonAjouterCours)
        btnAddCovoiturage.setOnClickListener {
            val depart = editDepart.text.toString().trim()
            val destination = editDestination.text.toString().trim()
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
                        Toast.makeText(this, "Covoiturage ajouté avec succès!", Toast.LENGTH_SHORT).show()
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
                    val intent = Intent(this, DriverCovoiturageActivity::class.java)
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
                    val intent = Intent(this, profil_image::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_profil -> {
                    val intent = Intent(this, Profil_Driver::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true

                }
                else -> false
            }
        }
    }

}



