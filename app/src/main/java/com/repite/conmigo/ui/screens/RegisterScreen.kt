package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.repite.conmigo.R
import com.repite.conmigo.logic.AuthService
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    authService: AuthService,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(R.string.register_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = DuoBlue
        )
        Text(
            stringResource(R.string.register_subtitle),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.full_name_label)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.confirm_password_label)) },
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
                if (password != confirmPassword) {
                    errorMessage = "passwords_dont_match" // I'll handle mapping later or use a helper
                    return@Button
                }
                scope.launch {
                    isLoading = true
                    val res = authService.signUp(email, password)
                    isLoading = false
                    if (res.isSuccess) onRegisterSuccess()
                    else errorMessage = res.exceptionOrNull()?.message ?: "error_creating_account"
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DuoGreen),
            enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text(stringResource(R.string.register_button), fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text(stringResource(R.string.already_have_account), color = DuoBlue, fontWeight = FontWeight.Bold)
        }
    }
}
