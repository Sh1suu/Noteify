package edu.cit.gaviola.noteify

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView

class NotesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val userName = intent.getStringExtra("USER_NAME") ?: "Student"

        // Setup Toolbar with back button
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Create New Note card click
        findViewById<CardView>(R.id.cardCreateNote).setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }

        // Setup bottom nav
        setupBottomNav("notes", userName)
    }

    private fun setupBottomNav(activeTab: String, userName: String) {
        val btnNavNotes = findViewById<LinearLayout>(R.id.btnNavNotes)
        val btnNavHome = findViewById<LinearLayout>(R.id.btnNavHome)
        val btnNavProfile = findViewById<LinearLayout>(R.id.btnNavProfile)

        // Active vs inactive colors
        val activeColor = Color.parseColor("#9b51e0")
        val inactiveColor = Color.parseColor("#888888")

        // Notes tab coloring
        findViewById<ImageView>(R.id.iconNotes).setColorFilter(
            if (activeTab == "notes") activeColor else inactiveColor
        )
        findViewById<TextView>(R.id.labelNotes).setTextColor(
            if (activeTab == "notes") activeColor else inactiveColor
        )

        // Profile tab coloring
        findViewById<ImageView>(R.id.iconProfile).setColorFilter(
            if (activeTab == "profile") activeColor else inactiveColor
        )
        findViewById<TextView>(R.id.labelProfile).setTextColor(
            if (activeTab == "profile") activeColor else inactiveColor
        )

        // Notes button
        btnNavNotes.setOnClickListener {
            if (activeTab != "notes") {
                val intent = Intent(this, NotesActivity::class.java)
                intent.putExtra("USER_NAME", userName)
                startActivity(intent)
            }
        }

        // Home button
        btnNavHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }

        // Profile button
        btnNavProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}