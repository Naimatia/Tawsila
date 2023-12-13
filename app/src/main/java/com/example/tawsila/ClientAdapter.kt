package com.example.tawsila

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class ClientAdapter( private var clientList: List<ClientDTO>,
                     private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<ClientAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val client = clientList[position]

        holder.nameTextView.text = client.name
        holder.emailTextView.text = client.email

        // Set the click listener for the delete button
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener.onDeleteClick(client.id)
        }
    }

    override fun getItemCount(): Int {
        return clientList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.clientName)
        val emailTextView: TextView = itemView.findViewById(R.id.clientDetails)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        init {
            // Set the click listener for the delete button
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val client = clientList[position]
                    onDeleteClickListener.onDeleteClick(client.id)
                }
            }
        }
    }
    interface OnDeleteClickListener {
        fun onDeleteClick(userId: Long)
    }
}
