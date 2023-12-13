package com.example.tawsila

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Admin_profile : AppCompatActivity() {
    private lateinit var imgProfile: ImageView
    private lateinit var fullNameEditText: TextView
    private lateinit var emailEditText: TextView

    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_admin)

        // Initialize your ImageView
        val userId: Long = intent.getLongExtra("USER_ID", -1)
        Log.e("userId", "userID: ${userId}")

        imgProfile = findViewById(R.id.profile_image)
        fullNameEditText = findViewById(R.id.full_name)
        emailEditText = findViewById(R.id.email)

        // Call the function to set up userId and BottomNavigationView
        setUpBottomNavigationView()


        // Call the API to get user information based on the user ID
// Replace the placeholder "getUserInfo" with your actual API endpoint
        val getUserInfoCall: Call<UserDTO> = microserviceApi.getUserInfo(userId)
        getUserInfoCall.enqueue(object : Callback<UserDTO> {
            override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                if (response.isSuccessful) {
                    val user = response.body()

                    // Fill the input fields with user information
                    fullNameEditText.setText(user?.name)
                    emailEditText.setText(user?.email)

                    // Check and handle profile image
                    val profileImage = user?.profileImage
                    if (profileImage != null) {
                        when (profileImage) {
                            is ByteArray -> {
                                // Handle byte array (e.g., convert it to a Bitmap and set it to an ImageView)
                                val bitmap = BitmapFactory.decodeByteArray(profileImage, 0, profileImage.size)
                                imgProfile.setImageBitmap(bitmap)
                            }
                            is String -> {
                                // Handle the case where the server sends the image as a base64-encoded string
                                // You need to decode the base64 string and set it to an ImageView
                                val decodedBytes = Base64.decode(profileImage as String, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                                imgProfile.setImageBitmap(bitmap)
                            }
                            else -> {
                                // Handle other cases if needed
                            }
                        }
                    }
                } else {
                    Log.e("YourTag", "Failed to retrieve user information. Code: ${response.code()}")
                    Log.e("YourTag", "Response: ${response.raw()}")
                }
            }

            override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                Log.e("YourTag", "Network error: ${t.message}", t)
            }
        })



        val goUpdate = findViewById<Button>(R.id.goUpdate)
        goUpdate.setOnClickListener {
            val intent = Intent(this, driver_profile::class.java)
            intent.putExtra("USER_ID", userId)

            startActivity(intent)
        }
        val logoutButton = findViewById<Button>(R.id.logout)
        // Set an OnClickListener for the logout button
        logoutButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // Close the current activity to prevent going back to it from the login screen
        }
    }
    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.Acceuil
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
                    val intent = Intent(this, Admin_profile::class.java)
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