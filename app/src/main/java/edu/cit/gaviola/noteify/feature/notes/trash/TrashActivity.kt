package edu.cit.gaviola.noteify.feature.notes.trash

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
import edu.cit.gaviola.noteify.core.model.NoteEntity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TrashActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var trashContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trash)

        val userName  = getUserName()
        val userEmail = getUserEmail()

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        trashContainer = findViewById(R.id.trashContainer)

        // Purge expired notes every time Trash is opened
        noteViewModel.purgeExpiredTrash()

        if (userEmail.isNotEmpty()) {
            noteViewModel.getTrashedNotes(userEmail).observe(this) { notes ->
                renderTrash(notes, userEmail)
            }
        }

        findViewById<Button>(R.id.btnEmptyTrash).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_empty_trash))
                .setMessage(getString(R.string.msg_empty_trash_confirm))
                .setPositiveButton(getString(R.string.btn_empty_trash)) { _, _ ->
                    noteViewModel.emptyTrash(userEmail)
                    showToast(getString(R.string.text_trash_emptied))
                }
                .setNegativeButton(getString(R.string.btn_cancel), null)
                .show()
        }
    }

    private fun renderTrash(notes: List<NoteEntity>, userEmail: String) {
        trashContainer.removeAllViews()

        if (notes.isEmpty()) {
            trashContainer.addView(TextView(this).apply {
                text = getString(R.string.text_trash_empty)
                textSize = 15f
                setTextColor(Color.parseColor("#888888"))
                gravity = android.view.Gravity.CENTER
                setPadding(16, 48, 16, 48)
            })
            return
        }

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val now = System.currentTimeMillis()

        notes.forEach { note ->
            val deletedAt = note.deletedAt ?: now
            val daysElapsed = TimeUnit.MILLISECONDS.toDays(now - deletedAt)
            val daysLeft = (30 - daysElapsed).coerceAtLeast(0)

            val cardLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundColor(Color.WHITE)
            }

            val redBar = android.view.View(this).apply {
                layoutParams = LinearLayout.LayoutParams(10, LinearLayout.LayoutParams.MATCH_PARENT)
                setBackgroundColor(Color.parseColor("#FF4444"))
            }

            val content = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                ).also { it.setMargins(18, 16, 18, 16) }
            }

            content.addView(TextView(this).apply {
                text = note.title
                textSize = 15f
                setTextColor(Color.parseColor("#111111"))
                setTypeface(null, Typeface.BOLD)
            })

            content.addView(TextView(this).apply {
                text = note.subject
                textSize = 12f
                setTextColor(Color.parseColor("#9b51e0"))
                setPadding(0, 3, 0, 0)
            })

            content.addView(TextView(this).apply {
                text = getString(R.string.label_deleted_on, sdf.format(Date(deletedAt)))
                textSize = 11f
                setTextColor(Color.parseColor("#AAAAAA"))
                setPadding(0, 4, 0, 0)
            })

            val daysColor = when {
                daysLeft <= 3  -> "#FF4444"
                daysLeft <= 7  -> "#FF9800"
                else           -> "#888888"
            }
            content.addView(TextView(this).apply {
                text = getString(R.string.label_days_left, daysLeft)
                textSize = 11f
                setTextColor(Color.parseColor(daysColor))
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 2, 0, 0)
            })

            // Actions
            val actionRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = 8 }
            }

            actionRow.addView(Button(this).apply {
                text = getString(R.string.btn_restore)
                textSize = 12f
                setTextColor(Color.parseColor("#4CAF50"))
                setBackgroundColor(Color.TRANSPARENT)
                setPadding(0, 0, 24, 0)
                setOnClickListener {
                    noteViewModel.restoreNote(note.id)
                    showToast(getString(R.string.text_note_restored))
                }
            })

            actionRow.addView(Button(this).apply {
                text = getString(R.string.btn_delete_permanently)
                textSize = 12f
                setTextColor(Color.parseColor("#FF4444"))
                setBackgroundColor(Color.TRANSPARENT)
                setPadding(0, 0, 0, 0)
                setOnClickListener {
                    AlertDialog.Builder(this@TrashActivity)
                        .setTitle(getString(R.string.title_delete_permanently))
                        .setMessage(getString(R.string.msg_delete_permanently_confirm, note.title))
                        .setPositiveButton(getString(R.string.btn_delete_permanently)) { _, _ ->
                            noteViewModel.deleteNote(note)
                        }
                        .setNegativeButton(getString(R.string.btn_cancel), null)
                        .show()
                }
            })

            content.addView(actionRow)
            cardLayout.addView(redBar)
            cardLayout.addView(content)

            trashContainer.addView(CardView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = 12 }
                radius = 12f
                cardElevation = 2f
                addView(cardLayout)
            })
        }
    }
}