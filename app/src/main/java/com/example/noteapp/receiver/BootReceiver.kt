package com.example.noteapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.noteapp.data.NoteDatabase
import com.example.noteapp.util.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val noteDao = NoteDatabase.getDatabase(context).noteDao()
                    val notesWithReminder = noteDao.getNotesWithReminder()

                    for (note in notesWithReminder) {
                        ReminderScheduler.scheduleReminder(
                            context = context,
                            noteId = note.title.hashCode(),
                            noteTitle = note.title,
                            triggerAtMillis = note.reminderTime!!
                        )
                    }

                    Log.d("BootReceiver", "All reminders restored")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Failed to restore reminders", e)
                }
            }
        }
    }
}
