package com.example.noteapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val imageUri: String? = null,
    val voiceUri: String? = null,
    val reminderTime: Long? = null,
    val lastModified: Long = System.currentTimeMillis(),
    val reminderTime: Long? = null,
    val isSynced: Boolean = false,
    val userId: String = ""
)
