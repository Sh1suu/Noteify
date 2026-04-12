package edu.cit.gaviola.noteify

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.database.NoteEntity
import edu.cit.gaviola.noteify.viewmodel.NoteViewModel

class NotesActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var notesContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val userName = intent.getStringExtra("USER_NAME") ?: "Student"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Container where notes will be dynamically added
        notesContainer = findViewById(R.id.notesContainer)

        // Observe notes from database
        if (userEmail.isNotEmpty()) {
            noteViewModel.getNotesByUser(userEmail).observe(this) { notes ->
                displayNotes(notes, userName, userEmail)
            }
        }

        // Create New Note card click
        findViewById<CardView>(R.id.cardCreateNote).setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        // Setup bottom nav
        setupBottomNav("notes", userName, userEmail)
    }

    private fun displayNotes(notes: List<NoteEntity>, userName: String, userEmail: String) {
        notesContainer.removeAllViews()

        if (notes.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No notes yet. Tap '+' to create one!"
            emptyText.textSize = 16f
            emptyText.setTextColor(Color.parseColor("#888888"))
            emptyText.setPadding(16, 32, 16, 32)
            notesContainer.addView(emptyText)
            return
        }

        val colors = listOf("#FFD580", "#9b51e0", "#4CAF50", "#2196F3", "#FF5722")
        val textColors = listOf("#FFA000", "#9b51e0", "#2E7D32", "#1565C0", "#BF360C")
        val bgColors = listOf("#FFF3E0", "#F3E8FF", "#E8F5E9", "#E3F2FD", "#FBE9E7")

        notes.forEachIndexed { index, note ->
            val colorIndex = index % colors.size
            val cardLayout = LinearLayout(this)
            cardLayout.orientation = LinearLayout.HORIZONTAL
            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            cardParams.bottomMargin = 32
            cardLayout.layoutParams = cardParams
            cardLayout.setBackgroundColor(Color.WHITE)

            // Left color bar
            val colorBar = android.view.View(this)
            val barParams = LinearLayout.LayoutParams(16, LinearLayout.LayoutParams.MATCH_PARENT)
            colorBar.layoutParams = barParams
            colorBar.setBackgroundColor(Color.parseColor(colors[colorIndex]))

            // Content layout
            val contentLayout = LinearLayout(this)
            contentLayout.orientation = LinearLayout.VERTICAL
            val contentParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            contentParams.setMargins(24, 24, 24, 24)
            contentLayout.layoutParams = contentParams

            // Subject tag
            val subjectTag = TextView(this)
            subjectTag.text = note.subject
            subjectTag.textSize = 12f
            subjectTag.setTextColor(Color.parseColor(textColors[colorIndex]))
            subjectTag.setBackgroundColor(Color.parseColor(bgColors[colorIndex]))
            subjectTag.setPadding(12, 6, 12, 6)

            // Title
            val titleText = TextView(this)
            titleText.text = note.title
            titleText.textSize = 18f
            titleText.setTextColor(Color.BLACK)
            titleText.setPadding(0, 8, 0, 0)
            titleText.setTypeface(null, android.graphics.Typeface.BOLD)

            // Content preview
            val contentText = TextView(this)
            contentText.text = if (note.content.length > 80)
                note.content.substring(0, 80) + "..." else note.content
            contentText.textSize = 14f
            contentText.setTextColor(Color.parseColor("#888888"))
            contentText.setPadding(0, 4, 0, 0)

            // Timestamp
            val timeText = TextView(this)
            val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            timeText.text = sdf.format(java.util.Date(note.timestamp))
            timeText.textSize = 12f
            timeText.setTextColor(Color.parseColor("#AAAAAA"))
            timeText.setPadding(0, 8, 0, 0)

            // Delete button
            val deleteBtn = Button(this)
            deleteBtn.text = "Delete"
            deleteBtn.textSize = 12f
            deleteBtn.setTextColor(Color.parseColor("#FF4444"))
            deleteBtn.setBackgroundColor(Color.TRANSPARENT)
            deleteBtn.setPadding(0, 4, 0, 0)
            deleteBtn.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete '${note.title}'?")
                    .setPositiveButton("Delete") { _, _ ->
                        noteViewModel.deleteNote(note)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            contentLayout.addView(subjectTag)
            contentLayout.addView(titleText)
            contentLayout.addView(contentText)
            contentLayout.addView(timeText)
            contentLayout.addView(deleteBtn)

            cardLayout.addView(colorBar)
            cardLayout.addView(contentLayout)

            // Wrap in CardView
            val cardView = androidx.cardview.widget.CardView(this)
            val cvParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            cvParams.bottomMargin = 32
            cardView.layoutParams = cvParams
            cardView.radius = 24f
            cardView.cardElevation = 4f
            cardView.addView(cardLayout)

            notesContainer.addView(cardView)
        }
    }

    private fun setupBottomNav(activeTab: String, userName: String, userEmail: String) {
        val activeColor = Color.parseColor("#9b51e0")
        val inactiveColor = Color.parseColor("#888888")

        findViewById<ImageView>(R.id.iconNotes).setColorFilter(
            if (activeTab == "notes") activeColor else inactiveColor
        )
        findViewById<TextView>(R.id.labelNotes).setTextColor(
            if (activeTab == "notes") activeColor else inactiveColor
        )
        findViewById<ImageView>(R.id.iconProfile).setColorFilter(
            if (activeTab == "profile") activeColor else inactiveColor
        )
        findViewById<TextView>(R.id.labelProfile).setTextColor(
            if (activeTab == "profile") activeColor else inactiveColor
        )

        findViewById<LinearLayout>(R.id.btnNavNotes).setOnClickListener {}

        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
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