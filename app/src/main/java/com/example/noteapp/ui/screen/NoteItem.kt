package com.example.noteapp.ui.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.noteapp.model.Note
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    onDelete: (Note) -> Unit,
    onUpdate: (Note) -> Unit,
    onPlayAudio: (String) -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (note.content.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!note.imagePath.isNullOrBlank()) {
                    Icon(Icons.Default.Image, contentDescription = "Có ảnh")
                }
                if (!note.audioPath.isNullOrBlank() && File(note.audioPath).exists()) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Ghi âm",
                        modifier = Modifier.clickable {
                            onPlayAudio(note.audioPath!!)
                        }
                    )
                }
            }

            note.reminderTime?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nhắc: ${
                        SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                    }",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { showEditDialog = true }) {
                    Text("✏️ Sửa")
                }

                TextButton(onClick = { onDelete(note) }) {
                    Text("🗑 Xoá")
                }
            }
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
