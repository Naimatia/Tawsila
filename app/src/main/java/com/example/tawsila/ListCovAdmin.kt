package com.example.tawsila

import CovoiturageAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListCovAdmin : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private lateinit var covAdapter: CovAdapter
    private lateinit var recyclerView: RecyclerView
    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_cov)

        recyclerView = findViewById(R.id.recyclerViewCov)
        recyclerView.layoutManager = LinearLayoutManager(this)
        covAdapter = CovAdapter(emptyList())
        recyclerView.adapter = covAdapter

        val listeCovoituragesButton = findViewById<Button>(R.id.listeCovoituragesButton)
        listeCovoituragesButton.setOnClickListener {
            fetchAndDisplayCovoiturages()
        }
        setUpBottomNavigationView()
    }

    private fun fetchAndDisplayCovoiturages() {
        val call: Call<List<Covoiturage>> = microserviceApi.getCovs()
        call.enqueue(object : Callback<List<Covoiturage>> {
            override fun onResponse(call: Call<List<Covoiturage>>, response: Response<List<Covoiturage>>) {
                if (response.isSuccessful) {
                    val covoituragesList: List<Covoiturage>? = response.body()
                    covoituragesList?.let {
                        covAdapter = CovAdapter(it)
                        recyclerView.adapter = covAdapter
                        recyclerView.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("ListeCovoiturage", "Échec de la récupération des covoiturages: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Covoiturage>>, t: Throwable) {
                Log.e("ListeCovoiturage", "Erreur: ${t.message}")
                t.printStackTrace()
            }
        })
    }
    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.liste_driver -> {
                    val intent = Intent(this, ListeDriversActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.liste_client -> {
                    val intent = Intent(this, ListeClientsActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.carpooling -> {
                    val intent = Intent(this, ListCovAdmin::class.java)
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