package edu.cit.gaviola.noteify.profile

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.auth.login.MainActivity
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.notes.list.NotesActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userName = getUserName()
        val userEmail = getUserEmail()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        findViewById<TextView>(R.id.tvProfileName).text = userName

        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            navigateAndClearStack<MainActivity>()
        }

        setupBottomNav(userName, userEmail)
    }

    private fun setupBottomNav(userName: String, userEmail: String) {
        val notesTab = findViewById<LinearLayout>(R.id.btnNavNotes)
        val profileTab = findViewById<LinearLayout>(R.id.btnNavProfile)

        notesTab.applyNavTabStyle(R.id.iconNotes, R.id.labelNotes, isActive = false)
        profileTab.applyNavTabStyle(R.id.iconProfile, R.id.labelProfile, isActive = true)

        notesTab.setOnClickListener { navigateTo<NotesActivity>(userName, userEmail) }
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            navigateTo<DashboardActivity>(userName, userEmail)
        }
        profileTab.setOnClickListener { /* already here */ }
    }
}