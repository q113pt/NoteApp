package com.example.noteapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.noteapp.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val noteTitle = intent.getStringExtra("noteTitle") ?: "Note Reminder"
        val noteId = intent.getIntExtra("noteId", 0)

        Log.d("ReminderReceiver", "Triggered for noteId=$noteId, title=$noteTitle")

        val notification = NotificationCompat.Builder(context, "note_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Nhắc nhở")
            .setContentText(noteTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(noteId, notification)
    }
}
