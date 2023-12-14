package com.example.tawsila

import ApiParticipation
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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


class ParticiDetailsadmin : AppCompatActivity() {
    private val microServiceApi = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(MicroServiceApi::class.java)

    private lateinit var participantAdapter: ParticipantAdapter
    private lateinit var participantRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_cov)

        val covoiturageId = intent.getLongExtra("COVOITURAGE_ID", -1)

        participantRecyclerView = findViewById(R.id.participantsRecyclerView)
        participantRecyclerView.layoutManager = LinearLayoutManager(this)
        participantAdapter = ParticipantAdapter(emptyList())
        participantRecyclerView.adapter = participantAdapter

        if (covoiturageId != -1L) {
            fetchParticipantsForCovoiturage(covoiturageId)
        }
        setUpBottomNavigationView();
    }

    private fun fetchParticipantsForCovoiturage(covoiturageId: Long) {
        val participationApi = Retrofit.Builder()
            .baseUrl(ApiParticipation.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiParticipation::class.java)

        val call: Call<List<ParticipationRequest>> = participationApi.getParticipantsForCovoiturage(covoiturageId)
        call.enqueue(object : Callback<List<ParticipationRequest>> {
            override fun onResponse(call: Call<List<ParticipationRequest>>, response: Response<List<ParticipationRequest>>) {
                if (response.isSuccessful) {
                    val participantsList: List<ParticipationRequest>? = response.body()
                    participantsList?.let {
                        fetchParticipantsDetails(it)
                    }
                } else {
                    Log.e("DetailsActivity", "Échec de récupération des participants: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<ParticipationRequest>>, t: Throwable) {
                Log.e("DetailsActivity", "Erreur: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun fetchParticipantsDetails(participantsList: List<ParticipationRequest>) {
        val participantDetailsList = mutableListOf<UserDTO>()

        val totalParticipants = participantsList.size
        var participantsProcessed = 0

        for (participant in participantsList) {
            val userId = participant.clientID
            Log.d("DetailsActivity", "UserID : $userId") // Vérifier si l'ID de l'utilisateur est correct

            val userCall = microServiceApi.getUserInfo(userId)
            userCall.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        val userDTO: UserDTO? = response.body()
                        userDTO?.let {
                            participantDetailsList.add(it)
                            Log.d("DetailsActivity", "Nom de l'utilisateur : ${it.name}")
                        }
                    } else {
                        Log.e("DetailsActivity", "Échec de récupération des détails de l'utilisateur: ${response.code()}")
                    }

                    participantsProcessed++
                    if (participantsProcessed == totalParticipants) {
                        showParticipants(participantDetailsList)
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    Log.e("DetailsActivity", "Erreur lors de la récupération des détails de l'utilisateur: ${t.message}")
                    t.printStackTrace()
                    participantsProcessed++
                    if (participantsProcessed == totalParticipants) {
                        showParticipants(participantDetailsList)
                    }
                }
            })
        }
    }

    private fun showParticipants(participantsList: List<UserDTO>) {
        participantAdapter = ParticipantAdapter(participantsList)
        participantRecyclerView.adapter = participantAdapter
    }
    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.carpooling
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