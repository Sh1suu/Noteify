package edu.cit.gaviola.noteify.notes.data

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    fun getNotesByUser(userEmail: String): LiveData<List<NoteEntity>> =
        noteDao.getNotesByUser(userEmail)

    fun getNoteCount(userEmail: String): LiveData<Int> =
        noteDao.getNoteCount(userEmail)

    suspend fun insertNote(note: NoteEntity) = noteDao.insertNote(note)

    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)

    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    suspend fun getNoteById(noteId: Int): NoteEntity? = noteDao.getNoteById(noteId)
}