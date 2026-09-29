package com.example.ui.screens

import android.app.Activity
import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldSecondary

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onSignInWithEmail: (email: String, password: String) -> Unit,
    onSignUpWithEmail: (email: String, password: String, displayName: String) -> Unit,
    onSendPasswordReset: (email: String, onResult: (Boolean, String) -> Unit) -> Unit,
    onGoogleSignInClick: (Activity) -> Unit,
    onDismissError: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val focusManager = LocalFocusManager.current

    // Toggle between Login ("Sign In") and Signup ("Create Account")
    var isSignUpMode by remember { mutableStateOf(false) }

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var localEmailError by remember { mutableStateOf<String?>(null) }
    var localPasswordError by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    // Mandatory Login: Back button cannot bypass login
    BackHandler(enabled = true) {
        if (isSignUpMode) {
            isSignUpMode = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF041810),
                        Color(0xFF0B2E21),
                        Color(0xFF061E15)
                    )
                )
            )
            .testTag("mandatory_login_screen")
    ) {
        // Decorative background geometric accents
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0x12D4AF37),
                radius = size.width * 0.45f,
                center = Offset(size.width * 0.9f, size.height * 0.12f)
            )
            drawCircle(
                color = Color(0x0E1B5E20),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.1f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header: Islamic Crest & App Branding
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.25f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(GoldSecondary, EmeraldPrimary))),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(40.dp)) {
                            val path = Path().apply {
                                moveTo(size.width * 0.7f, size.height * 0.15f)
                                cubicTo(
                                    size.width * 0.2f, size.height * 0.25f,
                                    size.width * 0.2f, size.height * 0.75f,
                                    size.width * 0.7f, size.height * 0.85f
                                )
                                cubicTo(
                                    size.width * 0.4f, size.height * 0.75f,
                                    size.width * 0.4f, size.height * 0.25f,
                                    size.width * 0.7f, size.height * 0.15f
                                )
                                close()
                            }
                            drawPath(path, color = GoldSecondary)
                            drawCircle(
                                color = GoldSecondary,
                                radius = size.width * 0.08f,
                                center = Offset(size.width * 0.68f, size.height * 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Muslim Ummah",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "أمة المسلمين",
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = FontFamily.Serif,
                    color = GoldSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = if (isSignUpMode) "Create your account to sync your journey" else "Sign in to access your Quran, Hadith & Prayer data",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFCFD8DC),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, start = 16.dp, end = 16.dp)
                )
            }

            // Error Alert Banner
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = onDismissError,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Form Card
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F3627).copy(alpha = 0.85f)
                ),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color(0x33D4AF37), Color(0x221B5E20)))),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Full Name (Signup mode only)
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name", color = Color(0xFFCFD8DC)) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = "Name", tint = GoldSecondary)
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GoldSecondary,
                                unfocusedBorderColor = Color(0x55D4AF37),
                                focusedContainerColor = Color(0x20000000),
                                unfocusedContainerColor = Color(0x20000000)
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input")
                        )
                    }

                    // Email Field
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = {
                            emailInput = it
                            if (localEmailError != null) localEmailError = null
                        },
                        label = { Text("Email Address", color = Color(0xFFCFD8DC)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = GoldSecondary)
                        },
                        isError = localEmailError != null,
                        supportingText = {
                            if (localEmailError != null) {
                                Text(text = localEmailError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldSecondary,
                            unfocusedBorderColor = Color(0x55D4AF37),
                            focusedContainerColor = Color(0x20000000),
                            unfocusedContainerColor = Color(0x20000000)
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input")
                    )

                    // Password Field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            if (localPasswordError != null) localPasswordError = null
                        },
                        label = { Text("Password", color = Color(0xFFCFD8DC)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password", tint = GoldSecondary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFFCFD8DC)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = localPasswordError != null,
                        supportingText = {
                            if (localPasswordError != null) {
                                Text(text = localPasswordError!!, color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = GoldSecondary,
                            unfocusedBorderColor = Color(0x55D4AF37),
                            focusedContainerColor = Color(0x20000000),
                            unfocusedContainerColor = Color(0x20000000)
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                submitForm(
                                    isSignUp = isSignUpMode,
                                    email = emailInput,
                                    password = passwordInput,
                                    name = nameInput,
                                    onEmailError = { localEmailError = it },
                                    onPasswordError = { localPasswordError = it },
                                    onSignIn = onSignInWithEmail,
                                    onSignUp = onSignUpWithEmail
                                )
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input")
                    )

                    // Forgot Password Link (Login mode only)
                    if (!isSignUpMode) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = GoldSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Main Action Button (Sign In / Create Account)
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            submitForm(
                                isSignUp = isSignUpMode,
                                email = emailInput,
                                password = passwordInput,
                                name = nameInput,
                                onEmailError = { localEmailError = it },
                                onPasswordError = { localPasswordError = it },
                                onSignIn = onSignInWithEmail,
                                onSignUp = onSignUpWithEmail
                            )
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sign_in_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.5.dp,
                                color = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isSignUpMode) "Creating Account..." else "Signing In...",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = if (isSignUpMode) "Create Account" else "Sign In",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Divider with "OR"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0x33D4AF37)
                        )
                        Text(
                            text = "OR",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0BEC5),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = Color(0x33D4AF37)
                        )
                    }

                    // Continue with Google Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (activity != null && !isLoading) {
                                onGoogleSignInClick(activity)
                            }
                        },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("google_sign_in_button")
                    ) {
                        GoogleGLogo()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F)
                        )
                    }
                }
            }

            // Signup Navigation Footer
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = if (isSignUpMode) "Already have an account?" else "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCFD8DC)
                )
                TextButton(
                    onClick = {
                        isSignUpMode = !isSignUpMode
                        localEmailError = null
                        localPasswordError = null
                        onDismissError()
                    }
                ) {
                    Text(
                        text = if (isSignUpMode) "Sign In" else "Create Account",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            initialEmail = emailInput,
            onDismiss = { showForgotPasswordDialog = false },
            onSendReset = onSendPasswordReset
        )
    }
}

