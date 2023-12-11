package com.example.tawsila

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class CovAdapter(private val covoiturageList: List<Covoiturage>) : RecyclerView.Adapter<CovAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_covoiturage_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val covoiturage = covoiturageList[position]
//
        holder.depart.text = covoiturage.depart
        holder.destination.text = covoiturage.destination
        holder.price.text = covoiturage.price.toString()
        holder.heureDepart.text = covoiturage.heureDepart
        holder.heureArrive.text = covoiturage.heureArrive

        val timeDifference = calculateTimeDifference(covoiturage)
        holder.timeDifference.text = timeDifference

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ParticiDetailsadmin::class.java)
            intent.putExtra("COVOITURAGE_ID", covoiturage.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return covoiturageList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val heureDepart: TextView = itemView.findViewById(R.id.heureDepart)
        val heureArrive: TextView = itemView.findViewById(R.id.heureArrivee)
        val depart: TextView = itemView.findViewById(R.id.departTextView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val destination: TextView = itemView.findViewById(R.id.destinationTextView)
        val timeDifference: TextView = itemView.findViewById(R.id.TimeDifference)
    }

    private fun calculateTimeDifference(covoiturage: Covoiturage): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val departTime = dateFormat.parse(covoiturage.heureDepart)
            val arriveTime = dateFormat.parse(covoiturage.heureArrive)

            val diff = Math.abs(arriveTime.time - departTime.time)
            val hours = diff / (60 * 60 * 1000)
            val minutes = (diff % (60 * 60 * 1000)) / (60 * 1000)

            return String.format("%02d:%02d", hours, minutes)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}