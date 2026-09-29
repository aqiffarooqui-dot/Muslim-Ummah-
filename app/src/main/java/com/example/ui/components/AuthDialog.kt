package com.example.ui.components

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.viewmodel.MuslimUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    uiState: MuslimUiState,
    onDismiss: () -> Unit,
    onSignInWithGoogle: (Activity) -> Unit,
    onSignInWithEmail: (email: String, name: String?) -> Unit,
    onSignOut: () -> Unit,
    onLoginAsAdmin: () -> Unit
) {
    val context = LocalContext.current
    var googleEmailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var showManualInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Google Account Login",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_dialog")
            ) {
                // Current User Status Card
                val user = uiState.currentUser
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (uiState.isAdmin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (uiState.isAdmin) Color(0xFFFFD54F) else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (uiState.isAdmin) "👑" else (user?.displayName?.take(1) ?: "U"),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isAdmin) Color(0xFF183830) else Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user?.displayName ?: "Guest User",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (uiState.isAdmin) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = Color(0xFFFFD54F)
                                        ) {
                                            Text(
                                                text = "ADMIN",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = Color(0xFF183830),
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = user?.email ?: "Not signed in",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Subscription: ${user?.planType ?: "Free"}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isPremium) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (user != null) {
                                TextButton(onClick = onSignOut) {
                                    Text("Sign Out", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary One-Tap Google Sign-In with Credential Manager
                Button(
                    onClick = {
                        if (context is Activity) {
                            onSignInWithGoogle(context)
                        } else {
                            onLoginAsAdmin()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("google_one_tap_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "G", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF4285F4))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sign in with Google",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Admin Account Direct Fast-Login Button
                Button(
                    onClick = {
                        onLoginAsAdmin()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("direct_admin_login_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "👑", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sign in as aqiffarooqui@gmail.com",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Manual Google Account Email Sign-In Option
                OutlinedButton(
                    onClick = { showManualInput = !showManualInput },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Mail, contentDescription = "Email", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (showManualInput) "Hide Email Form" else "Sign in with another Google Email")
                }

                if (showManualInput) {
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = googleEmailInput,
                        onValueChange = { googleEmailInput = it },
                        label = { Text("Your Google Account Email") },
                        placeholder = { Text("name@gmail.com") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Your Name (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (googleEmailInput.isNotBlank()) {
                                onSignInWithEmail(googleEmailInput.trim(), nameInput.trim())
                                onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Authenticate Google Email")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
