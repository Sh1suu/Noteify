package edu.cit.gaviola.noteify.feature.notes.data

import androidx.lifecycle.LiveData
import androidx.room.*
import edu.cit.gaviola.noteify.core.model.NoteEntity

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    /** Active (non-deleted) notes for this user, newest first. */
    @Query("SELECT * FROM notes WHERE userEmail = :userEmail AND deletedAt IS NULL ORDER BY timestamp DESC")
    fun getNotesByUser(userEmail: String): LiveData<List<NoteEntity>>

    /** Count of active notes only. */
    @Query("SELECT COUNT(*) FROM notes WHERE userEmail = :userEmail AND deletedAt IS NULL")
    fun getNoteCount(userEmail: String): LiveData<Int>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): NoteEntity?

    /** 5 most recent ACTIVE notes for the dashboard feed. */
    @Query("SELECT * FROM notes WHERE userEmail = :userEmail AND deletedAt IS NULL ORDER BY timestamp DESC LIMIT 5")
    fun getRecentNotes(userEmail: String): LiveData<List<NoteEntity>>

    /** All active notes grouped by subject — Room returns flat list, grouping done in ViewModel. */
    @Query("SELECT * FROM notes WHERE userEmail = :userEmail AND deletedAt IS NULL ORDER BY subject ASC, timestamp DESC")
    fun getNotesByUserGroupedBySubject(userEmail: String): LiveData<List<NoteEntity>>

    // ── Trash ────────────────────────────────────────────────────────────────

    /** Soft-delete: stamp deletedAt timestamp. */
    @Query("UPDATE notes SET deletedAt = :deletedAt WHERE id = :noteId")
    suspend fun softDelete(noteId: Int, deletedAt: Long)

    /** Restore: clear deletedAt. */
    @Query("UPDATE notes SET deletedAt = NULL WHERE id = :noteId")
    suspend fun restoreNote(noteId: Int)

    /** All trashed notes for this user. */
    @Query("SELECT * FROM notes WHERE userEmail = :userEmail AND deletedAt IS NOT NULL ORDER BY deletedAt DESC")
    fun getTrashedNotes(userEmail: String): LiveData<List<NoteEntity>>

    /** Permanently delete all notes trashed before the given cutoff (for 30-day auto-delete). */
    @Query("DELETE FROM notes WHERE deletedAt IS NOT NULL AND deletedAt < :cutoff")
    suspend fun purgeExpiredTrash(cutoff: Long)

    /** Permanently delete all trash for a user immediately (Empty Trash). */
    @Query("DELETE FROM notes WHERE userEmail = :userEmail AND deletedAt IS NOT NULL")
    suspend fun emptyTrash(userEmail: String)

    /** All trashed notes across all users (for background worker). */
    @Query("DELETE FROM notes WHERE deletedAt IS NOT NULL AND deletedAt < :cutoff")
    suspend fun purgeExpiredGlobally(cutoff: Long)
}