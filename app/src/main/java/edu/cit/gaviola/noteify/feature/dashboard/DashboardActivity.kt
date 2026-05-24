package edu.cit.gaviola.noteify.feature.dashboard

import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.feature.auth.login.MainActivity
import edu.cit.gaviola.noteify.feature.notes.create.CreateNoteActivity
import edu.cit.gaviola.noteify.core.model.NoteEntity
import edu.cit.gaviola.noteify.feature.notes.list.NotesActivity
import edu.cit.gaviola.noteify.feature.notes.trash.TrashActivity
import edu.cit.gaviola.noteify.feature.notes.viewmodel.NoteViewModel
import edu.cit.gaviola.noteify.feature.profile.ProfileActivity
import edu.cit.gaviola.noteify.feature.settings.SettingsActivity
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var recentActivityContainer: LinearLayout
    private lateinit var tvStorageUsed: TextView
    private lateinit var progressStorageUsage: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val userName  = getUserName()
        val userEmail = getUserEmail()

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Clear the title so the ActionBar doesn't inject the app label
        // alongside our custom logo+text view inside the Toolbar.
        supportActionBar?.title = ""

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val headerView = navigationView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.tvNavName).text = userName
        headerView.findViewById<TextView>(R.id.tvNavEmail).text = userEmail

        recentActivityContainer = findViewById(R.id.recentActivityContainer)
        tvStorageUsed = findViewById(R.id.tvStorageUsed)
        progressStorageUsage = findViewById(R.id.progressStorageUsage)

        if (userEmail.isNotEmpty()) {
            noteViewModel.getNoteCount(userEmail).observe(this) { count ->
                findViewById<TextView>(R.id.tvTotalNotes).text = count.toString()
            }
            noteViewModel.getRecentNotes(userEmail).observe(this) { recentNotes ->
                renderRecentActivity(recentNotes)
            }
        }

        updateStorageDisplay()

        findViewById<CardView>(R.id.cardCreateNote).setOnClickListener {
            navigateTo<CreateNoteActivity>(userName, userEmail)
        }

        setupBottomNav(userName, userEmail)
    }

    private fun updateStorageDisplay() {
        try {
            val cacheBytes = (cacheDir.listFiles()?.sumOf { it.length() } ?: 0L) +
                    (externalCacheDir?.listFiles()?.sumOf { it.length() } ?: 0L)
            val filesBytes = (filesDir.listFiles()?.sumOf { it.length() } ?: 0L)
            val appBytes = cacheBytes + filesBytes

            val statFs = StatFs(Environment.getDataDirectory().path)
            val totalBytes = statFs.totalBytes
            val usagePercent = if (totalBytes > 0)
                ((appBytes.toFloat() / totalBytes.toFloat()) * 100).toInt().coerceAtMost(100)
            else 0

            tvStorageUsed.text = getString(R.string.label_storage_used_dynamic, formatBytes(appBytes))
            progressStorageUsage.progress = usagePercent
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

    private fun renderRecentActivity(notes: List<NoteEntity>) {
        recentActivityContainer.removeAllViews()

        if (notes.isEmpty()) {
            recentActivityContainer.addView(TextView(this).apply {
                text = getString(R.string.text_no_activity)
                textSize = 14f
                setTextColor(Color.parseColor("#888888"))
                setPadding(0, 8, 0, 8)
            })
            return
        }

        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        notes.forEach { note ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.bottomMargin = 20 }
            }

            val dot = TextView(this).apply {
                text = "•"
                textSize = 18f
                setTextColor(Color.parseColor("#9b51e0"))
                setPadding(0, 0, 16, 0)
            }

            val detail = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                )
            }

            detail.addView(TextView(this).apply {
                text = getString(R.string.activity_note_created, note.title)
                textSize = 14f
                setTextColor(Color.parseColor("#222222"))
            })

            detail.addView(TextView(this).apply {
                text = sdf.format(Date(note.timestamp))
                textSize = 12f
                setTextColor(Color.parseColor("#AAAAAA"))
                setPadding(0, 2, 0, 0)
            })

            row.addView(dot)
            row.addView(detail)
            recentActivityContainer.addView(row)
        }
    }

    private fun setupBottomNav(userName: String, userEmail: String) {
        val notesTab   = findViewById<LinearLayout>(R.id.btnNavNotes)
        val profileTab = findViewById<LinearLayout>(R.id.btnNavProfile)

        notesTab.applyNavTabStyle(R.id.iconNotes, R.id.labelNotes, isActive = false)
        profileTab.applyNavTabStyle(R.id.iconProfile, R.id.labelProfile, isActive = false)

        notesTab.setOnClickListener { navigateTo<NotesActivity>(userName, userEmail) }
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            showToast(getString(R.string.text_already_on_dashboard))
        }
        profileTab.setOnClickListener { navigateTo<ProfileActivity>(userName, userEmail) }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val userName  = getUserName()
        val userEmail = getUserEmail()

        when (item.itemId) {
            R.id.nav_dashboard -> showToast(getString(R.string.text_already_on_dashboard))
            R.id.nav_notes     -> navigateTo<NotesActivity>(userName, userEmail)
            R.id.nav_trash     -> navigateTo<TrashActivity>(userName, userEmail)
            R.id.nav_settings  -> navigateTo<SettingsActivity>()
            R.id.nav_logout    -> navigateAndClearStack<MainActivity>()
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}