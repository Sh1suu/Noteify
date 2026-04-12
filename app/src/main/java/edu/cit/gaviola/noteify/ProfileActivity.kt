package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userName = intent.getStringExtra("USER_NAME") ?: "John Doe"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Display passed name
        findViewById<TextView>(R.id.tvProfileName).text = userName

        // Log Out
        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Bottom nav
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavNotes).setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }
    }
}