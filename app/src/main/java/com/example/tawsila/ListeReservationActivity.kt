package com.example.tawsila

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    }

    override fun onItemClick(reservation: Reservation) {
        // Handle item click, e.g., launch DetailActivity
        // Add your logic here
    }

    private fun fetchAndDisplayReservations() {

        val idclient = 2

        val baseUrl = "http://192.168.56.1:3002/participations/$idclient"
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
}