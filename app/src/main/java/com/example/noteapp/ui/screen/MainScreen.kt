package com.example.noteapp.ui.screen

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.noteapp.model.Note
import com.example.noteapp.util.ReminderScheduler
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.viewModel.NoteViewModelFactory

@Composable
fun MainScreen(context: Context) {
    val navController = rememberNavController()
    val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(context.applicationContext as Application))
    val notes by viewModel.allNotes.observeAsState(emptyList())

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "note",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("note") {
                NoteListScreen(
                    notes = notes,
                    onAddNote = { title, content, reminderTime, audioPath, imagePath ->
                        val note = Note(
                            title = title,
                            content = content,
                            reminderTime = reminderTime,
                            audioPath = audioPath,
                            imagePath = imagePath
                        )
                        viewModel.insert(note)

                        ReminderScheduler.scheduleReminder(
                            context = context,
                            noteId = note.id.hashCode(),
                            noteTitle = note.title,
                            triggerAtMillis = reminderTime
                        )
                    },
                    onDeleteNote = { note ->
                        viewModel.delete(note)
                        ReminderScheduler.cancelReminder(
                            context = context,
                            noteId = note.id.hashCode()
                        )
                    },
                    onUpdateNote = { updatedNote ->
                        viewModel.update(updatedNote)
                        updatedNote.reminderTime?.let {
                            ReminderScheduler.scheduleReminder(
                                context = context,
                                noteId = updatedNote.id.hashCode(),
                                noteTitle = updatedNote.title,
                                triggerAtMillis = it
                            )
                        } ?: ReminderScheduler.cancelReminder(
                            context = context,
                            noteId = updatedNote.id.hashCode()
                        )
                    }
                )
            }

            composable("calendar") {
                CalendarScreen(notes = notes)
            }

            composable("profile") {
                ProfileScreen()
            }
        }
    }
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("note", "Ghi chú", Icons.Filled.Edit),
        BottomNavItem("calendar", "Lịch", Icons.Filled.DateRange),
        BottomNavItem("profile", "Cá nhân", Icons.Filled.Person)
    )

    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
