package com.example.noteapp

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.noteapp.model.Note
import com.example.noteapp.ui.screen.NoteListScreen
import com.example.noteapp.ui.theme.NoteAppTheme
import com.example.noteapp.util.ReminderScheduler
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.viewModel.NoteViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        requestExactAlarmPermissionIfNeeded()

        setContent {
            val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(application))
            val notes by viewModel.allNotes.observeAsState(emptyList())

            NoteAppTheme {
                NoteListScreen(
                    notes = notes,
                    onAddNote = { title, content, reminderTime ->
                        val note = Note(
                            title = title,
                            content = content,
                            reminderTime = reminderTime
                        )
                        viewModel.insert(note)

                        ReminderScheduler.scheduleReminder(
                            context = this,
                            noteId = note.id.hashCode(),
                            noteTitle = note.title,
                            triggerAtMillis = reminderTime
                        )
                    },
                    onDeleteNote = { note ->
                        viewModel.delete(note)
                        ReminderScheduler.cancelReminder(
                            context = this,
                            noteId = note.id.hashCode()
                        )
                    },
                    onUpdateNote = { updatedNote ->
                        viewModel.update(updatedNote)
                        updatedNote.reminderTime?.let {
                            ReminderScheduler.scheduleReminder(
                                context = this,
                                noteId = updatedNote.id.hashCode(),
                                noteTitle = updatedNote.title,
                                triggerAtMillis = it
                            )
                        } ?: ReminderScheduler.cancelReminder(
                            context = this,
                            noteId = updatedNote.id.hashCode()
                        )
                    }
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "note_channel"
            val channelName = "Note Reminder"
            val descriptionText = "Channel for note reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(AlarmManager::class.java)
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }
}
