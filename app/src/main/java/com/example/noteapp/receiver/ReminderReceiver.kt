package com.example.noteapp.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.noteapp.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderReceiver", "onReceive triggered")
        Toast.makeText(context, "Đã nhận nhắc nhở", Toast.LENGTH_LONG).show()

        val noteTitle = intent.getStringExtra("noteTitle") ?: "Note Reminder"
        val noteId = intent.getIntExtra("noteId", 0)

        val notification = NotificationCompat.Builder(context, "note_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reminder")
            .setContentText(noteTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        with(NotificationManagerCompat.from(context)) {
            notify(noteId, notification)
        }
    }
}
