package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.model.Note
import com.example.noteapp.ui.screen.NoteListScreen
import com.example.noteapp.ui.theme.NoteAppTheme
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.viewModel.NoteViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(application))
            val notes by viewModel.allNotes.observeAsState(emptyList())

            NoteAppTheme {
                NoteListScreen(
                    notes = notes,
                    onAddNote = { title, content ->
                        val note = Note(title = title, content = content)
                        viewModel.insert(note)
                    },
                    onDeleteNote = { note ->
                        viewModel.delete(note)
                    }
                )
            }
        }
    }
}
