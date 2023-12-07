package com.example.tawsila

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private var covoiturage: Covoiturage? = null
    private lateinit var microServiceApi: MicroServiceApi // Declare the Retrofit service
    private lateinit var apiParticipation: ApiParticipation // Declare the Retrofit service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covoiturage_detail)

        // Initialize your services
        microServiceApi =
            RetrofitClient.getClient(MicroServiceApi.BASE_URL).create(MicroServiceApi::class.java)
        apiParticipation =
            RetrofitClient.getClient(ApiParticipation.BASE_URL).create(ApiParticipation::class.java)

        val confirmerButton: Button = findViewById(R.id.confirmButton)
        confirmerButton.setOnClickListener {
            showConfirmationDialog()
        }

        // Retrieve data from intent
        covoiturage = intent.getParcelableExtra("covoiturage")

        // Update UI with covoiturage details
        if (covoiturage != null) {
            val departTextView: TextView = findViewById(R.id.detailDepartEditText)
            departTextView.text = covoiturage!!.depart ?: ""

            val destinationTextView: TextView = findViewById(R.id.detailDestinationEditText)
            destinationTextView.text = covoiturage!!.destination ?: ""

            val DateTextView: TextView = findViewById(R.id.detailDateEditText)
            DateTextView.text = covoiturage!!.date ?: ""

            val priceTextView: TextView = findViewById(R.id.detailPriceEditText)
            priceTextView.text = covoiturage!!.price.toString() ?: ""
            val phoneTextView: TextView = findViewById(R.id.detailPhoneEditText)
            phoneTextView.text = covoiturage!!.phone.toString() ?: ""
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to confirm?")
            .setPositiveButton("Yes") { _, _ ->
                // Handle confirmation
                postConfirmation()
            }
            .setNegativeButton("No", null)
            .show()
    }
    private fun postConfirmation() {
        // Fetch user ID from authentication (Replace with your authentication logic)
        val clientId: Int = 123

        // Fetch carpooling ID from covoiturage
        val carpoolingId: Long = covoiturage?.id ?: 0

        // Set the "etat" value to 1
        val etat: Int = 1

        // Replace with your actual logic to generate a participation ID
        val participationId: String = "123"

        // Create a ParticipationRequest object
        val participationRequest = ParticipationRequest(
            participationID = participationId,
            clientID = clientId,
            carpoolingID = carpoolingId.toInt(), // Convert to Int if necessary
            etat = etat
        )

        // Log the API endpoint URL
        val requestUrl = apiParticipation.postParticipation(participationRequest).request().url.toString()
        Log.d("Confirmation", "API URL: $requestUrl")

        // Make the API call
        val call: Call<ResponseBody> = apiParticipation.postParticipation(participationRequest)
        Log.d("Confirmation", "API URL: $call")

        // Enqueue the call
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Handle API response
                if (response.isSuccessful) {
                    // Successful confirmation
                    val responseBody = response.body()?.string() ?: ""
                    Log.d("Confirmation", "Confirmation successful. Response: $responseBody")
                    Toast.makeText(
                        this@DetailActivity,
                        "Confirmation successful",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Failed confirmation
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("Confirmation", "Confirmation failed. Error: $errorBody")
                    Toast.makeText(this@DetailActivity, "Confirmation failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle API call failure
                Log.e("Confirmation", "Confirmation failed", t)
                Toast.makeText(this@DetailActivity, "Confirmation failed", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

}
