package com.example.ui.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.local.AppUser
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object GoogleAuthManager {
    private const val TAG = "GoogleAuthManager"
    const val ADMIN_EMAIL = "aqiffarooqui@gmail.com"

    // Web Client ID extracted from project's google-services.json
    const val WEB_CLIENT_ID = "518938483883-3f0vvd8bkcd8e0cc21h6ciifgg1j3euv.apps.googleusercontent.com"

    fun isAdminEmail(email: String): Boolean {
        return email.trim().equals(ADMIN_EMAIL, ignoreCase = true)
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
            val isAdmin = isAdminEmail(email)
            AppUser(
                uid = fbUser.uid,
                email = email.trim(),
                displayName = fbUser.displayName?.ifBlank { null } ?: email.substringBefore("@"),
                photoUrl = fbUser.photoUrl?.toString() ?: "",
                isPremium = isAdmin,
                planType = if (isAdmin) "Lifetime VIP" else "Free",
                role = if (isAdmin) "ADMIN" else "USER",
                registeredDate = fbUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                notes = if (isAdmin) "Primary Administrator" else "Google Authenticated"
            )
        } catch (e: Throwable) {
            Log.w(TAG, "FirebaseAuth not ready or not initialized: ${e.message}")
            null
        }
    }

    /**
     * Mandatory Google Sign-In using modern Android Credential Manager + Firebase Authentication
     */
    suspend fun signInWithGoogleCredentialManager(
        context: Context,
        activity: Activity
    ): Result<AppUser> = withContext(Dispatchers.IO) {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
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
                val idToken = googleIdTokenCredential.idToken
                val email = googleIdTokenCredential.id
                val name = googleIdTokenCredential.displayName ?: email.substringBefore("@")
                val photo = googleIdTokenCredential.profilePictureUri?.toString() ?: ""

                // 1. Authenticate with Firebase using Google ID Token
                val firebaseAuth = FirebaseAuth.getInstance()
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()
                val firebaseUser = authResult.user ?: throw Exception("Firebase user is null after sign-in")

                val isAdmin = isAdminEmail(firebaseUser.email ?: email)

                val user = AppUser(
                    uid = firebaseUser.uid,
                    email = (firebaseUser.email ?: email).trim(),
                    displayName = firebaseUser.displayName ?: name,
                    photoUrl = firebaseUser.photoUrl?.toString() ?: photo,
                    isPremium = isAdmin,
                    planType = if (isAdmin) "Lifetime VIP" else "Free",
                    role = if (isAdmin) "ADMIN" else "USER",
                    registeredDate = firebaseUser.metadata?.creationTimestamp ?: System.currentTimeMillis(),
                    notes = if (isAdmin) "Primary Administrator" else "Google Authenticated"
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Unknown credential format received: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("Google Sign-In cancelled by user."))
        } catch (e: GetCredentialException) {
            Log.w(TAG, "CredentialManager exception: ${e.message}")
            Result.failure(Exception("Google Account sign-in unavailable on this device: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Authentication failed: ${e.message}", e)
            Result.failure(e)
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
