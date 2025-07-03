package com.example.noteapp.ui.screen

import android.media.MediaPlayer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.noteapp.model.Note
import com.example.noteapp.ui.component.NoteEditDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: Note,
    onUpdate: (Note) -> Unit,
    onDelete: (Note) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var player by remember { mutableStateOf<MediaPlayer?>(null) }

    fun playAudio(path: String?) {
        if (path.isNullOrBlank() || !File(path).exists()) return
        try {
            player?.release()
            player = MediaPlayer().apply {
                setDataSource(path)
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player?.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết ghi chú") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trở về")

                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Chỉnh sửa")
                    }
                    IconButton(onClick = {
                        onDelete(note)
                        onBack()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Xoá")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(note.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            if (note.content.isNotBlank()) {
                Text(note.content, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))
            }

            note.imagePath?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Ảnh ghi chú",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            note.audioPath?.let {
                Button(onClick = { playAudio(it) }) {
                    Text("▶️ Phát ghi âm")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            note.reminderTime?.let {
                val timeStr = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                Text("Nhắc lúc: $timeStr", style = MaterialTheme.typography.labelMedium)
            }

            val createdAt = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(note.lastModified))
            Text("Tạo lúc: $createdAt", style = MaterialTheme.typography.labelSmall)
        }
    }

    if (showEditDialog) {
        NoteEditDialog(
            context = context,
            noteToEdit = note,
            onUpdate = {
                onUpdate(it)
                showEditDialog = false
            },
            onDismiss = {
                showEditDialog = false
            }
        )
    }
}
