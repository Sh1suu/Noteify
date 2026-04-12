package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CreateNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val userName = intent.getStringExtra("USER_NAME") ?: "Student"

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Spinner subjects
        val subjects = arrayOf("Select a subject", "Physics", "Calculus", "History", "Computer Science", "English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinnerSubject).adapter = adapter

        // Save Note
        findViewById<Button>(R.id.btnSaveNote).setOnClickListener {
            val title = findViewById<EditText>(R.id.etNoteTitle).text.toString()
            val content = findViewById<EditText>(R.id.etNoteContent).text.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Note '$title' saved!", Toast.LENGTH_SHORT).show()

            // Pass note data back to NotesActivity
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("NOTE_TITLE", title)
            intent.putExtra("NOTE_CONTENT", content)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        // Cancel
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
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

        findViewById<LinearLayout>(R.id.btnNavProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            startActivity(intent)
        }
    }
}