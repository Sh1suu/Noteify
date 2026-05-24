package edu.cit.gaviola.noteify.feature.notes.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import edu.cit.gaviola.noteify.core.data.AppDatabase
import edu.cit.gaviola.noteify.feature.notes.data.NoteRepository

/**
 * Runs once daily in the background.
 * Permanently purges any trashed note older than 30 days.
 */
class TrashCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val dao = AppDatabase.getDatabase(applicationContext).noteDao()
            val repository = NoteRepository(dao)
            repository.purgeExpiredTrash()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}