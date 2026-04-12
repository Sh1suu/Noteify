package edu.cit.gaviola.noteify

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.viewmodel.NoteViewModel

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val userName = intent.getStringExtra("USER_NAME") ?: "Student"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Spinner subjects
        val subjects = arrayOf("Select a subject", "Physics", "Calculus", "History", "Computer Science", "English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinnerSubject).adapter = adapter

        // Observe save result
        noteViewModel.saveResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NotesActivity::class.java)
                intent.putExtra("USER_NAME", userName)
                intent.putExtra("USER_EMAIL", userEmail)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }

        // Save Note
        findViewById<Button>(R.id.btnSaveNote).setOnClickListener {
            val title = findViewById<EditText>(R.id.etNoteTitle).text.toString().trim()
            val content = findViewById<EditText>(R.id.etNoteContent).text.toString().trim()
            val subject = findViewById<Spinner>(R.id.spinnerSubject).selectedItem.toString()
            val isImportant = findViewById<CheckBox>(R.id.cbImportant).isChecked

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (subject == "Select a subject") {
                Toast.makeText(this, "Please select a subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userEmail.isEmpty()) {
                Toast.makeText(this, "User session error. Please login again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            noteViewModel.saveNote(title, content, subject, isImportant, userEmail)
        }

        // Cancel
        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }

        // Bottom nav
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavNotes).setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }
}