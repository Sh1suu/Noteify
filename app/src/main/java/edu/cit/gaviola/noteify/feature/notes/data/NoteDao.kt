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

    @Query("SELECT * FROM notes WHERE userEmail = :userEmail ORDER BY timestamp DESC")
    fun getNotesByUser(userEmail: String): LiveData<List<NoteEntity>>

    @Query("SELECT COUNT(*) FROM notes WHERE userEmail = :userEmail")
    fun getNoteCount(userEmail: String): LiveData<Int>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): NoteEntity?

    /** Returns the 5 most recent notes for the dashboard activity feed. */
    @Query("SELECT * FROM notes WHERE userEmail = :userEmail ORDER BY timestamp DESC LIMIT 5")
    fun getRecentNotes(userEmail: String): LiveData<List<NoteEntity>>
}