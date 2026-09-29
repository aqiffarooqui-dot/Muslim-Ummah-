package com.example.ui.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.local.AppUser
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GoogleAuthManager {
    const val ADMIN_EMAIL = "aqiffarooqui@gmail.com"

    fun isAdminEmail(email: String): Boolean {
        return email.trim().equals(ADMIN_EMAIL, ignoreCase = true)
    }

    suspend fun signInWithGoogleCredentialManager(
        context: Context,
        activity: Activity
    ): Result<AppUser> = withContext(Dispatchers.IO) {
        val credentialManager = CredentialManager.create(context)

        // Web Client ID or default Google client
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("google-apps.apps.googleusercontent.com")
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val email = googleIdTokenCredential.id
                val name = googleIdTokenCredential.displayName ?: email.substringBefore("@")
                val photo = googleIdTokenCredential.profilePictureUri?.toString() ?: ""
                val isAdmin = isAdminEmail(email)

                val user = AppUser(
                    email = email.trim(),
                    displayName = name,
                    photoUrl = photo,
                    isPremium = isAdmin,
                    planType = if (isAdmin) "Lifetime VIP" else "Free",
                    role = if (isAdmin) "ADMIN" else "USER",
                    notes = if (isAdmin) "Primary Administrator" else "Google Authenticated"
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Unknown credential format: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Google Sign-In was cancelled."))
        } catch (e: GetCredentialException) {
            Log.w("GoogleAuth", "CredentialManager: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Google Sign-In error", e)
            Result.failure(e)
        }
    }

    fun authenticateWithGoogleEmail(email: String, displayName: String? = null): AppUser {
        val cleanEmail = email.trim()
        val isAdmin = isAdminEmail(cleanEmail)
        val name = displayName?.ifBlank { null } ?: cleanEmail.substringBefore("@").replace(".", " ").split(" ")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

        return AppUser(
            email = cleanEmail,
            displayName = name,
            photoUrl = "",
            isPremium = isAdmin,
            planType = if (isAdmin) "Lifetime VIP" else "Free",
            role = if (isAdmin) "ADMIN" else "USER",
            registeredDate = System.currentTimeMillis(),
            notes = if (isAdmin) "Primary Administrator" else "Google Verified Account"
        )
    }

    fun createDefaultAdminUser(): AppUser {
        return AppUser(
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
