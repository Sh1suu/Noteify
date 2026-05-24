package edu.cit.gaviola.noteify.app

import android.app.Application
import android.util.Log
import androidx.work.*
import edu.cit.gaviola.noteify.core.data.AppDatabase
import edu.cit.gaviola.noteify.feature.notes.worker.TrashCleanupWorker
import java.util.concurrent.TimeUnit

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
        scheduleTrashCleanup()
        Log.d(TAG, "NoteifyApp initialized — database ready.")
    }

    private fun scheduleTrashCleanup() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val request = PeriodicWorkRequestBuilder<TrashCleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "trash_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}