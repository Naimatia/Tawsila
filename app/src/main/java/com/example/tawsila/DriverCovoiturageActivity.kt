package com.example.tawsila

import CovoiturageDriverAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tawsila.MicroServiceApi.Companion.BASE_URL
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DriverCovoiturageActivity : AppCompatActivity(), CovoiturageDriverAdapter.OnItemClickListener {

    private lateinit var CovoiturageDriverAdapter: CovoiturageDriverAdapter
    val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)
    private lateinit var recyclerView: RecyclerView
    private lateinit var textSource: TextView
    private lateinit var textDestination: TextView
    private lateinit var textDate: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listcovoiturage_driver)

        recyclerView = findViewById(R.id.recyclerViewClients)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        CovoiturageDriverAdapter = CovoiturageDriverAdapter(emptyList(), this)
        recyclerView.adapter = CovoiturageDriverAdapter
        CovoiturageDriverAdapter.setOnItemClickListener(this)


        // Call the function to fetch and display covoiturages
        fetchAndDisplayCovoiturages()

        val iconBack: ImageView = findViewById(R.id.iconBack)

        // Add a click listener to the iconBack ImageView
        iconBack.setOnClickListener {
            val userId = intent.getLongExtra("USER_ID", -1)
            val intent = Intent(this, Interface_driver::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }
        val userId = intent.getLongExtra("USER_ID", -1)
        Log.e("idNaim", "$userId")
    }

    override fun onItemClick(covoiturage: Covoiturage) {
        Log.d("ItemClick", "Item clicked! covoiturageId: ${covoiturage.id}")


        // Retrieve user ID from the intent
        val userId = intent.getLongExtra("USER_ID", -1)
        // Create the intent and put extra values
        val intent = Intent(this@DriverCovoiturageActivity, ReservationListDriver::class.java)
        intent.putExtra("COVOITURAGE_ID", covoiturage.id)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

    override fun onAcceptRequest(covoiturage: Covoiturage) {
        TODO("Not yet implemented")
    }

    override fun onDeleteCovoiturage(covoiturage: Covoiturage) {
        TODO("Not yet implemented")
    }


    private fun fetchAndDisplayCovoiturages() {
        val userid= intent.getLongExtra("USER_ID", 0)
        Log.e("idDriver", "user id: $userid")
        val baseUrl = "${BASE_URL}/driver/covoituragesDriver/$userid"
       // val url = "${baseUrl}?depart=$source&destination=$destination&date=$date"
        val call: Call<List<Covoiturage>> = microserviceApi.getFilteredCovoiturages(baseUrl)

        call.enqueue(object : Callback<List<Covoiturage>> {
            override fun onResponse(call: Call<List<Covoiturage>>, response: Response<List<Covoiturage>>) {
                if (response.isSuccessful) {
                    val covoituragesList: List<Covoiturage>? = response.body()
                    if (covoituragesList != null) {
                        Log.d("Liste", "Received ${covoituragesList.size} covoiturages")
                        CovoiturageDriverAdapter.setFilteredData(covoituragesList) // Update adapter data
                        recyclerView.visibility = View.VISIBLE
                    } else {
                        Log.e("Liste", "Response body is null")
                    }
                } else {
                    Log.e("Liste", "Failed to fetch filtered covoiturages: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Covoiturage>>, t: Throwable) {
                Log.e("Liste", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
}
