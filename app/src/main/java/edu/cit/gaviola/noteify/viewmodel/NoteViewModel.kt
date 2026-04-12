package edu.cit.gaviola.noteify.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.cit.gaviola.noteify.database.AppDatabase
import edu.cit.gaviola.noteify.database.NoteEntity
import edu.cit.gaviola.noteify.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    init {
        val noteDao = AppDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
    }

    fun getNotesByUser(userEmail: String): LiveData<List<NoteEntity>> {
        return repository.getNotesByUser(userEmail)
    }

    fun getNoteCount(userEmail: String): LiveData<Int> {
        return repository.getNoteCount(userEmail)
    }

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
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note)
        }
    }
}