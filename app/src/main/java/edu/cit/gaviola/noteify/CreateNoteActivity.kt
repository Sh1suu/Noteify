package edu.cit.gaviola.noteify.notes.create

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.notes.list.NotesActivity
import edu.cit.gaviola.noteify.notes.viewmodel.NoteViewModel
import edu.cit.gaviola.noteify.profile.ProfileActivity

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val userName = getUserName()
        val userEmail = getUserEmail()

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val subjects = arrayOf("Select a subject", "Physics", "Calculus", "History", "Computer Science", "English")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        findViewById<Spinner>(R.id.spinnerSubject).adapter = adapter

        noteViewModel.saveResult.observe(this) { success ->
            if (success) {
                showToast("Note saved!")
                navigateTo<NotesActivity>(userName, userEmail) {
                    flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                finish()
            }
        }

        findViewById<Button>(R.id.btnSaveNote).setOnClickListener {
            val title = findViewById<EditText>(R.id.etNoteTitle).text.toString().trim()
            val content = findViewById<EditText>(R.id.etNoteContent).text.toString().trim()
            val subject = findViewById<Spinner>(R.id.spinnerSubject).selectedItem.toString()
            val isImportant = findViewById<CheckBox>(R.id.cbImportant).isChecked

            when {
                title.isEmpty() -> { showToast("Please enter a title"); return@setOnClickListener }
                subject == "Select a subject" -> { showToast("Please select a subject"); return@setOnClickListener }
                userEmail.isEmpty() -> { showToast("User session error. Please login again."); return@setOnClickListener }
            }

            noteViewModel.saveNote(title, content, subject, isImportant, userEmail)
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }

        setupBottomNav(userName, userEmail)
    }

    private fun setupBottomNav(userName: String, userEmail: String) {
        val notesTab = findViewById<LinearLayout>(R.id.btnNavNotes)
        val profileTab = findViewById<LinearLayout>(R.id.btnNavProfile)

        notesTab.applyNavTabStyle(R.id.iconNotes, R.id.labelNotes, isActive = false)
        profileTab.applyNavTabStyle(R.id.iconProfile, R.id.labelProfile, isActive = false)

        notesTab.setOnClickListener { navigateTo<NotesActivity>(userName, userEmail) }
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            navigateTo<DashboardActivity>(userName, userEmail)
        }
        profileTab.setOnClickListener { navigateTo<ProfileActivity>(userName, userEmail) }
    }
}