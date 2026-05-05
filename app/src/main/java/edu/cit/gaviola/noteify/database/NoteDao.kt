package edu.cit.gaviola.noteify.notes.data

import androidx.lifecycle.LiveData
import androidx.room.*

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
}