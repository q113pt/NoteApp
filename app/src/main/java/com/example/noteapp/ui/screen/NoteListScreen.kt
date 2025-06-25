package com.example.noteapp.ui.screen

import android.app.DatePickerDialog
import com.example.noteapp.util.ReminderScheduler
import android.app.TimePickerDialog
<<<<<<< Updated upstream
import android.widget.Toast
=======
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
>>>>>>> Stashed changes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.noteapp.model.Note
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteListScreen(
    notes: List<Note>,
<<<<<<< Updated upstream
    onAddNote: (String, String, Long) -> Unit,
=======
    onAddNote: (String, String, Long, String?, String?) -> Unit,
>>>>>>> Stashed changes
    onDeleteNote: (Note) -> Unit,
    onUpdateNote: (Note) -> Unit // ← thêm hàm này để cập nhật note
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var reminderTime by remember { mutableStateOf<Long?>(null) }
    var displayTime by remember { mutableStateOf("Chưa chọn") }
<<<<<<< Updated upstream

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

=======

    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }
    var isRecording by remember { mutableStateOf(false) }
    var audioFilePath by remember { mutableStateOf<String?>(null) }
    var audioValid by remember { mutableStateOf(false) }

    var imagePath by remember { mutableStateOf<String?>(null) }

    val calendar = remember { Calendar.getInstance() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Cần quyền ghi âm", Toast.LENGTH_SHORT).show()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imagePath = getRealPathFromUri(context, it)
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

    fun playAudio(path: String?) {
        if (path.isNullOrEmpty() || !File(path).exists()) {
            Toast.makeText(context, "File không tồn tại", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(context, "Không phát được: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteAudio() {
        val file = File(audioFilePath ?: return)
        if (file.exists()) file.delete()
        audioFilePath = null
        audioValid = false
        Toast.makeText(context, "Đã xoá ghi âm", Toast.LENGTH_SHORT).show()
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
                Button(onClick = { playAudio(audioFilePath) }) { Text("▶️ Phát") }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { deleteAudio() }) { Text("❌ Xoá") }
            }
        }

>>>>>>> Stashed changes
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val now = Calendar.getInstance()
<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes
        }

        Text("Nhắc lúc: $displayTime", style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(8.dp))

<<<<<<< Updated upstream
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
=======
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
>>>>>>> Stashed changes
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(notes) { note ->
<<<<<<< Updated upstream
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
=======
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(note.title, style = MaterialTheme.typography.titleMedium)
                        Text(note.content)
                        note.imagePath?.let { path ->
                            Image(
                                painter = rememberAsyncImagePainter(model = path),
                                contentDescription = "Ảnh",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                            )
                        }
                        note.reminderTime?.let {
                            Text("Nhắc: ${SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(it))}")
                        }
                        note.audioPath?.let {
                            Text("🎧 Ghi âm", modifier = Modifier.clickable { playAudio(it) })
>>>>>>> Stashed changes
                        }
                        Text(
                            text = "Tạo lúc: " + SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(note.lastModified)),
                            style = MaterialTheme.typography.labelSmall
                        )

<<<<<<< Updated upstream
                        Row {
                            TextButton(onClick = { onDeleteNote(note) }) {
                                Text("Delete")
                            }

=======
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { onDeleteNote(note) }) { Text("🗑 Xoá") }
>>>>>>> Stashed changes
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

// Helper để lấy đường dẫn thật từ Uri
fun getRealPathFromUri(context: Context, uri: Uri): String? {
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        if (cursor.moveToFirst()) {
            return cursor.getString(columnIndex)
        }
    }
    return uri.path
}
