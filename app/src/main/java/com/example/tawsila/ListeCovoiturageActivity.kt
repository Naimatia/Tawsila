package com.example.tawsila

import CovoiturageAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListeCovoiturageActivity : AppCompatActivity(), CovoiturageAdapter.OnItemClickListener {

    private lateinit var covoiturageAdapter: CovoiturageAdapter
    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()
    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_covoiturages)

        recyclerView = findViewById(R.id.recyclerViewClients)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        covoiturageAdapter = CovoiturageAdapter(emptyList(),this)
        covoiturageAdapter.setOnItemClickListener(this)
        recyclerView.adapter = covoiturageAdapter

        // Call the function to fetch and display covoiturages
        fetchAndDisplayCovoiturages()
    }

    override fun onItemClick(covoiturage: Covoiturage) {
        // Handle item click, e.g., launch DetailActivity
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("covoiturage", covoiturage)
        startActivity(intent)
    }

    private fun fetchAndDisplayCovoiturages() {
        val call: Call<List<Covoiturage>> = microserviceApi.getCovoiturages()
        call.enqueue(object : Callback<List<Covoiturage>> {
            override fun onResponse(call: Call<List<Covoiturage>>, response: Response<List<Covoiturage>>) {
                if (response.isSuccessful) {
                    val covoituragesList: List<Covoiturage>? = response.body()
                    if (covoituragesList != null) {
                        covoiturageAdapter.updateData(covoituragesList) // Update adapter data
                        recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("Liste", "Failed to fetch covoiturages: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Covoiturage>>, t: Throwable) {
                Log.e("Liste n", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }
}
