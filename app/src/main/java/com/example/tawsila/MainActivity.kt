package com.example.tawsila

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.util.Pair
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private val SPLASH_SCREEN = 5000 // Utilisation de val au lieu de static int
    // Variable
    private lateinit var topAnim: Animation
    private lateinit var bottomAnim: Animation
    private lateinit var logo: TextView
    private lateinit var slogan: TextView
    private lateinit var image: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_main)
        // Load animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        // Initialize views
        image = findViewById(R.id.imageView)
        logo = findViewById(R.id.textView)
        slogan = findViewById(R.id.textView2)

        // Set animations for views
        image.startAnimation(topAnim)
        logo.startAnimation(bottomAnim)
        slogan.startAnimation(bottomAnim)

        Handler().postDelayed({
            val intent = Intent(this, Login::class.java)

            // Création d'un tableau de paires pour les éléments de transition
            val pairs = arrayOf(
                Pair<View, String>(image, "logo_image"),
                Pair<View, String>(logo, "logo_text")
            )

            // Création de l'objet ActivityOptions avec les paires
            val options = ActivityOptions.makeSceneTransitionAnimation(this, *pairs)

            startActivity(intent, options.toBundle())
            finish()
        }, SPLASH_SCREEN.toLong())
    }
}