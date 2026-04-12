package edu.cit.gaviola.noteify

import android.content.Intent
import android.graphics.Color
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
import edu.cit.gaviola.noteify.viewmodel.NoteViewModel

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val userName = intent.getStringExtra("USER_NAME") ?: "Student"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

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

        // Observe real note count from database
        if (userEmail.isNotEmpty()) {
            noteViewModel.getNoteCount(userEmail).observe(this) { count ->
                findViewById<TextView>(R.id.tvTotalNotes).text = count.toString()
            }
        }

        findViewById<Button>(R.id.btnQuickAdd).setOnClickListener {
            val intent = Intent(this, CreateNoteActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        setupBottomNav("home", userName, userEmail)
    }

    private fun setupBottomNav(activeTab: String, userName: String, userEmail: String) {
        val activeColor = Color.parseColor("#9b51e0")
        val inactiveColor = Color.parseColor("#888888")

        findViewById<ImageView>(R.id.iconNotes).setColorFilter(inactiveColor)
        findViewById<TextView>(R.id.labelNotes).setTextColor(inactiveColor)
        findViewById<ImageView>(R.id.iconProfile).setColorFilter(inactiveColor)
        findViewById<TextView>(R.id.labelProfile).setTextColor(inactiveColor)

        findViewById<LinearLayout>(R.id.btnNavNotes).setOnClickListener {
            val intent = Intent(this, NotesActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnNavHome).setOnClickListener {
            Toast.makeText(this, "Already on Dashboard", Toast.LENGTH_SHORT).show()
        }

        findViewById<LinearLayout>(R.id.btnNavProfile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_NAME", userName)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val userName = intent.getStringExtra("USER_NAME") ?: "Student"
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        when (item.itemId) {
            R.id.nav_dashboard -> Toast.makeText(this, "Already on Dashboard", Toast.LENGTH_SHORT).show()
            R.id.nav_notes -> {
                startActivity(Intent(this, NotesActivity::class.java)
                    .putExtra("USER_NAME", userName)
                    .putExtra("USER_EMAIL", userEmail))
            }
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
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