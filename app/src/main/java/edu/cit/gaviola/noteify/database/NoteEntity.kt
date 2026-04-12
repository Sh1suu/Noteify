package edu.cit.gaviola.noteify.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val subject: String,
    val isImportant: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val userEmail: String
)