package com.example.tawsila

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

class profil_image : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var selectedImageUri: Uri

    // Retrofit instance
    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)

    // Declare the launcher for the image picker activity result
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                // Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
                selectedImageUri = fileUri

                // Use the Uri object instead of File to avoid storage permissions
                imgProfile.setImageURI(fileUri)
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profil_image)

        // Initialize your ImageView
        imgProfile = findViewById(R.id.profile_image)

        // Set onClickListener for the FloatingActionButton
        val uploadButton: FloatingActionButton = findViewById(R.id.floatingActionButton4)
        uploadButton.setOnClickListener {
            // Launch the image picker
            ImagePicker.with(this)
                .compress(1024)         // Final image size will be less than 1 MB (Optional)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }

        }

        // Set onClickListener for the Save Button
        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            // Retrieve user ID from the extras
            val userId: Long = intent.getLongExtra("USER_ID", -1)
            val role: String? = intent.getStringExtra("name")

            Log.e("YourTag", "User ID: $userId")
            // Check if an image is selected
            if (imgProfile.drawable != null) {
                // Call the API to upload the image
                uploadImage(userId, selectedImageUri)
                if (role == "DRIVER") {
                    // If the profile image is not null, navigate to InterfaceActivity
                    val intent =
                        Intent(this, Interface_driver::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                } else if (role == "CLIENT"){
                    // If the profile image is not null, navigate to InterfaceActivity
                    val intent =
                        Intent(this, Interface_client::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                } else if (role == "ADMIN"){
                    // If the profile image is not null, navigate to InterfaceActivity
                    val intent =
                        Intent(this, Interface_admin::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show()
            }
        }

        val ignorer = findViewById<Button>(R.id.Ignorer)
        ignorer.setOnClickListener {
            val role: String? = intent.getStringExtra("name")
            val userId: Long = intent.getLongExtra("USER_ID", -1)
            if (role == "DRIVER") {
                // If the profile image is not null, navigate to InterfaceActivity
                val intent =
                    Intent(this, Interface_driver::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            } else if (role == "CLIENT"){
                // If the profile image is not null, navigate to InterfaceActivity
                val intent =
                    Intent(this, Interface_client::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            } else if (role == "ADMIN"){
                // If the profile image is not null, navigate to InterfaceActivity
                val intent =
                    Intent(this, Interface_admin::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
            }
        }

    }

    private fun uploadImage(userId: Long, fileUri: Uri) {
        // Check if selectedImageUri has been initialized
        if (!::selectedImageUri.isInitialized) {
            Log.e("profil_image", "Please select an image first")
            return
        }

        // Utilisez l'InputStream pour obtenir le contenu du fichier
        val contentResolver = this.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(fileUri)

        // Vérifiez si l'InputStream est non nul
        if (inputStream != null) {
            // Utilisez l'InputStream pour obtenir le contenu du fichier
            val byteArray = inputStream.readBytes()

            // Créez une demande de fichier avec le contenu lu
            val requestFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), byteArray)
            val body: MultipartBody.Part = MultipartBody.Part.createFormData("file", "image_file", requestFile)

            // Appelez l'API pour télécharger l'image
            val call: Call<String> = microserviceApi.setImageById(userId, body)

            call.enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        // Image uploaded successfully
                        Log.i("profil_image", "Image uploaded successfully")
                    } else {
                        // Handle unsuccessful response
                        Log.e("profil_image", "Unsuccessful response: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // Handle network or other errors during image upload
                    Log.e("profil_image", "Error: ${t.message}")
                }
            })

            // Fermez l'inputStream après utilisation
            inputStream.close()
        } else {
            Log.e("profil_image", "Invalid InputStream for selected image")
        }
    }



    companion object {
        private const val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 123
    }

}
