package edu.cit.gaviola.noteify.feature.notes.edit

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.feature.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.feature.notes.list.NotesActivity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel
import edu.cit.gaviola.noteify.feature.profile.ProfileActivity
import kotlinx.coroutines.launch

class EditNoteActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel

    companion object {
        const val EXTRA_NOTE_ID = "EXTRA_NOTE_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        val userName  = getUserName()
        val userEmail = getUserEmail()
        val noteId    = intent.getIntExtra(EXTRA_NOTE_ID, -1)

        if (noteId == -1) {
            showToast(getString(R.string.error_note_not_found))
            finish()
            return
        }

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val etTitle     = findViewById<EditText>(R.id.etNoteTitle)
        val etSubject   = findViewById<EditText>(R.id.etSubject)
        val etContent   = findViewById<EditText>(R.id.etNoteContent)
        val cbImportant = findViewById<CheckBox>(R.id.cbImportant)

        // Load existing note
        lifecycleScope.launch {
            val note = noteViewModel.getNoteById(noteId)
            if (note == null) {
                showToast(getString(R.string.error_note_not_found))
                finish()
                return@launch
            }
            etTitle.setText(note.title)
            etSubject.setText(note.subject)
            etContent.setText(note.content)
            cbImportant.isChecked = note.isImportant

            // Save button
            findViewById<Button>(R.id.btnSaveNote).setOnClickListener {
                val newTitle   = etTitle.text.toString().trim()
                val newSubject = etSubject.text.toString().trim()
                val newContent = etContent.text.toString().trim()
                val important  = cbImportant.isChecked

                when {
                    newTitle.isEmpty() -> {
                        showToast(getString(R.string.error_empty_title))
                        return@setOnClickListener
                    }
                    newSubject.isEmpty() -> {
                        showToast(getString(R.string.error_empty_subject))
                        return@setOnClickListener
                    }
                }

                noteViewModel.updateNote(note, newTitle, newContent, newSubject, important)
            }
        }

        noteViewModel.updateResult.observe(this) { success ->
            if (success) {
                showToast(getString(R.string.text_note_updated))
                navigateTo<NotesActivity>(userName, userEmail) {
                    flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                finish()
            }
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }

        setupBottomNav(userName, userEmail)
    }

    private fun setupBottomNav(userName: String, userEmail: String) {
        val notesTab   = findViewById<LinearLayout>(R.id.btnNavNotes)
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