package edu.cit.gaviola.noteify.repository

import androidx.lifecycle.LiveData
import edu.cit.gaviola.noteify.database.NoteDao
import edu.cit.gaviola.noteify.database.NoteEntity

class NoteRepository(private val noteDao: NoteDao) {

    fun getNotesByUser(userEmail: String): LiveData<List<NoteEntity>> {
        return noteDao.getNotesByUser(userEmail)
    }

    fun getNoteCount(userEmail: String): LiveData<Int> {
        return noteDao.getNoteCount(userEmail)
    }

    suspend fun insertNote(note: NoteEntity) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    suspend fun getNoteById(noteId: Int): NoteEntity? {
        return noteDao.getNoteById(noteId)
    }
}