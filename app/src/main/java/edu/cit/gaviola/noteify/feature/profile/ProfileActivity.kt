package edu.cit.gaviola.noteify.feature.profile

import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.core.preferences.AppPreferences
import edu.cit.gaviola.noteify.feature.auth.login.MainActivity
import edu.cit.gaviola.noteify.feature.dashboard.DashboardActivity
import edu.cit.gaviola.noteify.feature.notes.list.NotesActivity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var prefs: AppPreferences
    private lateinit var noteViewModel: NoteViewModel

    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileCourse: TextView
    private lateinit var tvProfileYear: TextView
    private lateinit var etCourse: EditText
    private lateinit var etYear: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var btnSaveProfile: Button
    private lateinit var viewModeGroup: LinearLayout
    private lateinit var editModeGroup: LinearLayout
    private lateinit var tvStorageUsed: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userName  = getUserName()
        val userEmail = getUserEmail()

        prefs = AppPreferences(this)
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        tvProfileName   = findViewById(R.id.tvProfileName)
        tvProfileCourse = findViewById(R.id.tvProfileCourse)
        tvProfileYear   = findViewById(R.id.tvProfileYear)
        etCourse        = findViewById(R.id.etCourse)
        etYear          = findViewById(R.id.etYear)
        btnEditProfile  = findViewById(R.id.btnEditProfile)
        btnSaveProfile  = findViewById(R.id.btnSaveProfile)
        viewModeGroup   = findViewById(R.id.viewModeGroup)
        editModeGroup   = findViewById(R.id.editModeGroup)
        tvStorageUsed   = findViewById(R.id.tvStorageUsed)

        tvProfileName.text = userName

        val savedCourse = prefs.getCourse(userEmail)
        val savedYear   = prefs.getYear(userEmail)
        tvProfileCourse.text = if (savedCourse.isNotEmpty()) savedCourse
        else getString(R.string.text_placeholder_course_default)
        tvProfileYear.text   = if (savedYear.isNotEmpty()) savedYear
        else getString(R.string.text_placeholder_year_default)

        if (userEmail.isNotEmpty()) {
            noteViewModel.getNoteCount(userEmail).observe(this) { count ->
                findViewById<TextView>(R.id.tvNotesCount).text = count.toString()
            }
        }

        updateStorageDisplay()

        btnEditProfile.setOnClickListener {
            etCourse.setText(prefs.getCourse(userEmail).ifEmpty {
                getString(R.string.text_placeholder_course_default)
            })
            etYear.setText(prefs.getYear(userEmail).ifEmpty {
                getString(R.string.text_placeholder_year_default)
            })
            viewModeGroup.visibility = View.GONE
            editModeGroup.visibility = View.VISIBLE
        }

        btnSaveProfile.setOnClickListener {
            val newCourse = etCourse.text.toString().trim()
            val newYear   = etYear.text.toString().trim()

            if (newCourse.isEmpty() || newYear.isEmpty()) {
                showToast(getString(R.string.error_fill_profile_fields))
                return@setOnClickListener
            }

            prefs.setCourse(userEmail, newCourse)
            prefs.setYear(userEmail, newYear)

            tvProfileCourse.text = newCourse
            tvProfileYear.text   = newYear

            editModeGroup.visibility = View.GONE
            viewModeGroup.visibility = View.VISIBLE
            showToast(getString(R.string.text_profile_saved))
        }

        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            navigateAndClearStack<MainActivity>()
        }

        setupBottomNav(userName, userEmail)
    }

    private fun updateStorageDisplay() {
        try {
            val cacheBytes = (cacheDir.listFiles()?.sumOf { it.length() } ?: 0L) +
                    (externalCacheDir?.listFiles()?.sumOf { it.length() } ?: 0L)
            val filesBytes = (filesDir.listFiles()?.sumOf { it.length() } ?: 0L)
            val appBytes = cacheBytes + filesBytes
            tvStorageUsed.text = formatBytes(appBytes)
        } catch (e: Exception) {
            tvStorageUsed.text = getString(R.string.label_storage_unavailable)
        }
    }

    private fun formatBytes(bytes: Long): String = when {
        bytes >= 1_073_741_824L -> String.format("%.2f GB", bytes / 1_073_741_824.0)
        bytes >= 1_048_576L     -> String.format("%.2f MB", bytes / 1_048_576.0)
        bytes >= 1_024L         -> String.format("%.2f KB", bytes / 1_024.0)
        else                    -> "$bytes B"
    }

    private fun setupBottomNav(userName: String, userEmail: String) {
        val notesTab   = findViewById<LinearLayout>(R.id.btnNavNotes)
        val profileTab = findViewById<LinearLayout>(R.id.btnNavProfile)

        notesTab.applyNavTabStyle(R.id.iconNotes, R.id.labelNotes, isActive = false)
        profileTab.applyNavTabStyle(R.id.iconProfile, R.id.labelProfile, isActive = true)

        notesTab.setOnClickListener { navigateTo<NotesActivity>(userName, userEmail) }
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            navigateTo<DashboardActivity>(userName, userEmail)
        }
        profileTab.setOnClickListener { /* already here */ }
    }
}