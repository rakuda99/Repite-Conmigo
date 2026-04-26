package com.repite.conmigo.logic

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.repite.conmigo.data.UserProfile
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import android.util.Log

class AuthService(private val context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    private val adminEmail = "sulaiman.repite@gmail.com" // You can change this to your email

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isAdmin: Boolean
        get() = auth.currentUser?.email == adminEmail

    fun getGoogleSignInClient(): GoogleSignInClient {
        // IMPORTANT: Get this from Firebase Console -> Authentication -> Sign-in method -> Google -> Web SDK configuration
        val webClientId = "245336262893-d52trismp61rdfukn8h31uf33c6iuojr.apps.googleusercontent.com" 
        
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        auth.signOut()
        getGoogleSignInClient().signOut().await()
    }

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return try {
            withTimeout(10000L) { // 10 seconds timeout
                val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
                usersCollection.document(uid).set(profile.copy(uid = uid)).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Error saving profile: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(): UserProfile? {
        return try {
            withTimeout(5000L) { // 5 seconds timeout for quick check
                val uid = auth.currentUser?.uid ?: return@withTimeout null
                val doc = usersCollection.document(uid).get().await()
                doc.toObject(UserProfile::class.java)
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Error getting profile: ${e.message}")
            null
        }
    }
}
