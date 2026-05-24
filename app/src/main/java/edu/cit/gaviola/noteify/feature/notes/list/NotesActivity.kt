package edu.cit.gaviola.noteify.feature.notes.list

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.feature.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.feature.notes.create.CreateNoteActivity
import edu.cit.gaviola.noteify.core.model.NoteEntity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel
import edu.cit.gaviola.noteify.feature.profile.ProfileActivity
import java.text.SimpleDateFormat
import java.util.*

class NotesActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var notesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val userName  = getUserName()
        val userEmail = getUserEmail()

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        notesContainer = findViewById(R.id.notesContainer)

        if (userEmail.isNotEmpty()) {
            noteViewModel.getNotesByUser(userEmail).observe(this) { notes ->
                displayNotes(notes, userName, userEmail)
            }
        }

        // Bug 2: entire card is the click target
        findViewById<CardView>(R.id.cardCreateNote).setOnClickListener {
            navigateTo<CreateNoteActivity>(userName, userEmail)
        }

        setupBottomNav(userName, userEmail)
    }

    private fun displayNotes(notes: List<NoteEntity>, userName: String, userEmail: String) {
        notesContainer.removeAllViews()

        // Re-add the "Create New Note" card first so it stays at top
        val inflated = layoutInflater.inflate(R.layout.activity_notes, null)
        val createCard = inflated.findViewById<CardView>(R.id.cardCreateNote)

        if (notes.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = getString(R.string.text_no_notes)
                textSize = 16f
                setTextColor(Color.parseColor("#888888"))
                setPadding(16, 32, 16, 32)
            }
            notesContainer.addView(emptyText)
            return
        }

        val colors     = listOf("#FFD580", "#9b51e0", "#4CAF50", "#2196F3", "#FF5722")
        val textColors = listOf("#FFA000", "#9b51e0", "#2E7D32", "#1565C0", "#BF360C")
        val bgColors   = listOf("#FFF3E0", "#F3E8FF", "#E8F5E9", "#E3F2FD", "#FBE9E7")

        notes.forEachIndexed { index, note ->
            val ci = index % colors.size

            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = 32 }
                setBackgroundColor(Color.WHITE)
            }

            val colorBar = android.view.View(this).apply {
                layoutParams = LinearLayout.LayoutParams(16, LinearLayout.LayoutParams.MATCH_PARENT)
                setBackgroundColor(Color.parseColor(colors[ci]))
            }

            val contentLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                ).also { it.setMargins(24, 24, 24, 24) }
            }

            val subjectTag = TextView(this).apply {
                text = note.subject
                textSize = 12f
                setTextColor(Color.parseColor(textColors[ci]))
                setBackgroundColor(Color.parseColor(bgColors[ci]))
                setPadding(12, 6, 12, 6)
            }

            val titleText = TextView(this).apply {
                text = note.title
                textSize = 18f
                setTextColor(Color.BLACK)
                setPadding(0, 8, 0, 0)
                setTypeface(null, Typeface.BOLD)
            }

            val contentText = TextView(this).apply {
                text = note.content.truncate(80)
                textSize = 14f
                setTextColor(Color.parseColor("#888888"))
                setPadding(0, 4, 0, 0)
            }

            val timeText = TextView(this).apply {
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                text = sdf.format(Date(note.timestamp))
                textSize = 12f
                setTextColor(Color.parseColor("#AAAAAA"))
                setPadding(0, 8, 0, 0)
            }

            val deleteBtn = Button(this).apply {
                text = getString(R.string.btn_delete)
                textSize = 12f
                setTextColor(Color.parseColor("#FF4444"))
                setBackgroundColor(Color.TRANSPARENT)
                setPadding(0, 4, 0, 0)
                setOnClickListener {
                    AlertDialog.Builder(this@NotesActivity)
                        .setTitle(getString(R.string.title_delete_note))
                        .setMessage("Are you sure you want to delete '${note.title}'?")
                        .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                            noteViewModel.deleteNote(note)
                        }
                        .setNegativeButton(getString(R.string.btn_cancel), null)
                        .show()
                }
            }

            contentLayout.apply {
                addView(subjectTag)
                addView(titleText)
                addView(contentText)
                addView(timeText)
                addView(deleteBtn)
            }

            cardLayout.addView(colorBar)
            cardLayout.addView(contentLayout)

            val cardView = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = 32 }
                radius = 24f
                cardElevation = 4f
                addView(cardLayout)
            }

            notesContainer.addView(cardView)
        }
    }

    private fun setupBottomNav(userName: String, userEmail: String) {
        val notesTab   = findViewById<LinearLayout>(R.id.btnNavNotes)
        val profileTab = findViewById<LinearLayout>(R.id.btnNavProfile)

        notesTab.applyNavTabStyle(R.id.iconNotes, R.id.labelNotes, isActive = true)
        profileTab.applyNavTabStyle(R.id.iconProfile, R.id.labelProfile, isActive = false)

        notesTab.setOnClickListener { /* already here */ }
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            navigateTo<DashboardActivity>(userName, userEmail)
        }
        profileTab.setOnClickListener { navigateTo<ProfileActivity>(userName, userEmail) }
    }
}