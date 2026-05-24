package edu.cit.gaviola.noteify.core.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.cit.gaviola.noteify.feature.auth.data.UserDao
import edu.cit.gaviola.noteify.feature.notes.data.NoteDao
import edu.cit.gaviola.noteify.core.model.NoteEntity

/**
 * Central Room database.
 * Shared infrastructure used by both the auth and notes feature slices.
 */
@Database(
    entities = [UserEntity::class, NoteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "AppDatabase"
                ).build().also { INSTANCE = it }
            }
        }
    }
}