package com.example.tawsila

import ApiParticipation
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tawsila.MicroServiceApi.Companion.BASE_URLF
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.HttpException
import retrofit2.Response
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import retrofit2.Call
import retrofit2.Callback
import java.io.IOException
import java.net.URL
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class DetailActivity : AppCompatActivity() {
    private lateinit var apiParticipation: ApiParticipation
    private lateinit var confirmerButton: Button
    private var covoiturage: Covoiturage? = null
    private lateinit var map: MapView

    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
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

            val iconBack: ImageView = findViewById(R.id.iconBack)

            // Add a click listener to the iconBack ImageView
            iconBack.setOnClickListener {
                val userId = intent.getLongExtra("USER_ID", -1)
                val intent = Intent().apply {
                    putExtra("USER_ID", userId)
                }
                setResult(RESULT_OK, intent)
                finish()
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
        // Display the route on the map
        displayRoute()
    }

    private suspend fun updateUI(covoiturage: Covoiturage) {
        // Update UI with covoiturage details
        val departTextView: TextView = findViewById(R.id.departTextView)
        val constantDepart = "Depart: "
        departTextView.text = "$constantDepart${covoiturage.depart ?: ""}"

        val destinationTextView: TextView = findViewById(R.id.destinationTextView)
        val constantDes = "Destination: "

        destinationTextView.text = "$constantDes${covoiturage.destination ?: ""}"

            // Assuming covoiturage.driver is the user ID
            val userId = covoiturage.driver
            if (userId != null) {
                // Fetch user by ID and update NameTextView with user's name and image
                val user = fetchUserById(userId)
                val nameTextView: TextView = findViewById(R.id.driverName)
                nameTextView.text = user?.name ?: ""

                // Load and display the user's image
                val profileImageView: ImageView = findViewById(R.id.profile_image)
                val profileImage = user?.profileImage
                if (profileImage != null) {
                    when (profileImage) {
                        is ByteArray -> {
                            // Handle byte array (e.g., convert it to a Bitmap and set it to an ImageView)
                            val bitmap =
                                BitmapFactory.decodeByteArray(profileImage, 0, profileImage.size)
                            profileImageView.setImageBitmap(bitmap)
                        }

                        is String -> {
                            // Handle the case where the server sends the image as a base64-encoded string
                            // You need to decode the base64 string and set it to an ImageView
                            val decodedBytes = Base64.decode(profileImage as String, Base64.DEFAULT)
                            val bitmap =
                                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            profileImageView.setImageBitmap(bitmap)
                        }

                        else -> {
                            // Handle other cases if needed
                        }
                    }
                }

            }
        }


    private suspend fun fetchUserById(userId: Long): UserDTO? {
        return suspendCoroutine { continuation ->
            val call: Call<UserDTO> = microserviceApi.getUserInfo(userId)

            call.enqueue(object : Callback<UserDTO> {
                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                    if (response.isSuccessful) {
                        val userDTO = response.body()
                        continuation.resume(userDTO)
                    } else {
                        // Handle unsuccessful response
                        continuation.resume(null)
                    }
                }

                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                    // Handle failure
                    continuation.resume(null)
                }
            })
        }
    }



    private fun postConfirmation() {
        // Create a ParticipationRequest object
        val  userId = intent.getLongExtra("USER_ID", -1)
        Log.e("id", "user id: $userId")
        val participationRequest = ParticipationRequest(
           participationID = "2",
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
        val baseUrl = "${BASE_URLF}/participation"
        val dynamicUrl = "${baseUrl}?clientID=${participationRequest.clientID}&carpoolingID=${participationRequest.carpoolingID}&etat=${participationRequest.etat}"
        Log.d("api", "API :\n$dynamicUrl")

        // Make the API request with a dynamic URL
        val call = apiParticipation.postReservation(dynamicUrl)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,

                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    // Successful confirmation
                    Log.d("Confirmation", "Confirmation successful.")
                    showToast("Confirmation successful")
                    // Navigate back to the home page
                    val intent = Intent(this@DetailActivity, ListeReservationActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle unsuccessful confirmation
                    handleFailure(response)
                }

                // Enable the button after the API call is complete
                confirmerButton.isEnabled = true
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
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

    private fun displayRoute() {
        lifecycleScope.launch {
            try {
                // Get route coordinates from the GraphHopper API
                val routeCoordinates: List<GeoPoint> = getRouteCoordinatesAsync(
                    covoiturage?.depart ?: "", // Replace with the actual source location
                    covoiturage?.destination ?: "" // Replace with the actual destination location
                )
                Log.e("displayRoute", "Route coordinates $routeCoordinates")


                if (routeCoordinates.isNotEmpty()) {
                    // Create Polyline with the route
                    val polyline = Polyline()
                    polyline.color = Color.BLUE
                    polyline.width = 15f

                    for (point in routeCoordinates) {
                        polyline.addPoint(point)
                    }

                    // Add the Polyline to the map
                    map.overlays.add(polyline)

                    // Add markers for source and destination
                    addMarker(routeCoordinates.first(), "Departure", "Start Point")
                    addMarker(routeCoordinates.last(), "Destination", "End Point")

                    // Set map center and zoom level to fit the route
                    val boundingBox = BoundingBox.fromGeoPoints(routeCoordinates)
                    map.zoomToBoundingBox(boundingBox, true)

                } else {
                    // Handle case where route coordinates are not available
                    Log.e("displayRoute", "Route coordinates are empty or not available.")
                    showToast("Failed to retrieve route coordinates.")
                }
            } catch (e: IllegalArgumentException) {
                // Handle the case where source or destination coordinates are invalid
                Log.e("displayRoute", "Invalid source or destination coordinates.", e)
                showToast("Invalid source or destination coordinates.")
            } catch (e: Exception) {
                // Handle other exceptions
                Log.e("displayRoute", "Error in displaying route.", e)
                showToast("Error displaying route.")
            }
        }
    }


    private suspend fun getRouteCoordinatesAsync(source: String, destination: String): List<GeoPoint> {
        return withContext(Dispatchers.IO) {
            try {
                val sourcePoint = getCoordinatesAsync(source)
                val destinationPoint = getCoordinatesAsync(destination)
                Log.e("displayRoute", " $sourcePoint")
                Log.e("displayRoute", " $destinationPoint")

                if (sourcePoint.latitude == 0.0 && sourcePoint.longitude == 0.0) {
                    throw IllegalArgumentException("Invalid source coordinates")
                }

                if (destinationPoint.latitude == 0.0 && destinationPoint.longitude == 0.0) {
                    throw IllegalArgumentException("Invalid destination coordinates")
                }

                val graphHopperApiKey = "ac1fd54c-3c79-4745-97e5-49778336f3ae"
                val url = "https://graphhopper.com/api/1/route?" +
                        "point=${sourcePoint.latitude},${sourcePoint.longitude}&" +
                        "point=${destinationPoint.latitude},${destinationPoint.longitude}&" +
                        "vehicle=car&locale=en&key=$graphHopperApiKey"

                Log.d("RouteURL", "API Request URL: $url")

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code ${response.code}")
                }
                val responseBody = response.body?.string()
                Log.d("GraphHopperResponse", "Response Body: $responseBody")

                    if (!responseBody.isNullOrBlank()) {
                        val gson = Gson()
                        val routeResponse = gson.fromJson(responseBody, GraphHopperRouteResponse::class.java)
                        val encodedPolyline = routeResponse.paths?.get(0)?.points?.asString ?: ""

                        Log.d("GraphHopperResponse", "Route Response: $routeResponse")

                        // Decode the Polyline using Google Maps Android API
                        val decodedLatLngs: List<LatLng> = PolyUtil.decode(encodedPolyline)

                        // Convert decoded LatLngs to GeoPoints
                        val geoPointsList = decodedLatLngs.map { latLng ->
                            GeoPoint(latLng.latitude, latLng.longitude)
                        }

                        Log.d("GeoPoints", "GeoPoints: $geoPointsList")

                        geoPointsList

                } else {
                    throw IOException("Empty response body")
                }
            } catch (e: Exception) {
                Log.e("getRoute", "Error retrieving route coordinates", e)
                emptyList()  // Return an empty list or handle the error as appropriate
            }
        }
    }



    private suspend fun getCoordinatesAsync(address: String): GeoPoint {
        return withContext(Dispatchers.IO) {
            try {
                val encodedAddress = Uri.encode(address)
                val url = URL("https://nominatim.openstreetmap.org/search?q=$encodedAddress&format=json&limit=1")

                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                val responseBody = response.body?.string()

                if (responseBody != null) {
                    Log.d("GraphHopperResponse", "Response Body: $responseBody")
                    val jsonArray = JSONArray(responseBody)
                    if (jsonArray.length() > 0) {
                        val jsonObject = jsonArray.getJSONObject(0)
                        val lat = jsonObject.getDouble("lat")
                        val lon = jsonObject.getDouble("lon")
                        GeoPoint(lat, lon)
                    } else {
                        Log.e("getCoordinatesAsync", "No location found for address: $address")
                        GeoPoint(0.0, 0.0) // Fallback to a default location
                    }
                } else {
                    Log.e("getCoordinatesAsync", "Response body is null for address: $address")
                    GeoPoint(0.0, 0.0) // Fallback to a default location
                }
            } catch (e: Exception) {
                Log.e("getCoordinatesAsync", "Exception getting coordinates for address: $address", e)
                GeoPoint(0.0, 0.0) // Fallback to a default location
            }
        }
    }
    data class GraphHopperRouteResponse(
        val paths: List<GraphHopperPath>?
    )

    data class GraphHopperPath(
        val points: JsonElement?
    )


    data class GraphHopperPoint(
        @SerializedName("lat") val lat: Double,
        @SerializedName("lng") val lng: Double
    )

    private fun addMarker(geoPoint: GeoPoint, title: String, snippet: String) {
        val marker = Marker(map)
        marker.position = geoPoint
        marker.title = title
        marker.snippet = snippet
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(marker)
    }





}



