package edu.cit.gaviola.noteify.feature.notes.list

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
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
import edu.cit.gaviola.noteify.feature.notes.edit.EditNoteActivity
import edu.cit.gaviola.noteify.core.model.NoteEntity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel
import edu.cit.gaviola.noteify.feature.profile.ProfileActivity
import java.text.SimpleDateFormat
import java.util.*

class NotesActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var notesContainer: LinearLayout
    private val collapsedSubjects = mutableSetOf<String>()

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
            noteViewModel.getNotesByUserGroupedBySubject(userEmail).observe(this) { notes ->
                displayNotesGrouped(notes, userName, userEmail)
            }
        }

        setupBottomNav(userName, userEmail)
    }

    private fun displayNotesGrouped(notes: List<NoteEntity>, userName: String, userEmail: String) {
        notesContainer.removeAllViews()

        // Create card always at top
        val createCard = layoutInflater.inflate(R.layout.item_create_note_card, notesContainer, false)
        createCard.setOnClickListener { navigateTo<CreateNoteActivity>(userName, userEmail) }
        notesContainer.addView(createCard)

        if (notes.isEmpty()) {
            notesContainer.addView(TextView(this).apply {
                text = getString(R.string.text_no_notes)
                textSize = 16f
                setTextColor(Color.parseColor("#888888"))
                setPadding(16, 32, 16, 32)
            })
            return
        }

        val accentColors = listOf("#9b51e0", "#4CAF50", "#2196F3", "#FF5722", "#E91E63")
        val grouped = notes.groupBy { it.subject }

        grouped.entries.forEachIndexed { groupIndex, (subject, subjectNotes) ->
            val accentColor = accentColors[groupIndex % accentColors.size]
            val isCollapsed = collapsedSubjects.contains(subject)

            // ── Subject header card ─────────────────────────────────────────
            val headerCard = CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = 16; it.bottomMargin = 4 }
                radius = 12f
                cardElevation = 3f
                setCardBackgroundColor(Color.WHITE)
            }

            // Inner row carries the ripple so we avoid the Drawable/Int type mismatch on CardView
            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(20, 18, 20, 18)
                isClickable = true
                isFocusable = true
                background = with(obtainStyledAttributes(intArrayOf(android.R.attr.selectableItemBackground))) {
                    getDrawable(0).also { recycle() }
                }
            }

            val stripe = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(8, 48).also { it.marginEnd = 14 }
                setBackgroundColor(Color.parseColor(accentColor))
            }

            val subjectLabel = TextView(this).apply {
                text = subject
                textSize = 15f
                setTextColor(Color.parseColor("#111111"))
                setTypeface(null, Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val countBadge = TextView(this).apply {
                val noun = if (subjectNotes.size == 1)
                    getString(R.string.label_note_singular)
                else
                    getString(R.string.label_note_plural)
                text = "${subjectNotes.size} $noun"
                textSize = 12f
                setTextColor(Color.parseColor(accentColor))
            }

            val chevron = TextView(this).apply {
                text = if (isCollapsed) "  ▶" else "  ▼"
                textSize = 13f
                setTextColor(Color.parseColor("#888888"))
            }

            headerRow.addView(stripe)
            headerRow.addView(subjectLabel)
            headerRow.addView(countBadge)
            headerRow.addView(chevron)
            headerCard.addView(headerRow)

            // ── Notes container for this subject ────────────────────────────
            val subjectNotesContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                visibility = if (isCollapsed) View.GONE else View.VISIBLE
            }

            headerRow.setOnClickListener {
                if (collapsedSubjects.contains(subject)) {
                    collapsedSubjects.remove(subject)
                    subjectNotesContainer.visibility = View.VISIBLE
                    chevron.text = "  ▼"
                } else {
                    collapsedSubjects.add(subject)
                    subjectNotesContainer.visibility = View.GONE
                    chevron.text = "  ▶"
                }
            }

            notesContainer.addView(headerCard)
            notesContainer.addView(subjectNotesContainer)

            subjectNotes.forEach { note ->
                subjectNotesContainer.addView(buildNoteCard(note, accentColor, userName, userEmail))
            }

            // Divider between subject groups
            notesContainer.addView(View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 4
                ).also { it.topMargin = 4; it.bottomMargin = 4 }
                setBackgroundColor(Color.parseColor("#E0E0E0"))
            })
        }
    }

    private fun buildNoteCard(
        note: NoteEntity,
        accentColor: String,
        userName: String,
        userEmail: String
    ): CardView {
        val cardLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.WHITE)
        }

        val colorBar = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor(accentColor))
        }

        val contentLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ).also { it.setMargins(18, 16, 18, 16) }
        }

        val titleText = TextView(this).apply {
            text = note.title
            textSize = 15f
            setTextColor(Color.BLACK)
            setTypeface(null, Typeface.BOLD)
        }

        val contentPreview = TextView(this).apply {
            text = note.content.truncate(80)
            textSize = 13f
            setTextColor(Color.parseColor("#888888"))
            setPadding(0, 3, 0, 0)
        }

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeText = TextView(this).apply {
            text = sdf.format(Date(note.timestamp))
            textSize = 11f
            setTextColor(Color.parseColor("#AAAAAA"))
            setPadding(0, 4, 0, 0)
        }

        contentLayout.addView(titleText)
        contentLayout.addView(contentPreview)
        contentLayout.addView(timeText)

        if (note.isImportant) {
            contentLayout.addView(TextView(this).apply {
                text = "★ ${getString(R.string.label_important)}"
                textSize = 11f
                setTextColor(Color.parseColor("#FF5722"))
                setPadding(0, 2, 0, 0)
            })
        }

        // Action row: Edit | Delete
        val actionRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.topMargin = 6 }
        }

        actionRow.addView(Button(this).apply {
            text = getString(R.string.btn_edit)
            textSize = 12f
            setTextColor(Color.parseColor("#9b51e0"))
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(0, 0, 24, 0)
            setOnClickListener {
                startActivity(
                    Intent(this@NotesActivity, EditNoteActivity::class.java).apply {
                        putExtra("USER_NAME", userName)
                        putExtra("USER_EMAIL", userEmail)
                        putExtra(EditNoteActivity.EXTRA_NOTE_ID, note.id)
                    }
                )
            }
        })

        actionRow.addView(Button(this).apply {
            text = getString(R.string.btn_delete)
            textSize = 12f
            setTextColor(Color.parseColor("#FF4444"))
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(0, 0, 0, 0)
            setOnClickListener {
                AlertDialog.Builder(this@NotesActivity)
                    .setTitle(getString(R.string.title_delete_note))
                    .setMessage(getString(R.string.msg_delete_note_confirm, note.title))
                    .setPositiveButton(getString(R.string.btn_move_to_trash)) { _, _ ->
                        noteViewModel.softDeleteNote(note.id)
                        showToast(getString(R.string.text_note_moved_to_trash))
                    }
                    .setNegativeButton(getString(R.string.btn_cancel), null)
                    .show()
            }
        })

        contentLayout.addView(actionRow)
        cardLayout.addView(colorBar)
        cardLayout.addView(contentLayout)

        return CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 6; it.topMargin = 2; it.marginStart = 8; it.marginEnd = 8 }
            radius = 8f
            cardElevation = 1f
            addView(cardLayout)
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