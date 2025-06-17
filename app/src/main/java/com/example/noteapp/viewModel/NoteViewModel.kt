package com.example.noteapp.viewModel

import android.app.Application
import androidx.lifecycle.*
import com.example.noteapp.data.NoteDatabase
import com.example.noteapp.data.NoteRepository
import com.example.noteapp.model.Note
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    private val repository = NoteRepository(noteDao)

    val allNotes: LiveData<List<Note>> = repository.allNotes

    fun insert(note: Note) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun delete(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }

    fun update(note: Note) {
        viewModelScope.launch {
            repository.update(note)
        }
    }
}
