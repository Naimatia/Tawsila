package com.example.tawsila
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class DriverAdapter( private var DriverList: List<ClientDTO>,
                     private val onDeleteClickListener: OnDeleteClickListener
) : RecyclerView.Adapter<DriverAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_driver_cardview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver = DriverList[position]

        holder.nameTextView.text = driver.name
        holder.emailTextView.text = driver.email

        // Set the click listener for the delete button
        holder.deleteButton.setOnClickListener {
            onDeleteClickListener.onDeleteClick(driver.id)
        }
    }

    override fun getItemCount(): Int {
        return DriverList.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.driverName)
        val emailTextView: TextView = itemView.findViewById(R.id.driverDetails)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButtonD)

        init {
            // Set the click listener for the delete button
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val driver = DriverList[position]
                    onDeleteClickListener.onDeleteClick(driver.id)
                }
            }
        }
    }
    interface OnDeleteClickListener {
        fun onDeleteClick(userId: Long)
    }

}