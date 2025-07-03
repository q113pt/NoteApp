package com.example.noteapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    // Giả lập trạng thái đăng nhập
    var isLoggedIn by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("Người dùng") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👤 Cá nhân", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        if (isLoggedIn) {
            Text("Xin chào, $username!", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            Text("🔵 Bạn đang ở chế độ **online**", style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                isLoggedIn = false
                username = "Người dùng"
            }) {
                Text("Đăng xuất")
            }
        } else {
            Text("⚪ Bạn đang ở chế độ **offline**", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                isLoggedIn = true
                username = "UserDemo"
            }) {
                Text("Đăng nhập")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                isLoggedIn = true
                username = "UserMới"
            }) {
                Text("Đăng ký")
            }
        }
    }
}
