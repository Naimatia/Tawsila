package com.example.tawsila

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddMaps : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val textInputLayoutArrivee: TextInputLayout = findViewById(R.id.textInputLayoutArrivee)
        val editTextArrivee: TextInputEditText = findViewById(R.id.editTextArrivee)

// Set an OnClickListener on the end icon
        textInputLayoutArrivee.setEndIconOnClickListener {
            // Handle the click event
            // For example, navigate to another page
            val intent = Intent(this, Maps::class.java)
            startActivity(intent)
        }
    }
}