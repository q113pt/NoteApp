package com.example.noteapp.ui.screen

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.noteapp.model.Note
import com.example.noteapp.ui.component.NoteEditDialog
import com.example.noteapp.ui.component.NoteItem
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    notes: List<Note>,
    onAddNote: (String, String, Long, String?, String?) -> Unit,
    onDeleteNote: (Note) -> Unit,
    onUpdateNote: (Note) -> Unit,
    onNoteClick: (Note) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var displayTime by remember { mutableStateOf("Chưa chọn") }

    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var audioValid by remember { mutableStateOf(false) }

    var imagePath by remember { mutableStateOf<String?>(null) }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Cần quyền ghi âm", Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val fileName = "image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            inputStream?.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
            imagePath = file.absolutePath
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    fun startRecording() {
        if (audioFilePath != null && audioValid) {
            Toast.makeText(context, "Đã có ghi âm. Xoá trước khi ghi mới.", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val fileName = "record_${System.currentTimeMillis()}.m4a"
            val file = File(context.filesDir, fileName)

            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            audioFilePath = file.absolutePath
            isRecording = true
            audioValid = false
        } catch (e: Exception) {
            recorder = null
            isRecording = false
            audioFilePath = null
            Toast.makeText(context, "Lỗi ghi âm: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.release()
            recorder = null
            isRecording = false

            val file = File(audioFilePath ?: "")
            if (file.exists() && file.length() > 0) {
                audioValid = true
            } else {
                Toast.makeText(context, "Ghi âm không thành công", Toast.LENGTH_SHORT).show()
                audioFilePath = null
            }
        } catch (e: Exception) {
            recorder = null
            isRecording = false
            audioFilePath = null
            Toast.makeText(context, "Lỗi dừng ghi: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("NoteApp", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tiêu đề") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Nội dung") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("🖼 Chọn ảnh")
        }

        imagePath?.let {
            Text("Ảnh: $it", style = MaterialTheme.typography.labelSmall)
        }

        Row {
            Button(onClick = { if (!isRecording) startRecording() else stopRecording() }) {
                Text(if (isRecording) "⏹ Dừng ghi" else "🎙 Ghi âm")
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (audioValid && !isRecording) {
                Button(onClick = {
                    val path = audioFilePath
                    if (!path.isNullOrEmpty() && File(path).exists()) {
                        player?.release()
                        player = MediaPlayer().apply {
                            setDataSource(path)
                            prepare()
                            start()
                        }
                    }
                }) { Text("▶️ Phát") }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    val file = audioFilePath?.let { File(it) }
                    if (file != null && file.exists()) {
                        file.delete()
                        audioFilePath = null
                        audioValid = false
                        Toast.makeText(context, "Đã xoá ghi âm", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("❌ Xoá")
                }
            }
        }

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
            Text("Chọn nhắc")
        }

        Text("Nhắc lúc: $displayTime", style = MaterialTheme.typography.labelSmall)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (title.isNotBlank() || content.isNotBlank() || audioValid || imagePath != null) {
                onAddNote(title, content, reminderTime ?: System.currentTimeMillis(), audioFilePath, imagePath)
                title = ""
                content = ""
                reminderTime = null
                displayTime = "Chưa chọn"
                audioFilePath = null
                imagePath = null
                audioValid = false
            } else {
                Toast.makeText(context, "Chưa nhập nội dung", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("➕ Thêm ghi chú")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onDelete = onDeleteNote,
                    onUpdate = { editingNote = it },
                    onClick = { onNoteClick(note) },
                    onPlayAudio = { path ->
                        if (!path.isNullOrEmpty() && File(path).exists()) {
                            player?.release()
                            player = MediaPlayer().apply {
                                setDataSource(path)
                                prepare()
                                start()
                            }
                        } else {
                            Toast.makeText(context, "Không tìm thấy file ghi âm", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }

    editingNote?.let {
        NoteEditDialog(
            context = context,
            noteToEdit = it,
            onUpdate = {
                onUpdateNote(it)
                editingNote = null
            },
            onDismiss = {
                editingNote = null
            }
        )
    }
}
