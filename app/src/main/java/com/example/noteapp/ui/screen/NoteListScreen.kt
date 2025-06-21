package com.example.noteapp.ui.screen

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.noteapp.model.Note
import com.example.noteapp.util.ReminderScheduler
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteListScreen(
    notes: List<Note>,
    onAddNote: (String, String, Long, String?) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onUpdateNote: (Note) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var displayTime by remember { mutableStateOf("Chưa chọn") }
    var isRecording by remember { mutableStateOf(false) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    val calendar = remember { Calendar.getInstance() }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            Toast.makeText(context, "Cần quyền ghi âm", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    fun startRecording() {
        try {
            val fileName = "note_${System.currentTimeMillis()}.3gp"
            val output = File(context.filesDir, fileName)
            audioFilePath = output.absolutePath
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(audioFilePath)
                prepare()
                start()
            }
            isRecording = true
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi bắt đầu ghi âm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi dừng ghi âm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun playAudio(path: String?) {
        if (path.isNullOrEmpty() || !File(path).exists()) {
            Toast.makeText(context, "Không tìm thấy file ghi âm", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi phát ghi âm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Notes", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                if (!isRecording) startRecording() else stopRecording()
            }) {
                Text(if (isRecording) "⏹ Dừng ghi" else " Ghi âm")
            }

            Button(onClick = { playAudio(audioFilePath) }) {
                Text("▶ Nghe lại")
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            val now = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d ->
                calendar.set(Calendar.YEAR, y)
                calendar.set(Calendar.MONTH, m)
                calendar.set(Calendar.DAY_OF_MONTH, d)
                TimePickerDialog(context, { _, h, min ->
                    calendar.set(Calendar.HOUR_OF_DAY, h)
                    calendar.set(Calendar.MINUTE, min)
                    reminderTime = calendar.timeInMillis
                    displayTime = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show()
            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show()
        }) {
            Text("Chọn nhắc nhở")
        }

        Text("Nhắc lúc: $displayTime", style = MaterialTheme.typography.labelSmall)

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (title.isNotBlank() || content.isNotBlank() || audioFilePath != null) {
                onAddNote(title, content, reminderTime ?: System.currentTimeMillis(), audioFilePath)
                title = ""
                content = ""
                reminderTime = null
                displayTime = "Chưa chọn"
                audioFilePath = null
            } else {
                Toast.makeText(context, "Không có nội dung", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Thêm ghi chú")
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(notes) { note ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text(note.title, style = MaterialTheme.typography.titleMedium)
                        Text(note.content)
                        note.reminderTime?.let {
                            Text("Nhắc lúc: ${SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(it))}", style = MaterialTheme.typography.labelSmall)
                        }

                        if (!note.audioPath.isNullOrEmpty()) {
                            Text("▶ Nghe ghi âm", modifier = Modifier.clickable {
                                playAudio(note.audioPath)
                            }, style = MaterialTheme.typography.labelSmall)
                        }

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = { onDeleteNote(note) }) { Text("Xoá") }
                            TextButton(onClick = {
                                ReminderScheduler.cancelReminder(context, note.id.hashCode())
                                onUpdateNote(note.copy(reminderTime = null))
                            }) { Text("Huỷ nhắc") }
                        }
                    }
                }
            }
        }
    }
}
