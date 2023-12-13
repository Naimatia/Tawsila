package com.example.tawsila

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ListeDriversActivity : AppCompatActivity() {

    // ... (autres variables, initialisations, etc.)

    private lateinit var driverAdapter: DriverAdapter
    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_drivers)
        setUpBottomNavigationView()
        // ... (initialisation des vues, etc.)

        recyclerView = findViewById(R.id.recyclerViewDrivers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        driverAdapter = DriverAdapter(emptyList())
        recyclerView.adapter = driverAdapter

        val listeDriversButton = findViewById<Button>(R.id.listeDriversButton)
        listeDriversButton.setOnClickListener {
            fetchAndDisplayDrivers()
        }
    }

    private fun fetchAndDisplayDrivers() {
        // ... (votre code pour récupérer les conducteurs depuis l'API)

        val call: Call<List<ClientDTO>> = microserviceApi.getDrivers()
        call.enqueue(object : Callback<List<ClientDTO>> {
            override fun onResponse(call: Call<List<ClientDTO>>, response: Response<List<ClientDTO>>) {
                if (response.isSuccessful) {
                    val driversList: List<ClientDTO>? = response.body()
                    if (driversList != null) {
                        driverAdapter = DriverAdapter(driversList)
                        recyclerView.adapter = driverAdapter // Attacher l'adaptateur au RecyclerView
                        recyclerView.visibility = View.VISIBLE // Rendre le RecyclerView visible
                    }

                } else {
                    // Gérer la réponse non réussie
                    Log.e("ListeDriversActivity", "Échec de la récupération des conducteurs: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ClientDTO>>, t: Throwable) {
                // Gérer les erreurs réseau ou autres pendant la récupération des conducteurs
                Log.e("ListeDriversActivity", "Erreur: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.liste_driver
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Acceuil -> {
                    val intent = Intent(this, Interface_admin::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
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
                R.id.profile_image -> {
                    val intent = Intent(this, Profil_Admin::class.java)
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