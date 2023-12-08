package com.example.tawsila

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ParticipantAdapter(private val participantsList: List<UserDTO>) : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val participant = participantsList[position]

        holder.participantName.text = participant.name
        holder.participantEmail.text = participant.email
        // Ajoutez d'autres attributs du participant à afficher si nécessaire
    }

    override fun getItemCount(): Int {
        return participantsList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantName: TextView = itemView.findViewById(R.id.participantNameTextView)
        val participantEmail: TextView = itemView.findViewById(R.id.participantEmailTextView)
        // Ajoutez d'autres vues pour les attributs du participant si nécessaire
    }
}