package com.example.tawsila

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tawsila.Covoiturage
import com.example.tawsila.R

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_covoiturage_detail)

        // Retrieve data from intent
        val covoiturage: Covoiturage? = intent.getParcelableExtra("covoiturage")

        // Update UI with covoiturage details
        if (covoiturage != null) {
            val departTextView: TextView = findViewById(R.id.detailDepartEditText)
            departTextView.text = covoiturage.depart ?: ""

            val destinationTextView: TextView = findViewById(R.id.detailDestinationEditText)
            destinationTextView.text = covoiturage.destination ?: ""

            val DateTextView: TextView = findViewById(R.id.detailDateEditText)
            DateTextView.text = covoiturage.date ?: ""  // Fix: Use phoneTextView here

            val priceTextView: TextView = findViewById(R.id.detailPriceEditText)
            priceTextView.text = covoiturage.price.toString() ?: ""
            val phoneTextView: TextView = findViewById(R.id.detailPhoneEditText)
            phoneTextView.text = covoiturage.phone.toString() ?: ""
        }
    }
}
