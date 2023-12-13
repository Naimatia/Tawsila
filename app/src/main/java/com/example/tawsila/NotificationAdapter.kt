package com.example.tawsila

// NotificationAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle: TextView = itemView.findViewById(R.id.textTitle)
        val textBody: TextView = itemView.findViewById(R.id.textBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]

        holder.textTitle.text = notification.title
        holder.textBody.text = notification.body
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}
