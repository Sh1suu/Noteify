package edu.cit.gaviola.noteify.feature.notes.data

import androidx.lifecycle.LiveData
import edu.cit.gaviola.noteify.core.model.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {

    fun getNotesByUser(userEmail: String): LiveData<List<NoteEntity>> =
        noteDao.getNotesByUser(userEmail)

    fun getNoteCount(userEmail: String): LiveData<Int> =
        noteDao.getNoteCount(userEmail)

    fun getRecentNotes(userEmail: String): LiveData<List<NoteEntity>> =
        noteDao.getRecentNotes(userEmail)

    fun getNotesByUserGroupedBySubject(userEmail: String): LiveData<List<NoteEntity>> =
        noteDao.getNotesByUserGroupedBySubject(userEmail)

    fun getTrashedNotes(userEmail: String): LiveData<List<NoteEntity>> =
        noteDao.getTrashedNotes(userEmail)

    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    /** Soft-delete: move to trash. */
    suspend fun softDelete(noteId: Int) = noteDao.softDelete(noteId, System.currentTimeMillis())

    /** Restore from trash. */
    suspend fun restoreNote(noteId: Int) = noteDao.restoreNote(noteId)

    /** Permanent delete (used by hard-delete paths and worker). */
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun emptyTrash(userEmail: String) = noteDao.emptyTrash(userEmail)

    suspend fun purgeExpiredTrash() {
        val cutoff = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        noteDao.purgeExpiredGlobally(cutoff)
    }

    suspend fun getNoteById(noteId: Int): NoteEntity? = noteDao.getNoteById(noteId)
}