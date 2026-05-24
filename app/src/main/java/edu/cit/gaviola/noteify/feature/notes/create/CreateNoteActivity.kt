package edu.cit.gaviola.noteify.feature.notes.create

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.feature.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.feature.notes.list.NotesActivity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel
import edu.cit.gaviola.noteify.feature.profile.ProfileActivity

class CreateNoteActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val userName  = getUserName()
        val userEmail = getUserEmail()

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        noteViewModel.saveResult.observe(this) { success ->
            if (success) {
                showToast(getString(R.string.text_note_saved))
                navigateTo<NotesActivity>(userName, userEmail) {
                    flags = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                finish()
            }
        }

        findViewById<Button>(R.id.btnSaveNote).setOnClickListener {
            val title     = findViewById<EditText>(R.id.etNoteTitle).text.toString().trim()
            val subject   = findViewById<EditText>(R.id.etSubject).text.toString().trim()
            val content   = findViewById<EditText>(R.id.etNoteContent).text.toString().trim()
            val important = findViewById<CheckBox>(R.id.cbImportant).isChecked

            when {
                title.isEmpty() -> {
                    showToast(getString(R.string.error_empty_title))
                    return@setOnClickListener
                }
                subject.isEmpty() -> {
                    showToast(getString(R.string.error_empty_subject))
                    return@setOnClickListener
                }
                userEmail.isEmpty() -> {
                    showToast(getString(R.string.error_user_session))
                    return@setOnClickListener
                }
            }

            noteViewModel.saveNote(title, content, subject, important, userEmail)
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