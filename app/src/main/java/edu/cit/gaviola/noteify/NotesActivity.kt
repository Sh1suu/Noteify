package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView

class NotesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val userName = intent.getStringExtra("USER_NAME") ?: "Student"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Create New Note card
        findViewById<CardView>(R.id.cardCreateNote).setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }

        // Bottom navigation
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }
    }
}