private fun submitForm(
    isSignUp: Boolean,
    email: String,
    password: String,
    name: String,
    onEmailError: (String?) -> Unit,
    onPasswordError: (String?) -> Unit,
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String, String) -> Unit
) {
    var hasError = false
    val cleanEmail = email.trim()

    if (cleanEmail.isBlank()) {
        onEmailError("Email is required.")
        hasError = true
    } else if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
        onEmailError("Please enter a valid email address.")
        hasError = true
    } else {
        onEmailError(null)
    }

    if (password.isBlank()) {
        onPasswordError("Password is required.")
        hasError = true
    } else if (password.length < 6) {
        onPasswordError("Password must be at least 6 characters.")
        hasError = true
    } else {
        onPasswordError(null)
    }

    if (!hasError) {
        if (isSignUp) {
            onSignUp(cleanEmail, password, name)
        } else {
            onSignIn(cleanEmail, password)
        }
    }
}

@Composable
private fun ForgotPasswordDialog(
    initialEmail: String,
    onDismiss: () -> Unit,
    onSendReset: (email: String, onResult: (Boolean, String) -> Unit) -> Unit
) {
    var resetEmail by remember { mutableStateOf(initialEmail) }
    var isSending by remember { mutableStateOf(false) }
    var statusFeedback by remember { mutableStateOf<Pair<Boolean, String>?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LockReset, contentDescription = null, tint = GoldSecondary)
                Spacer(modifier = Modifier.width(10.dp))
                Text("Reset Password", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Enter your registered email address. We'll send you an official password reset link.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = { resetEmail = it },
                    label = { Text("Email Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                if (statusFeedback != null) {
                    val (isSuccess, message) = statusFeedback!!
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSuccess) EmeraldPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSuccess) EmeraldPrimary else MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (resetEmail.isNotBlank()) {
                        isSending = true
                        onSendReset(resetEmail) { success, msg ->
                            isSending = false
                            statusFeedback = Pair(success, msg)
                        }
                    }
                },
                enabled = !isSending && resetEmail.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                if (isSending) {
                    CircularProgressIndicator(strokeWidth = 2.dp, color = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Send Reset Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun GoogleGLogo() {
    Canvas(modifier = Modifier.size(22.dp)) {
        val width = size.width
        val height = size.height
        drawCircle(color = Color(0xFF4285F4), radius = width * 0.48f, center = Offset(width * 0.5f, height * 0.5f))
        drawCircle(color = Color.White, radius = width * 0.28f, center = Offset(width * 0.5f, height * 0.5f))
        drawRect(
            color = Color(0xFF4285F4),
            topLeft = Offset(width * 0.45f, height * 0.38f),
            size = androidx.compose.ui.geometry.Size(width * 0.52f, height * 0.24f)
        )
    }
}
