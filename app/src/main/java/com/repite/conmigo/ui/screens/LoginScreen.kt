package com.repite.conmigo.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.repite.conmigo.R
import com.repite.conmigo.logic.AuthService
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke

@Composable
fun LoginScreen(
    authService: AuthService,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    scope.launch {
                        isLoading = true
                        val res = authService.signInWithGoogle(token)
                        isLoading = false
                        if (res.isSuccess) onLoginSuccess()
                        else errorMessage = res.exceptionOrNull()?.message ?: context.getString(R.string.login_error)
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Google Sign In Failed: ${e.message}"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.login_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = DuoBlue
        )
        Text(
            stringResource(R.string.login_subtitle),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email_label)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password_label)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                errorMessage,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    val res = authService.signIn(email, password)
                    isLoading = false
                    if (res.isSuccess) onLoginSuccess()
                    else errorMessage = res.exceptionOrNull()?.message ?: context.getString(R.string.login_error)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text(stringResource(R.string.login_button), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign In Button
        OutlinedButton(
            onClick = {
                val signInIntent = authService.getGoogleSignInClient().signInIntent
                googleSignInLauncher.launch(signInIntent)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.google_login_button), color = Color.DarkGray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text(stringResource(R.string.no_account_register), color = DuoBlue, fontWeight = FontWeight.Bold)
        }
    }
}
