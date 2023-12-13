import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tawsila.Covoiturage
import com.example.tawsila.MicroServiceApi
import com.example.tawsila.ParticiDetailsadmin
import com.example.tawsila.R
import com.example.tawsila.Reservation
import com.example.tawsila.ReservationListDriver
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.abs
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class CovoiturageDriverAdapter(
    private var originalCovoituragesList: List<Covoiturage>,
    private val context: Context
) : RecyclerView.Adapter<CovoiturageDriverAdapter.ViewHolder>() {

    private val retrofit = Retrofit.Builder()
        .baseUrl(MicroServiceApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        )
        .build()

    private val microserviceApi = retrofit.create(MicroServiceApi::class.java)

    private var filteredCovoituragesList: List<Covoiturage> = originalCovoituragesList

    interface OnItemClickListener {
        fun onItemClick(covoiturage: Covoiturage)
        fun onAcceptRequest(covoiturage: Covoiturage)
        fun onDeleteCovoiturage(covoiturage: Covoiturage)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.item_covoiturage_driver_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val covoiturage = filteredCovoituragesList[position]

        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(covoiturage)
        }

        holder.bind(covoiturage)
    }




    override fun getItemCount(): Int {
        return filteredCovoituragesList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val depart: TextView = itemView.findViewById(R.id.departTextView)
        val destination: TextView = itemView.findViewById(R.id.destinationTextView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val heureDepart: TextView = itemView.findViewById(R.id.heureDepart)
        val heureArrive: TextView = itemView.findViewById(R.id.heureArrivee)
        val timeDifference: TextView = itemView.findViewById(R.id.TimeDifference)
        val deleteButton: TextView = itemView.findViewById(R.id.deleteButton)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                 //   onItemClickListener?.onDeleteCovoiturage(originalCovoituragesList[position])
                }
            }
        }

        fun bind(covoiturage: Covoiturage) {
            depart.text = covoiturage.depart ?: ""
            destination.text = covoiturage.destination ?: ""
            price.text = covoiturage.price?.toString() ?: ""
            heureDepart.text = covoiturage.heureDepart ?: ""
            heureArrive.text = covoiturage.heureArrive ?: ""

            // Calculate and display time difference
            val timeDiff = calculateTimeDifference(covoiturage)
            timeDifference.text = timeDiff
            deleteButton.setOnClickListener {
                // Perform the action when the "Accept" button is clicked
                onDeleteCovoiturage(covoiturage)
                Log.e("handleAcceptButtonClick", "handleAcceptButtonClick oku")

            }
        }

        private fun calculateTimeDifference(covoiturage: Covoiturage): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            if (covoiturage.heureDepart != null && covoiturage.heureArrive != null) {
                try {
                    val departTime = dateFormat.parse(covoiturage.heureDepart)
                    val arriveTime = dateFormat.parse(covoiturage.heureArrive)

                    val diff = abs(arriveTime.time - departTime.time)
                    val hours = diff / (60 * 60 * 1000)
                    val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)

                    return String.format("%02d:%02d", hours, minutes)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            return ""
        }
    }

    fun setFilteredData(newData: List<Covoiturage>) {
        filteredCovoituragesList = newData
        notifyDataSetChanged()
    }

    fun updateOriginalData(newData: List<Covoiturage>) {
        originalCovoituragesList = newData
        setFilteredData(newData)
    }


    fun onDeleteCovoiturage(covoiturage: Covoiturage) {
        val baseUrl = "driver/covoiturages/${covoiturage.id}"

        Log.d("DeleteCovoiturage", "Deleting covoiturage: ${covoiturage.id}")
        val call: Call<ResponseBody> = microserviceApi.deleteCovoiturage(baseUrl)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Handle successful deletion
                    // You may want to update the adapter data and refresh the UI
                    updateOriginalData(originalCovoituragesList.filterNot { it.id == covoiturage.id })
                    Log.d("DeleteCovoiturage", "Deleted covoiturage successfully")
                } else {
                    // Handle failed deletion
                    Log.e("DeleteCovoiturage", "Failed to delete covoiturage: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle failure
                Log.e("DeleteCovoiturage", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }


    }



