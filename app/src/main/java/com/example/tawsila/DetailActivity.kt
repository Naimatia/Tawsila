package com.example.tawsila

import ApiParticipation
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private lateinit var apiParticipation: ApiParticipation
    private lateinit var confirmerButton: Button
    private var covoiturage: Covoiturage? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covoiturage_detail)


        apiParticipation = RetrofitClient.getClient(ApiParticipation.BASE_URL)
            .create(ApiParticipation::class.java)

        // Initialize confirmerButton
        confirmerButton = findViewById(R.id.confirmButton)

        confirmerButton.setOnClickListener {
            // Disable the button to prevent multiple clicks during the API call
            confirmerButton.isEnabled = false

            // Handle confirmation directly without showing the dialog
            lifecycleScope.launch {
                postConfirmation()

                // Enable the button after the API call is complete
                confirmerButton.isEnabled = true
            }
        }

        // Retrieve data from intent
        covoiturage = intent.getParcelableExtra("covoiturage")

        // Update UI with covoiturage details
        covoiturage?.let {
            updateUI(it)
        }
    }

    private fun updateUI(covoiturage: Covoiturage) {
        // Update UI with covoiturage details
        val departTextView: TextView = findViewById(R.id.detailDepartEditText)
        departTextView.text = covoiturage.depart ?: ""

        val destinationTextView: TextView = findViewById(R.id.detailDestinationEditText)
        destinationTextView.text = covoiturage.destination ?: ""

        val DateTextView: TextView = findViewById(R.id.detailDateEditText)
        DateTextView.text = covoiturage.date ?: ""

        val priceTextView: TextView = findViewById(R.id.detailPriceEditText)
        priceTextView.text = covoiturage.price.toString() ?: ""

        val phoneTextView: TextView = findViewById(R.id.detailPhoneEditText)
        phoneTextView.text = covoiturage.phone.toString() ?: ""
    }

    private fun postConfirmation() {
        // Create a ParticipationRequest object
        val  userId = intent.getLongExtra("USER_ID", -1)
        Log.e("id", "user id: ${userId}")
        val participationRequest = ParticipationRequest(
            participationID = "aziz65633a5d96be0d766320782",
            clientID = userId,
            carpoolingID = covoiturage?.id ?: 0,
            etat = 1
        )

        // Log the API details
        val jsonBody = Gson().toJson(participationRequest)
        val requestDetails = """
        API Method: POST
        API Body: $jsonBody
    """.trimIndent()
        Log.d("Confirmation", "API Details:\n$requestDetails")

        // Construct dynamic URL
        val baseUrl = "http://192.168.56.1:3002/participation"
        val dynamicUrl = "${baseUrl}?clientID=${participationRequest.clientID}&carpoolingID=${participationRequest.carpoolingID}&etat=${participationRequest.etat}"
        Log.d("api", "API :\n$dynamicUrl")

        // Make the API request with a dynamic URL
        val call = apiParticipation.postReservation(dynamicUrl)
        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,

                response: retrofit2.Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    // Successful confirmation
                    Log.d("Confirmation", "Confirmation successful.")
                    showToast("Confirmation successful")
                } else {
                    // Handle unsuccessful confirmation
                    handleFailure(response)
                }

                // Enable the button after the API call is complete
                confirmerButton.isEnabled = true
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, t: Throwable) {
                // Handle failure
                handleException(t)

                // Enable the button after the API call is complete
                confirmerButton.isEnabled = true
            }
        })
    }


    private fun showToast(message: String) {
        // Show Toast on the main thread
        runOnUiThread {
            Toast.makeText(
                this@DetailActivity,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleFailure(response: Response<ResponseBody>) {
        // Handle unsuccessful confirmation
        Log.e("Confirmation", "Confirmation failed. HTTP status code: ${response.code()}")

        // Show Toast on the main thread
        showToast("Confirmation failed. Check logs for details")
    }

    private fun handleHttpException(e: HttpException) {
        // Handle HTTP-related exceptions
        Log.e("Confirmation", "Confirmation failed", e)

        // Log the error details
        val errorMessage = e.message ?: "Unknown error"
        Log.e("Confirmation", "Error message: $errorMessage")

        // Show Toast on the main thread
        showToast("Confirmation failed. Check logs for details")
    }

    private fun handleException(e: Throwable) {
        // Handle other exceptions
        Log.e("Confirmation", "Confirmation failed", e)

        // Log the error details
        val errorMessage = e.message ?: "Unknown error"
        Log.e("Confirmation", "Error message: $errorMessage")

        // Show Toast on the main thread
        showToast("Confirmation failed. Check logs for details")
    }
}
