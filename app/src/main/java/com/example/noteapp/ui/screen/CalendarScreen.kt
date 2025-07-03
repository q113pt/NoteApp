package com.example.noteapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.noteapp.model.Note
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(notes: List<Note>) {
    // Chỉ lấy những ghi chú có reminderTime
    val reminderNotes = notes.filter { it.reminderTime != null }
    val groupedByDate = reminderNotes.groupBy {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it.reminderTime!!))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("📅 Lịch nhắc ghi chú", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            groupedByDate.forEach { (date, notesOnDate) ->
                item {
                    Text("🕒 $date", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(notesOnDate) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = note.title, style = MaterialTheme.typography.titleSmall)
                            note.reminderTime?.let {
                                val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Nhắc lúc $timeString", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}
