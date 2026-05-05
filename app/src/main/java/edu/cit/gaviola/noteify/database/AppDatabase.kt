package edu.cit.gaviola.noteify.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.cit.gaviola.noteify.auth.data.UserDao
import edu.cit.gaviola.noteify.auth.data.UserEntity
import edu.cit.gaviola.noteify.notes.data.NoteDao
import edu.cit.gaviola.noteify.notes.data.NoteEntity

/**
 * Central Room database.
 *
 * Kept in its own `database` package because it is a shared infrastructure
 * concern used by both the `auth` and `notes` feature slices.
 *
 * The singleton is initialized once inside [NoteifyApp.onCreate].
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