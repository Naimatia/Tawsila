package com.example.tawsila

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
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

class ListeReservationActivity  : AppCompatActivity(), ReservationAdapter.OnItemClickListener {
    private lateinit var reservationAdapter: ReservationAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_reservations)

        recyclerView = findViewById(R.id.recyclerViewClients)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        reservationAdapter = ReservationAdapter(emptyList(), this)
        reservationAdapter.setOnItemClickListener(this)
        recyclerView.adapter = reservationAdapter

        // Call the function to fetch and display reservations
        fetchAndDisplayReservations()

        // Call the function to set up userId and BottomNavigationView
        setUpBottomNavigationView()

    }

    override fun onItemClick(reservation: Reservation) {
        // Handle item click, e.g., launch DetailActivity
        // Add your logic here
    }

    private fun fetchAndDisplayReservations() {

        val  userId = intent.getLongExtra("USER_ID", -1)
        Log.e("id", "user id: $userId")
        val baseUrl = "${BASE_URLF}/participations/$userId"
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
                        reservationAdapter.setFilteredData(reservationsList) // Update adapter data
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
                R.id.carpooling -> {
                    val intent = Intent(this, Profil::class.java)
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