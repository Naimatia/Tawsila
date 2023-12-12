package com.example.tawsila

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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

class ListeClientsActivity : AppCompatActivity(),ClientAdapter.OnDeleteClickListener {

    private lateinit var clientAdapter: ClientAdapter
    private lateinit var recyclerView: RecyclerView
    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_clients)
        setUpBottomNavigationView()





        // Initialize RecyclerView and set its layout manager
        recyclerView = findViewById(R.id.recyclerViewClients)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter and set it to the RecyclerView
        clientAdapter = ClientAdapter(emptyList(), this)
        recyclerView.adapter = clientAdapter

        val listeClientsButton = findViewById<Button>(R.id.listeClientsButton)
        listeClientsButton.setOnClickListener {
            fetchAndDisplayClients()
        }
    }
    override fun onDeleteClick(userId: Long) {
        deleteClientById(userId)
    }

    private fun fetchAndDisplayClients() {
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayoutClientsContainer)

        val call: Call<List<ClientDTO>> = microserviceApi.getClients()
        call.enqueue(object : Callback<List<ClientDTO>> {
            override fun onResponse(call: Call<List<ClientDTO>>, response: Response<List<ClientDTO>>) {
                if (response.isSuccessful) {
                    val clientList: List<ClientDTO>? = response.body()
                    if (clientList != null) {
                        // Clear existing views in the linear layout
                        linearLayout.removeAllViews()

                        // Iterate through the clientList and create views dynamically
                        for (client in clientList) {
                            val clientView = createClientView(client)
                            linearLayout.addView(clientView)
                        }
                    }
                } else {
                    Log.e("ListeClientsActivity", "Failed to get clients: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ClientDTO>>, t: Throwable) {
                Log.e("ListeClientsActivity", "Error fetching clients: ${t.message}", t)
            }
        })
    }
    @SuppressLint("InflateParams")
    private fun createClientView(client: ClientDTO): View {
        // Inflate the view from your layout XML
        val inflater = LayoutInflater.from(this)
        val clientView = inflater.inflate(R.layout.item_client_cardview, null) // Replace with the actual layout resource

        // Find TextViews and Button in your inflated view
        val nameTextView = clientView.findViewById<TextView>(R.id.clientName)
        val detailsTextView = clientView.findViewById<TextView>(R.id.clientDetails)
        val deleteButton = clientView.findViewById<Button>(R.id.deleteButton)

        // Set data to TextViews
        nameTextView.text = client.name
        detailsTextView.text = client.email

        // Set click listener for delete button
        deleteButton.setOnClickListener {
            deleteClientById(client.id)
        }

        return clientView
    }
    private fun deleteClientById(userId: Long) {
        val call: Call<String> = microserviceApi.deleteUserById(userId)
        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    fetchAndDisplayClients()
                } else {
                    Log.e("ListeClientsActivity", "Failed to delete client: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ListeClientsActivity", "Failed to delete client: ${t.message}", t)
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