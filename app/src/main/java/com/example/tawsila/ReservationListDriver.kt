package com.example.tawsila

import ApiParticipation
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tawsila.MicroServiceApi.Companion.BASE_URLF
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReservationListDriver : AppCompatActivity(), ReservationDriverAdapter.OnItemClickListener {
    private lateinit var ReservationDriverAdapter: ReservationDriverAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_reservations_driver)

        recyclerView = findViewById(R.id.recyclerViewClients)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        val userId = intent.getLongExtra("USER_ID", -1)
        ReservationDriverAdapter = ReservationDriverAdapter(emptyList(), this, userId)
        ReservationDriverAdapter.setOnItemClickListener(this)
        recyclerView.adapter = ReservationDriverAdapter


        // Call the function to fetch and display reservations
        Log.d("DriverReser", "onCreate called")

        // Call the function to set up userId and BottomNavigationView
        setUpBottomNavigationView()


        fetchAndDisplayReservations()

    }

    override fun onItemClick(reservation: Reservation) {
        // Handle item click, e.g., launch DetailActivity
        // Add your logic here
    }

    private fun fetchAndDisplayReservations() {
        // Retrieve covoiturage ID from the intent
        val covoiturageId = intent.getLongExtra("COVOITURAGE_ID", -1)
        Log.e("idCov" ,"$covoiturageId")

        val baseUrl = "${BASE_URLF}/participationDriver/${covoiturageId}"
        Log.e("URL", "{$baseUrl}")
        val retrofit = Retrofit.Builder()
            .baseUrl(MicroServiceApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .build()
            )
            .build()

        val microserviceApi = retrofit.create(MicroServiceApi::class.java)

        val call: Call<List<Reservation>> = microserviceApi.getFilteredReservations(baseUrl)

        call.enqueue(object : Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                if (response.isSuccessful) {
                    val reservationsList: List<Reservation>? = response.body()
                    if (reservationsList != null) {
                        Log.d("Liste", "Received ${reservationsList.size} reservations")
                        ReservationDriverAdapter.setFilteredData(reservationsList) // Update adapter data
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        Log.e("Liste", "Response body is null")
                    }
                } else {
                    Log.e("Liste", "Failed to fetch filtered reservations: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                Log.e("Liste", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_trajet
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