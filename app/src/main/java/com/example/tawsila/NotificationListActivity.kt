package com.example.tawsila

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class NotificationListActivity : AppCompatActivity() {

    private lateinit var notificationAdapter: NotificationAdapter
    private val notifications = mutableListOf<Notification>()

    // Reference to Firebase Database
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_list)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.notificationRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize NotificationAdapter
        notificationAdapter = NotificationAdapter(notifications)
        recyclerView.adapter = notificationAdapter

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("notifications")

        // Handle FCM notification payload if passed from another activity
        handleFcmNotification(intent)

        setUpBottomNavigationView()

        // Fetch messages from Firebase
        fetchMessages()
    }

    private fun fetchMessages() {
        // Listen for changes in the "notifications" node
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear existing notifications
                notifications.clear()

                // Iterate through the messages and add them to the list
                for (messageSnapshot in snapshot.children) {
                    val title = messageSnapshot.child("title").getValue(String::class.java)
                    val body = messageSnapshot.child("body").getValue(String::class.java)

                    if (title != null && body != null) {
                        val newNotification = Notification(title, body)
                        updateNotificationList(newNotification)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                // For simplicity, you can log the error
                error.toException().printStackTrace()
            }
        })
    }

    private fun updateNotificationList(newNotification: Notification) {
        // Update the list and notify the adapter
        notifications.add(newNotification)
        notificationAdapter.notifyDataSetChanged()
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

    private fun setUpBottomNavigationView() {
        val userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_notification
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    val intent = Intent(this, Interface_driver::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_trajet -> {
                    val intent = Intent(this, DriverCovoiturageActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_Add -> {
                    val intent = Intent(this, driver_trajet::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_notification -> {
                    // Already on the notification page
                    true
                }
                R.id.bottom_profil -> {
                    val intent = Intent(this, Profil::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }


    private fun navigateToNotificationListActivity() {
        val userId = intent.getLongExtra("USER_ID", -1)

        val intent = Intent(this, NotificationListActivity::class.java)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
    }

}
