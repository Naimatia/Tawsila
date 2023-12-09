package com.example.tawsila

import ApiParticipation
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URL

class DetailActivity : AppCompatActivity() {
    private lateinit var apiParticipation: ApiParticipation
    private lateinit var confirmerButton: Button
    private var covoiturage: Covoiturage? = null
    private lateinit var map: MapView


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
            lifecycleScope.launch {
                updateUI(it)
            }
        }


        // Chargez la configuration OSMdroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        // Récupérez la vue de la carte
        map = findViewById(R.id.mapView)

        // Définissez la source de tuiles
        map.setTileSource(TileSourceFactory.MAPNIK)

        // Activez les contrôles multi-touch
        map.setMultiTouchControls(true)
    }

    private suspend fun updateUI(covoiturage: Covoiturage) {
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

        val sourceCoordinates = withContext(Dispatchers.IO) {
            getCoordinates(covoiturage?.depart ?: "")
        }
        val destinationCoordinates = withContext(Dispatchers.IO) {
            getCoordinates(covoiturage?.destination ?: "")
        }

        val sourceMarker = Marker(map)
        sourceMarker.position = sourceCoordinates
        sourceMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(sourceMarker)

        val destinationMarker = Marker(map)
        destinationMarker.position = destinationCoordinates
        destinationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(destinationMarker)


    }


    private fun postConfirmation() {
        // Create a ParticipationRequest object
        val  userId = intent.getLongExtra("USER_ID", -1)
        Log.e("id", "user id: $userId")
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
        val baseUrl = "http://169.254.142.86:3002/participation"
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

    suspend fun getCoordinates(country: String): GeoPoint {
        val url = "https://nominatim.openstreetmap.org/search?country=$country&format=json"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        val gson = Gson()
        val type = object : TypeToken<List<Map<String, Any>>>() {}.type
        val list: List<Map<String, Any>> = gson.fromJson(responseBody, type)

        val lat = list[0]["lat"] as Double
        val lon = list[0]["lon"] as Double

        return GeoPoint(lat, lon)
    }



}
