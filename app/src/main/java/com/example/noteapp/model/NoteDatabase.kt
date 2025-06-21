package com.example.noteapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.noteapp.model.Note

@Database(entities = [Note::class], version = 2, exportSchema = false) // ⚠️ nhớ cập nhật version mỗi khi thay đổi schema
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .fallbackToDestructiveMigration() // ❗️tự động xoá DB nếu schema đổi, tránh crash
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
