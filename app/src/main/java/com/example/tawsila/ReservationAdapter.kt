package com.example.tawsila

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tawsila.Reservation
import com.example.tawsila.R
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReservationAdapter(
    private var originalReservationsList: List<Reservation>,
    private val context: Context
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    private var filteredReservationsList: List<Reservation> = originalReservationsList
    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(reservation: Reservation)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_reservation_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = filteredReservationsList[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(reservation)
        }

        holder.bind(reservation)
    }

    override fun getItemCount(): Int = filteredReservationsList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participationID: TextView = itemView.findViewById(R.id.participationIDTextView)
        // val clientID: TextView = itemView.findViewById(R.id.clientIDTextView)
        //val carpoolingID: TextView = itemView.findViewById(R.id.carpoolingIDTextView)
        val etat: TextView = itemView.findViewById(R.id.etatTextView)
        val departureTextView: TextView = itemView.findViewById(R.id.departTextView)
        val destinationTextView: TextView = itemView.findViewById(R.id.destinationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)


        fun bind(reservation: Reservation) {
            participationID.text = reservation.participationID
            //clientID.text = reservation.clientID.toString()
            //carpoolingID.text = reservation.carpoolingID.toString()

            // Conditionally set the text, text color, and text alignment based on the value of etat
            val etatValue = reservation.etat
            etat.text = if (etatValue == 1) {
                "En attente"
            } else {
                "Accepted"
            }

            // Set text color and alignment
            if (etatValue == 1) {
                etat.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorGreen))
                etat.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            } else {
                etat.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorBlue))
                etat.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            }

            // Assume you have a method to fetch covoiturage details by ID
            fetchCovoiturageDetails(reservation.carpoolingID)
        }

        private fun fetchCovoiturageDetails(carpoolingID: Int) {
            // Use the Retrofit or any other networking library to make the call
            val baseUrl = "http://192.168.56.1:8080/driver/covoiturages/$carpoolingID"

            Log.e("URL", "{$baseUrl}")
            val retrofit = Retrofit.Builder()
                .baseUrl(MicroServiceApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build()
                )
                .build()

            val microserviceApi = retrofit.create(MicroServiceApi::class.java)

            val call: Call<Covoiturage> = microserviceApi.getCovoiturageDetails(baseUrl)

            call.enqueue(object : Callback<Covoiturage> {
                override fun onResponse(call: Call<Covoiturage>, response: Response<Covoiturage>) {
                    if (response.isSuccessful) {
                        val covoiturage: Covoiturage? = response.body()
                        if (covoiturage != null) {
                            // Update the TextViews with departure and destination
                            departureTextView.text = "Depart: ${covoiturage.depart}"
                            destinationTextView.text = "Destination: ${covoiturage.destination}"
                            dateTextView.text = " ${covoiturage.date}"

                        } else {
                            Log.e("ViewHolder", "Response body is null")
                        }
                    } else {
                        Log.e("ViewHolder", "Failed to fetch covoiturage details: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<Covoiturage>, t: Throwable) {
                    Log.e("ViewHolder", "Error: ${t.message}")
                    t.printStackTrace()
                }
            })
        }
    }


    fun setFilteredData(newData: List<Reservation>) {
        filteredReservationsList = newData
        notifyDataSetChanged()
    }

    fun updateOriginalData(newData: List<Reservation>) {
        originalReservationsList = newData
        setFilteredData(newData) // Optionally update filtered data as well
    }
}
