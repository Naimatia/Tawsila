package com.example.tawsila

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging


class Interface_driver : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().subscribeToTopic("driver_notifications")

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        // Set content view from layout file
        setContentView(R.layout.activity_interface_driver)

        // Call the function to set up userId, BottomNavigationView, and FCM
        setUpBottomNavigationView()
        setUpFirebaseMessaging()

        // Find DrawerLayout by ID
        drawerLayout = findViewById(R.id.drawer_layout)

        setContentView(R.layout.activity_interface_driver)

        // Call the function to set up userId and BottomNavigationView
        setUpBottomNavigationView()

        drawerLayout = findViewById(R.id.drawer_layout)
        // Sample data for testing
        val imageList = listOf(
            ImageItem(R.drawable.ic_launcher_foreground, "Image 1"),
            ImageItem(R.drawable.ic_launcher_foreground, "Image 2"),
            ImageItem(R.drawable.ic_launcher_foreground, "Image 3"),
            ImageItem(R.drawable.ic_launcher_foreground, "Image 3"),
            ImageItem(R.drawable.ic_launcher_foreground, "Image 3")
        )

        // Set up RecyclerView
      //  val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
       // recyclerView.layoutManager = LinearLayoutManager(this)
      //  recyclerView.adapter = Adapter(imageList)

    }

    private fun setUpFirebaseMessaging() {
        // Create a BroadcastReceiver for handling received notifications
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Handle the received notification data
                val title = intent?.getStringExtra("title")
                val body = intent?.getStringExtra("body")

                // Update UI or perform any action with the received notification data
                // For example, display a Toast
                Toast.makeText(context, "Notification Received: $title - $body", Toast.LENGTH_SHORT).show()
            }
        }

        // Register the receiver with the intent filter
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("notification_received"))
    }


    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
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
                    val intent = Intent(this, NotificationListActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_profil -> {
                    val intent = Intent(this, Profil_Driver::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

}