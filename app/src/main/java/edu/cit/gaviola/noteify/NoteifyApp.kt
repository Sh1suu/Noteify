package edu.cit.gaviola.noteify

import android.app.Application
import android.util.Log
import edu.cit.gaviola.noteify.database.AppDatabase

/**
 * Custom Application class for Noteify.
 *
 * Responsibilities:
 * - Initializes the Room database singleton at app startup
 * - Provides a global application-level logger tag
 * - Serves as the single source of truth for app-wide dependencies
 *
 * Registered in AndroidManifest.xml via android:name=".NoteifyApp"
 */
class NoteifyApp : Application() {

    companion object {
        const val TAG = "NoteifyApp"

        /** Lazily-held reference so other components can access the db singleton if needed. */
        lateinit var instance: NoteifyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Eagerly initialize the database singleton so the first DB call is not delayed.
        AppDatabase.getDatabase(this)

        Log.d(TAG, "NoteifyApp initialized — database ready.")
    }
}