package edu.cit.gaviola.noteify.app

import android.app.Application
import android.util.Log
import edu.cit.gaviola.noteify.core.data.AppDatabase

/**
 * Custom Application class for Noteify.
 * Registered in AndroidManifest.xml via android:name=".app.NoteifyApp"
 */
class NoteifyApp : Application() {

    companion object {
        const val TAG = "NoteifyApp"

        lateinit var instance: NoteifyApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppDatabase.getDatabase(this)
        Log.d(TAG, "NoteifyApp initialized — database ready.")
    }
}