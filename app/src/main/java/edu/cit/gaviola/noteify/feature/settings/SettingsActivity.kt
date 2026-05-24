package edu.cit.gaviola.noteify.feature.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import edu.cit.gaviola.noteify.R
import edu.cit.gaviola.noteify.core.extensions.showToast
import edu.cit.gaviola.noteify.core.preferences.AppPreferences

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = AppPreferences(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // ── Notifications toggle ────────────────────────────────────────────
        val switchNotifications = findViewById<Switch>(R.id.switchNotifications)
        switchNotifications.isChecked = prefs.notificationsEnabled
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefs.notificationsEnabled = isChecked
            val msg = if (isChecked)
                getString(R.string.text_notifications_on)
            else
                getString(R.string.text_notifications_off)
            showToast(msg)
        }

        // ── Dark mode toggle ────────────────────────────────────────────────
        val switchDarkMode = findViewById<Switch>(R.id.switchDarkMode)
        switchDarkMode.isChecked = prefs.darkModeEnabled
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.darkModeEnabled = isChecked
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // ── Account Information ─────────────────────────────────────────────
        findViewById<CardView>(R.id.cardAccountInfo).setOnClickListener {
            showToast(getString(R.string.text_account_info_coming_soon))
        }

        // ── Storage ─────────────────────────────────────────────────────────
        findViewById<CardView>(R.id.cardStorage).setOnClickListener {
            startActivity(Intent(this, StorageActivity::class.java))
        }

        // ── Privacy ─────────────────────────────────────────────────────────
        findViewById<CardView>(R.id.cardPrivacy).setOnClickListener {
            showToast(getString(R.string.text_privacy_coming_soon))
        }

        // ── Help ────────────────────────────────────────────────────────────
        findViewById<CardView>(R.id.cardHelp).setOnClickListener {
            showToast(getString(R.string.text_help_coming_soon))
        }

        // ── About ───────────────────────────────────────────────────────────
        findViewById<CardView>(R.id.cardAbout).setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showAboutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.label_about))
            .setMessage(getString(R.string.text_about_content))
            .setPositiveButton(getString(R.string.btn_ok), null)
            .show()
    }
}