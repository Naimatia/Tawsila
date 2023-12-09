package com.example.tawsila

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CovAdapter(private val covoiturageList: List<Covoiturage>) : RecyclerView.Adapter<CovAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_covoiturage_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val covoiturage = covoiturageList[position]

        holder.depart.text = covoiturage.depart ?: ""
        holder.destination.text = covoiturage.destination ?: ""
        holder.price.text = covoiturage.price?.toString() ?: ""
        holder.place.text = covoiturage.place?.toString() ?: ""
        holder.bagage.text = covoiturage.bagage ?: ""
        holder.date.text = covoiturage.date ?: ""

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
        val depart: TextView = itemView.findViewById(R.id.departTextView)
        val destination: TextView = itemView.findViewById(R.id.destinationTextView)
        val price: TextView = itemView.findViewById(R.id.priceTextView)
        val place: TextView = itemView.findViewById(R.id.placeTextView)
        val bagage: TextView = itemView.findViewById(R.id.bagageTextView)
        val date: TextView = itemView.findViewById(R.id.dateTextView)
        // Ajoutez ici d'autres vues pour les données supplémentaires du covoiturage si nécessaire
    }
}