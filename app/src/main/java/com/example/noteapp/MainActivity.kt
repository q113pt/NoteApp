package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.model.Note
import com.example.noteapp.ui.screen.NoteListScreen
import com.example.noteapp.ui.theme.NoteAppTheme
import com.example.noteapp.util.ReminderScheduler
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.viewModel.NoteViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tạo NotificationChannel nếu chưa có (Android 8+)
        createNotificationChannel()

        setContent {
            val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(application))
            val notes by viewModel.allNotes.observeAsState(emptyList())

            NoteAppTheme {
                NoteListScreen(
                    notes = notes,
                    onAddNote = { title, content, reminderTime ->
                        val note = Note(title = title, content = content)
                        viewModel.insert(note)

                        // Lập lịch nhắc nếu có thời gian hợp lệ
                        ReminderScheduler.scheduleReminder(
                            context = this,
                            noteId = note.hashCode(), // dùng hashCode hoặc note.id nếu có
                            noteTitle = note.title,
                            triggerAtMillis = reminderTime
                        )
                    },
                    onDeleteNote = { note ->
                        viewModel.delete(note)
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = android.app.NotificationChannel(
            "note_channel",
            "Note Reminder",
            android.app.NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(android.app.NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}
