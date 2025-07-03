package com.example.noteapp.ui.component

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.noteapp.model.Note
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditDialog(
    context: Context = LocalContext.current,
    noteToEdit: Note,
    onUpdate: (Note) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = remember { Calendar.getInstance() }

    var title by remember { mutableStateOf(noteToEdit.title) }
    var content by remember { mutableStateOf(noteToEdit.content) }
    var reminderTime by remember { mutableStateOf(noteToEdit.reminderTime) }
    var displayTime by remember {
        mutableStateOf(
            noteToEdit.reminderTime?.let {
                SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(it))
            } ?: "Chưa chọn"
        )
    }
    var imagePath by remember { mutableStateOf(noteToEdit.imagePath) }
    var audioPath by remember { mutableStateOf(noteToEdit.audioPath) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = {
                if (title.isBlank() && content.isBlank()) {
                    Toast.makeText(context, "Tiêu đề hoặc nội dung không được để trống", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val updated = noteToEdit.copy(
                    title = title,
                    content = content,
                    reminderTime = reminderTime,
                    imagePath = imagePath,
                    audioPath = audioPath,
                    lastModified = System.currentTimeMillis()
                )
                onUpdate(updated)
                onDismiss()
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Huỷ") }
        },
        title = { Text("Chỉnh sửa ghi chú") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tiêu đề") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nội dung") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val now = Calendar.getInstance()
                    DatePickerDialog(context, { _, y, m, d ->
                        calendar.set(y, m, d)
                        TimePickerDialog(context, { _, h, min ->
                            calendar.set(Calendar.HOUR_OF_DAY, h)
                            calendar.set(Calendar.MINUTE, min)
                            calendar.set(Calendar.SECOND, 0)
                            reminderTime = calendar.timeInMillis
                            displayTime = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
                    }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
                }) {
                    Text("🕒 Chỉnh thời gian nhắc")
                }

                Text("Nhắc lúc: $displayTime", style = MaterialTheme.typography.labelSmall)

                Spacer(modifier = Modifier.height(8.dp))

                imagePath?.let {
                    Text("Ảnh đính kèm:", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                    TextButton(onClick = { imagePath = null }) {
                        Text("❌ Xoá ảnh", color = MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                audioPath?.let {
                    if (File(it).exists()) {
                        Text("Ghi âm đính kèm:", style = MaterialTheme.typography.labelSmall)
                        Row {
                            Button(onClick = {
                                Toast.makeText(context, "Chức năng phát ghi âm nên gọi từ ngoài.", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("▶️ Phát")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(onClick = {
                                File(it).delete()
                                audioPath = null
                            }) {
                                Text("❌ Xoá ghi âm", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    )
}
