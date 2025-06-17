package com.example.noteapp.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.noteapp.model.Note
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteListScreen(
    notes: List<Note>,
    onAddNote: (String, String, Long) -> Unit,
    onDeleteNote: (Note) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var displayTime by remember { mutableStateOf("Chưa chọn") }

    val calendar = remember { Calendar.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(text = "My Notes", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            // Chọn ngày trước
            val now = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Sau khi chọn ngày thì mở dialog chọn giờ
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            val selectedTime = calendar.timeInMillis
                            reminderTime = selectedTime
                            displayTime =
                                SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(
                                    calendar.time
                                )
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
            Text("Chọn ngày & giờ nhắc")
        }

        Text("Nhắc lúc: $displayTime", style = MaterialTheme.typography.labelSmall)

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (title.isNotBlank() || content.isNotBlank()) {
                    val time = reminderTime
                    if (time != null && time > System.currentTimeMillis()) {
                        onAddNote(title, content, time)
                        title = ""
                        content = ""
                        reminderTime = null
                        displayTime = "Chưa chọn"
                    } else {
                        Toast.makeText(
                            context,
                            "Vui lòng chọn thời gian hợp lệ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Note")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(notes) { note ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = note.title, style = MaterialTheme.typography.titleMedium)
                        Text(text = note.content)
                        note.reminderTime?.let {
                            Text(
                                text = "Nhắc lúc: " + SimpleDateFormat(
                                    "HH:mm dd/MM/yyyy",
                                    Locale.getDefault()
                                ).format(Date(it)),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Text(
                            text = "Tạo lúc: " + SimpleDateFormat(
                                "HH:mm dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(Date(note.lastModified)),
                            style = MaterialTheme.typography.labelSmall
                        )
                        TextButton(onClick = { onDeleteNote(note) }) {
                            Text("Delete")
                        }
                    }
                }
            }
        }

    }
}
