package com.example.ui.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.data.local.AppUser
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object GoogleAuthManager {
    private const val TAG = "GoogleAuthManager"

    // Fallback Web Client ID if resource lookup fails
    private const val WEB_CLIENT_ID_FALLBACK = "518938483883-3f0vvd8bkcd8e0cc21h6ciifgg1j3euv.apps.googleusercontent.com"

    /**
     * Resolves the Web OAuth client ID dynamically from google-services generated resources:
     * R.string.default_web_client_id
     */
    fun getWebClientId(context: Context): String {
        return try {
            val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
            if (resId != 0) {
                context.getString(resId)
            } else {
                WEB_CLIENT_ID_FALLBACK
            }
        } catch (_: Throwable) {
            WEB_CLIENT_ID_FALLBACK
        }
    }

    /**
     * Checks if a Firebase user is actively authenticated
     */
    fun getCurrentFirebaseUser(): FirebaseUser? {
        return try {
            FirebaseAuth.getInstance().currentUser
        } catch (_: Throwable) {
            null
        }
    }

    fun isUserLoggedIn(): Boolean {
        return try {
            FirebaseAuth.getInstance().currentUser != null
        } catch (_: Throwable) {
            false
        }
    }

    fun getCurrentAppUser(): AppUser? {
        return try {
            val fbUser = FirebaseAuth.getInstance().currentUser ?: return null
            val email = fbUser.email ?: ""
            AppUser(
                uid = fbUser.uid,
                email = email.trim(),
                displayName = fbUser.displayName?.ifBlank { null } ?: email.substringBefore("@"),
                photoUrl = fbUser.photoUrl?.toString() ?: "",
                isPremium = false,
                planType = "Free",
                role = "USER",
                registeredDate = fbUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                notes = if (isAdmin) "Primary Administrator" else "Google Authenticated"
            )
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseAuth not ready or not initialized: ${e.message}")
            null
        }
    }

    /**
     * Mandatory Google Sign-In using Android Credential Manager + Google Identity Services.
     * Incorporates GetSignInWithGoogleOption and GetGoogleIdOption to avoid "No credentials available".
     */
    suspend fun signInWithGoogleCredentialManager(
        context: Context,
        activity: Activity
    ): Result<AppUser> = withContext(Dispatchers.IO) {
        val credentialManager = CredentialManager.create(context)
        val webClientId = getWebClientId(context)

        // Primary: GetSignInWithGoogleOption triggers Google's standard native account picker
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        // Fallback: GetGoogleIdOption for existing authorized credentials
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(activity, request)
            handleCredentialResult(result.credential)
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Google Sign-In was cancelled."))
        } catch (noCred: NoCredentialException) {
            Log.w(TAG, "NoCredentialException encountered, attempting GetSignInWithGoogleOption explicit fallback: ${noCred.message}")
            try {
                val fallbackRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()
                val fallbackResult = credentialManager.getCredential(activity, fallbackRequest)
                handleCredentialResult(fallbackResult.credential)
            } catch (fallbackEx: Exception) {
                Log.e(TAG, "CredentialManager explicit fallback error: ${fallbackEx.message}", fallbackEx)
                Result.failure(getFriendlyAuthErrorMessage(fallbackEx))
            }
        } catch (e: Exception) {
            Log.e(TAG, "CredentialManager error: ${e.message}", e)
            Result.failure(getFriendlyAuthErrorMessage(e))
        }
    }

    private suspend fun handleCredentialResult(credential: Credential): Result<AppUser> {
        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            val email = googleIdTokenCredential.id
            val name = googleIdTokenCredential.displayName ?: email.substringBefore("@")
            val photo = googleIdTokenCredential.profilePictureUri?.toString() ?: ""

            // Authenticate with Firebase using Google ID Token
            val firebaseAuth = FirebaseAuth.getInstance()
            val authCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            val firebaseUser = authResult.user ?: throw Exception("Firebase user is null after sign-in")

            val user = AppUser(
                uid = firebaseUser.uid,
                email = (firebaseUser.email ?: email).trim(),
                displayName = firebaseUser.displayName ?: name,
                photoUrl = firebaseUser.photoUrl?.toString() ?: photo,
                isPremium = false,
                planType = "Free",
                role = "USER",
                registeredDate = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                notes = if (isAdmin) "Primary Administrator" else "Google Authenticated"
            )
            return Result.success(user)
        } else {
            return Result.failure(Exception("Unknown credential format received: ${credential.type}"))
        }
    }

    /**
     * Firebase Email and Password Sign In
     */
    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): Result<AppUser> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            val firebaseAuth = FirebaseAuth.getInstance()
            val authResult = firebaseAuth.signInWithEmailAndPassword(cleanEmail, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User is null after sign in")

            val user = AppUser(
                uid = firebaseUser.uid,
                email = cleanEmail,
                displayName = firebaseUser.displayName?.ifBlank { null } ?: cleanEmail.substringBefore("@"),
                photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                isPremium = false,
                planType = "Free",
                role = "USER",
                registeredDate = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                notes = if (isAdmin) "Primary Administrator" else "Email Authenticated"
            )
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "signInWithEmailAndPassword error: ${e.message}", e)
            Result.failure(getFriendlyAuthErrorMessage(e))
        }
    }

    /**
     * Firebase Email and Password Account Creation (Signup)
     */
    suspend fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        displayName: String? = null
    ): Result<AppUser> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            val firebaseAuth = FirebaseAuth.getInstance()
            val authResult = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User is null after account creation")

            val name = displayName?.trim()?.ifBlank { null } ?: cleanEmail.substringBefore("@")
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()
            } catch (_: Exception) {}

            val user = AppUser(
                uid = firebaseUser.uid,
                email = cleanEmail,
                displayName = name,
                photoUrl = "",
                isPremium = false,
                planType = "Free",
                role = "USER",
                registeredDate = System.currentTimeMillis(),
                notes = if (isAdmin) "Primary Administrator" else "Email Registered"
            )
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "createUserWithEmailAndPassword error: ${e.message}", e)
            Result.failure(getFriendlyAuthErrorMessage(e))
        }
    }

    /**
     * Firebase Password Reset Email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = email.trim()
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.sendPasswordResetEmail(cleanEmail).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendPasswordResetEmail error: ${e.message}", e)
            Result.failure(getFriendlyAuthErrorMessage(e))
        }
    }

    /**
     * Converts Firebase and CredentialManager exceptions into user-friendly messages.
     * Never displays raw system exceptions.
     */
    fun getFriendlyAuthErrorMessage(e: Throwable): Exception {
        val msg = e.message ?: ""
        return when {
            e is FirebaseAuthInvalidUserException || msg.contains("user-not-found", ignoreCase = true) ->
                Exception("No account found with this email. Please check your email or tap Create Account.")
            e is FirebaseAuthInvalidCredentialsException || msg.contains("wrong-password", ignoreCase = true) || msg.contains("invalid-credential", ignoreCase = true) ->
                Exception("Invalid email address or incorrect password. Please check and try again.")
            e is FirebaseAuthUserCollisionException || msg.contains("email-already-in-use", ignoreCase = true) ->
                Exception("An account already exists with this email address. Please sign in instead.")
            e is FirebaseAuthWeakPasswordException || msg.contains("weak-password", ignoreCase = true) ->
                Exception("Password is too weak. Please use at least 6 characters.")
            e is FirebaseNetworkException || msg.contains("network", ignoreCase = true) ->
                Exception("Network connection error. Please check your internet connection and try again.")
            e is FirebaseAuthRecentLoginRequiredException ->
                Exception("Please sign in again to continue.")
            msg.contains("TOO_MANY_ATTEMPTS", ignoreCase = true) || msg.contains("too-many-requests", ignoreCase = true) ->
                Exception("Too many failed attempts. Please wait a few moments before trying again.")
            e is GetCredentialCancellationException || msg.contains("cancelled", ignoreCase = true) ->
                Exception("Sign in was cancelled.")
            e is NoCredentialException || msg.contains("No credential", ignoreCase = true) ->
                Exception("No Google account selected or available on this device. Please select an account to continue.")
            else ->
                Exception(e.localizedMessage?.replace("com.google.firebase.auth.", "")?.replace("FirebaseAuthException: ", "") ?: "Authentication failed. Please try again.")
        }
    }

    /**
     * Signs out from Firebase and clears Android Credential Manager session
     */
    suspend fun signOut(context: Context) = withContext(Dispatchers.IO) {
        try {
            FirebaseAuth.getInstance().signOut()
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Log.d(TAG, "Successfully signed out from Firebase & Credential Manager")
        } catch (e: Exception) {
            Log.e(TAG, "Error during sign out: ${e.message}", e)
        }
    }

    fun createDefaultAdminUser(): AppUser {
        return AppUser(
            uid = "admin_owner_aqif",
            email = ADMIN_EMAIL,
            displayName = "Aqif Farooqui",
            photoUrl = "",
            isPremium = true,
            planType = "Lifetime VIP",
            role = "ADMIN",
            registeredDate = System.currentTimeMillis() - (60L * 24 * 3600 * 1000),
            notes = "Owner & System Administrator"
        )
    }
}
