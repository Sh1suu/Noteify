package edu.cit.gaviola.noteify.feature.notes.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.cit.gaviola.noteify.core.data.AppDatabase
import edu.cit.gaviola.noteify.core.model.NoteEntity
import edu.cit.gaviola.noteify.feature.notes.data.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
    }

    fun getNotesByUser(userEmail: String): LiveData<List<NoteEntity>> =
        repository.getNotesByUser(userEmail)

    fun getNoteCount(userEmail: String): LiveData<Int> =
        repository.getNoteCount(userEmail)

    fun getRecentNotes(userEmail: String): LiveData<List<NoteEntity>> =
        repository.getRecentNotes(userEmail)

    fun saveNote(
        title: String,
        content: String,
        subject: String,
        isImportant: Boolean,
        userEmail: String
    ) {
        viewModelScope.launch {
            repository.insertNote(
                NoteEntity(
                    title = title,
                    content = content,
                    subject = subject,
                    isImportant = isImportant,
                    userEmail = userEmail
                )
            )
            _saveResult.postValue(true)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch { repository.deleteNote(note) }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch { repository.updateNote(note) }
    }
}