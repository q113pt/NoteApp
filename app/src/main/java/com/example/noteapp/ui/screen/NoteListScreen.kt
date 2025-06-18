package com.example.noteapp.ui.screen

import android.app.DatePickerDialog
import com.example.noteapp.util.ReminderScheduler
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
    onDeleteNote: (Note) -> Unit,
    onUpdateNote: (Note) -> Unit // ← thêm hàm này để cập nhật note
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
            val now = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hour)
                            calendar.set(Calendar.MINUTE, minute)
                            calendar.set(Calendar.SECOND, 0)
                            calendar.set(Calendar.MILLISECOND, 0)

                            val selectedTime = calendar.timeInMillis
                            reminderTime = selectedTime
                            displayTime = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
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
                        Toast.makeText(context, "Vui lòng chọn thời gian hợp lệ", Toast.LENGTH_SHORT).show()
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
                                text = "Nhắc lúc: " + SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(it)),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        Text(
                            text = "Tạo lúc: " + SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(note.lastModified)),
                            style = MaterialTheme.typography.labelSmall
                        )

                        Row {
                            TextButton(onClick = { onDeleteNote(note) }) {
                                Text("Delete")
                            }

                            TextButton(onClick = {
                                // Huỷ nhắc nhở
                                ReminderScheduler.cancelReminder(context, note.id.hashCode())
                                val updated = note.copy(reminderTime = null)
                                onUpdateNote(updated)
                            }) {
                                Text("Huỷ nhắc")
                            }

                            TextButton(onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, y, m, d ->
                                        cal.set(Calendar.YEAR, y)
                                        cal.set(Calendar.MONTH, m)
                                        cal.set(Calendar.DAY_OF_MONTH, d)

                                        TimePickerDialog(
                                            context,
                                            { _, h, min ->
                                                cal.set(Calendar.HOUR_OF_DAY, h)
                                                cal.set(Calendar.MINUTE, min)
                                                cal.set(Calendar.SECOND, 0)
                                                cal.set(Calendar.MILLISECOND, 0)

                                                val newTime = cal.timeInMillis
                                                if (newTime > System.currentTimeMillis()) {
                                                    val updated = note.copy(reminderTime = newTime)
                                                    onUpdateNote(updated)
                                                    ReminderScheduler.scheduleReminder(
                                                        context,
                                                        note.id.hashCode(),
                                                        note.title,
                                                        newTime
                                                    )
                                                } else {
                                                    Toast.makeText(context, "Thời gian không hợp lệ", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            cal.get(Calendar.HOUR_OF_DAY),
                                            cal.get(Calendar.MINUTE),
                                            true
                                        ).show()
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }) {
                                Text("Chỉnh sửa nhắc")
                            }
                        }
                    }
                }
            }
        }
    }
}
