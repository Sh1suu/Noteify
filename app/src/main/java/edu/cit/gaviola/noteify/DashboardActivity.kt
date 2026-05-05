package edu.cit.gaviola.noteify.dashboard

import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.auth.login.MainActivity
import edu.cit.gaviola.noteify.core.extensions.*
import edu.cit.gaviola.noteify.notes.create.CreateNoteActivity
import edu.cit.gaviola.noteify.notes.list.NotesActivity
import edu.cit.gaviola.noteify.notes.viewmodel.NoteViewModel
import edu.cit.gaviola.noteify.profile.ProfileActivity
import edu.cit.gaviola.noteify.settings.SettingsActivity

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Extension functions replace the boilerplate intent.getStringExtra calls
        val userName = getUserName()
        val userEmail = getUserEmail()

        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

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

        findViewById<TextView>(R.id.tvWelcomeUser).text = "Welcome, $userName!"

        if (userEmail.isNotEmpty()) {
            noteViewModel.getNoteCount(userEmail).observe(this) { count ->
                findViewById<TextView>(R.id.tvTotalNotes).text = count.toString()
            }
        }

        findViewById<Button>(R.id.btnQuickAdd).setOnClickListener {
            // Extension function: navigateTo<T>(userName, userEmail)
            navigateTo<CreateNoteActivity>(userName, userEmail)
        }

        setupBottomNav(userName, userEmail)
    }

    private fun setupBottomNav(userName: String, userEmail: String) {
        // Extension function on LinearLayout eliminates the repeated color-filter boilerplate
        val notesTab = findViewById<LinearLayout>(R.id.btnNavNotes)
        val profileTab = findViewById<LinearLayout>(R.id.btnNavProfile)

        notesTab.applyNavTabStyle(R.id.iconNotes, R.id.labelNotes, isActive = false)
        profileTab.applyNavTabStyle(R.id.iconProfile, R.id.labelProfile, isActive = false)

        notesTab.setOnClickListener { navigateTo<NotesActivity>(userName, userEmail) }
        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            showToast("Already on Dashboard")
        }
        profileTab.setOnClickListener { navigateTo<ProfileActivity>(userName, userEmail) }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val userName = getUserName()
        val userEmail = getUserEmail()

        when (item.itemId) {
            R.id.nav_dashboard -> showToast("Already on Dashboard")
            R.id.nav_notes -> navigateTo<NotesActivity>(userName, userEmail)
            R.id.nav_settings -> navigateTo<SettingsActivity>()
            R.id.nav_logout -> navigateAndClearStack<MainActivity>()
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