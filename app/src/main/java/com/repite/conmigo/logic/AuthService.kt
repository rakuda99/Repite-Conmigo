package com.repite.conmigo.logic

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.repite.conmigo.data.UserProfile
import com.repite.conmigo.data.UserProgress
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

class AuthService(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val sharedPreferences = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isAdmin: Boolean
        get() = true // For a fully local app, we can consider the local user as admin, or keep it true to allow creating lessons locally.

    init {
        // Sign in anonymously on startup to ensure Firestore can be accessed for importing lessons from web.
        if (auth.currentUser == null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInAnonymously().await()
                    Log.d("AuthService", "Anonymous sign-in successful")
                } catch (e: Exception) {
                    Log.e("AuthService", "Anonymous sign-in failed: ${e.message}")
                }
            }
        }
    }

    suspend fun signOut() {
        // No-op for local app
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            val json = gson.toJson(profile.copy(uid = "local_user"))
            sharedPreferences.edit().putString("local_profile", json).apply()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthService", "Error saving local profile: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun saveUserProgress(progress: UserProgress): Result<Unit> {
        // Local progress is fully handled by Room in LessonViewModel. No need to sync to cloud.
        return Result.success(Unit)
    }

    suspend fun getUserProgress(): UserProgress? {
        // Return null so LessonViewModel knows there's no cloud progress to fetch.
        return null
    }

    suspend fun getUserProfile(): UserProfile? {
        return try {
            val json = sharedPreferences.getString("local_profile", null)
            if (json != null) {
                gson.fromJson(json, UserProfile::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Error getting local profile: ${e.message}")
            null
        }
    }
}
