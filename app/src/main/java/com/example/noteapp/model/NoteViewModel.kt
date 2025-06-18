package com.example.noteapp.model

import android.app.Application
import androidx.lifecycle.*
import com.example.noteapp.data.NoteDatabase
import com.example.noteapp.model.Note
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    fun insert(note: Note) = viewModelScope.launch {
        noteDao.insert(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        noteDao.delete(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        noteDao.update(note)
    }
}
