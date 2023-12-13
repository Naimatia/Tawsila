
package com.example.tawsila

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent

class NotificationListActivity : AppCompatActivity() {

    private lateinit var notificationAdapter: NotificationAdapter
    private val notifications = mutableListOf<Notification>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.notificationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize NotificationAdapter
        notificationAdapter = NotificationAdapter(notifications)
        recyclerView.adapter = notificationAdapter

        // Handle FCM notification payload if passed from another activity
        handleFcmNotification(intent)
    }

    private fun handleFcmNotification(intent: Intent?) {
        val title = intent?.getStringExtra("title")
        val body = intent?.getStringExtra("body")

        if (title != null && body != null) {
            // Display the FCM notification in the RecyclerView
            val newNotification = Notification(title, body)
            updateNotificationList(newNotification)
        }
    }

    private fun updateNotificationList(newNotification: Notification) {
        // Update the list and notify the adapter
        notifications.add(newNotification)
        notificationAdapter.notifyDataSetChanged()
    }
}
