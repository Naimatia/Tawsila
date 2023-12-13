package com.example.tawsila

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class Maps : AppCompatActivity() {

    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps)

        setUpBottomNavigationView()

        // Load OSMdroid configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        // Get the map view
        map = findViewById(R.id.mapView) // Make sure to replace with your MapView ID

        // Set the tile source
        map.setTileSource(TileSourceFactory.MAPNIK)

        // Enable multi-touch controls
        map.setMultiTouchControls(true)

        // Define a bounding box for Tunisia
        val tunisiaBoundingBox = BoundingBox(30.2313, 7.5241, 37.4792, 11.6855)

        // Zoom to the bounding box of Tunisia
        map.zoomToBoundingBox(tunisiaBoundingBox, true)

        // Enable location overlay
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        locationOverlay.enableMyLocation()
        map.overlays.add(locationOverlay)

        // Set up other map configurations or overlays as needed
        // ...

        // Optionally, you can set a default zoom level
        val mapController: IMapController = map.controller
        mapController.setZoom(50)


    }



    private fun setUpBottomNavigationView() {
        val  userId = intent.getLongExtra("USER_ID", -1)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.bottom_maps
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    val intent = Intent(this, Interface_client::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_trajet -> {
                    val intent = Intent(this, ListeReservationActivity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.bottom_maps -> {
                    val intent = Intent(this, Maps::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                    finish()
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
}