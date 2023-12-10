package com.example.tawsila

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private val rememberMeKey = "rememberMe"
    private val usernameKey = "username"
    private val passwordKey = "password"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_login)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)
        val usernameLayout = findViewById<TextInputLayout>(R.id.username)
        val passwordLayout = findViewById<TextInputLayout>(R.id.password)

        val usernameEditText = usernameLayout.findViewById<TextInputEditText>(R.id.editTextUsername)
        val passwordEditText = passwordLayout.findViewById<TextInputEditText>(R.id.editTextPassword)
        val rememberMeCheckBox = findViewById<CheckBox>(R.id.checkBoxRememberMe)
        if (sharedPreferences.getBoolean(rememberMeKey, false)) {
            // If Remember Me was checked, populate the username and password fields
            usernameEditText.setText(sharedPreferences.getString(usernameKey, ""))
            passwordEditText.setText(sharedPreferences.getString(passwordKey, ""))
            rememberMeCheckBox.isChecked = true
        }

        val loginButton = findViewById<Button>(R.id.goButton)
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Check if username is empty
            if (username.isEmpty()) {
                usernameLayout.error = "Enter a username"
                return@setOnClickListener
            } else {
                // Clear the error if username is valid
                usernameLayout.error = null
            }

            // Check if password is not empty
            if (password.isEmpty()) {
                passwordLayout.error = "Passwords cannot be empty"
                return@setOnClickListener
            } else {
                // Clear the error if passwords match
                passwordLayout.error = null
            }

            // Create an instance of AuthRequest
            val authRequest = AuthRequest(username, password)

            // Make the API call for authentication
            val loginCall: Call<Map<String, Any>> = microserviceApi.getToken(authRequest)

            // Inside your Login activity
            loginCall.enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                    if (response.isSuccessful) {
                        val data: Map<String, Any>? = response.body()
                        // Log the entire response map
                        Log.d("Login", "Response Map: $data")


                        // Check if the response contains the token and user ID
                        val token = data?.get("token") as? String
                        val userId = (data?.get("userId") as? Number)?.toLong()

                        // Access the role information correctly
                        val roleData = data?.get("role") as? Map<String, Any>
                        val role = roleData?.get("name") as? String
                        Log.d("Login", "Role: $role")

                        if (token != null && userId != null) {
                            // Make the API call to get user information
                            val getUserInfoCall: Call<UserDTO> = microserviceApi.getUserInfo(userId)
                            getUserInfoCall.enqueue(object : Callback<UserDTO> {
                                override fun onResponse(call: Call<UserDTO>, response: Response<UserDTO>) {
                                    if (response.isSuccessful) {
                                        val user = response.body()
                                        if (rememberMeCheckBox.isChecked) {
                                            // Save credentials
                                            Log.d("Login", "Remember Me is checked. Saving credentials.")
                                            with(sharedPreferences.edit()) {
                                                putBoolean(rememberMeKey, true)
                                                putString(usernameKey, username)
                                                putString(passwordKey, password)
                                                apply()
                                            }
                                        } else if (!rememberMeCheckBox.isChecked) {
                                            // Clear credentials
                                            Log.d(
                                                "Login",
                                                "Remember Me is unchecked. Clearing credentials."
                                            )
                                            with(sharedPreferences.edit()) {
                                                remove(rememberMeKey)
                                                remove(usernameKey)
                                                remove(passwordKey)
                                                apply()
                                            }
                                        }
                                        // Check if the profile image is null
                                        if (user?.profileImage == null) {
                                                // If the profile image is null, navigate to profil_image
                                                val intent =
                                                    Intent(this@Login, profil_image::class.java)
                                                intent.putExtra("USER_TOKEN", token)
                                                intent.putExtra("USER_ID", userId)
                                                intent.putExtra("name", role)


                                            startActivity(intent)

                                        } else {
                                            if (role == "DRIVER") {
                                                // If the profile image is not null, navigate to InterfaceActivity
                                                val intent =
                                                    Intent(this@Login, Interface_driver::class.java)
                                                intent.putExtra("USER_TOKEN", token)
                                                intent.putExtra("USER_ID", userId)
                                                startActivity(intent)
                                            } else if (role == "CLIENT"){
                                                // If the profile image is not null, navigate to InterfaceActivity
                                                val intent =
                                                    Intent(this@Login, Interface_client::class.java)
                                                intent.putExtra("USER_TOKEN", token)
                                                intent.putExtra("USER_ID", userId)
                                                startActivity(intent)
                                            } else if (role == "ADMIN"){
                                                // If the profile image is not null, navigate to InterfaceActivity
                                                val intent =
                                                    Intent(this@Login, Interface_admin::class.java)
                                                intent.putExtra("USER_TOKEN", token)
                                                intent.putExtra("USER_ID", userId)
                                                startActivity(intent)
                                            }
                                        }
                                    } else {
                                        // Handle the case where retrieving user information fails
                                        Log.e("Login", "Failed to retrieve user information. Code: ${response.code()}")
                                        Log.e("Login", "Response: ${response.raw()}")
                                    }
                                }

                                override fun onFailure(call: Call<UserDTO>, t: Throwable) {
                                    // Handle network or other errors when getting user information
                                    Log.e("Login", "Network error: ${t.message}", t)
                                }
                            })
                        }  else {
                            // Handle missing token or user ID
                            Log.e("Login", "Token or user ID not found in the response")
                            // Handle other cases as needed
                        }
                    } else {
                        // Handle unsuccessful login
                        val errorBody = response.errorBody()?.string()
                        Log.e("Login", "Login failed: $errorBody")
                        // Check the response code for invalid credentials
                        if (response.code() == 401) {
                            // Unauthorized (invalid username or password)
                            // Show an error message to the user
                            Toast.makeText(this@Login, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    // Handle network or other errors during login
                    Log.e("Login", "Login failed: ${t.message}")
                    t.printStackTrace()
                }
            })

        }


        val goSignupButton = findViewById<Button>(R.id.goSignup)
        goSignupButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}