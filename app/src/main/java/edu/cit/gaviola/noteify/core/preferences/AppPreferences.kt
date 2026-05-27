package edu.cit.gaviola.noteify.core.preferences

import android.content.Context
import android.content.SharedPreferences

/**
 * Single source of truth for all SharedPreferences in Noteify.
 * Covers settings toggles and per-user profile fields (course, year, profile image path).
 */
class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ── Settings ────────────────────────────────────────────────────────────

    var notificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS, value).apply()

    var darkModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    // ── Profile — per-user fields keyed by email ─────────────────────────────

    fun getCourse(email: String): String =
        prefs.getString(profileKey(email, KEY_COURSE), "") ?: ""

    fun setCourse(email: String, value: String) =
        prefs.edit().putString(profileKey(email, KEY_COURSE), value).apply()

    fun getYear(email: String): String =
        prefs.getString(profileKey(email, KEY_YEAR), "") ?: ""

    fun setYear(email: String, value: String) =
        prefs.edit().putString(profileKey(email, KEY_YEAR), value).apply()

    /** Absolute path to the saved profile JPEG inside internal storage, or "" if not set. */
    fun getProfileImagePath(email: String): String =
        prefs.getString(profileKey(email, KEY_PROFILE_IMAGE), "") ?: ""

    fun setProfileImagePath(email: String, path: String) =
        prefs.edit().putString(profileKey(email, KEY_PROFILE_IMAGE), path).apply()

    private fun profileKey(email: String, field: String) = "profile_${email}_$field"

    companion object {
        private const val PREFS_NAME          = "noteify_prefs"
        private const val KEY_NOTIFICATIONS   = "notifications_enabled"
        private const val KEY_DARK_MODE       = "dark_mode_enabled"
        private const val KEY_COURSE          = "course"
        private const val KEY_YEAR            = "year"
        private const val KEY_PROFILE_IMAGE   = "profile_image_path"
    }
}