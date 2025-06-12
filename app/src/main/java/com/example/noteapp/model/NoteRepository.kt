package com.example.noteapp.data

import com.example.noteapp.model.Note

class NoteRepository(private val dao: NoteDao) {

    val allNotes = dao.getAllNotes()

    suspend fun insert(note: Note) = dao.insert(note)

    suspend fun update(note: Note) = dao.update(note)

    suspend fun delete(note: Note) = dao.delete(note)

    suspend fun getUnsyncedNotes() = dao.getUnsyncedNotes()

    suspend fun markAsSynced(id: String) = dao.markAsSynced(id)
}
