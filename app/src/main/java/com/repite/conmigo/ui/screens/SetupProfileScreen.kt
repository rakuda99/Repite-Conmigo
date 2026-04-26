package com.repite.conmigo.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import com.repite.conmigo.R
import com.repite.conmigo.data.UserProfile
import com.repite.conmigo.logic.AuthService
import com.repite.conmigo.ui.theme.DuoBlue
import com.repite.conmigo.ui.theme.DuoGreen
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupProfileScreen(
    authService: AuthService,
    onComplete: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var motherTongue by remember { mutableStateOf("") }
    var targetLanguage by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Data for dropdowns
    val ageOptions = (5..99).map { it.toString() }
    val countryOptions = listOf("Saudi Arabia", "Egypt", "UAE", "Kuwait", "Morocco", "Algeria", "Jordan", "Oman", "Qatar", "Bahrain", "Other")
    
    // Using resource IDs for language names and standard ISO codes
    val languageOptions = listOf(
        R.string.lang_arabic to "ar",
        R.string.lang_english to "en",
        R.string.lang_spanish to "es",
        R.string.lang_french to "fr",
        R.string.lang_other to "other"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            stringResource(R.string.setup_profile_title),
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = DuoBlue,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            stringResource(R.string.setup_profile_subtitle),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Full Name
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(stringResource(R.string.full_name_label)) },
            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Gender Selection
        Text(stringResource(R.string.gender_label), modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = DuoBlue)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val genderOptions = listOf(
                stringResource(R.string.gender_male) to "Male",
                stringResource(R.string.gender_female) to "Female"
            )
            genderOptions.forEach { (label, value) ->
                FilterChip(
                    selected = gender == value,
                    onClick = { gender = value },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DuoBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Age Dropdown
        ProfileDropdownMenu(
            label = stringResource(R.string.age_label),
            options = ageOptions,
            selectedOption = age,
            onOptionSelected = { age = it },
            icon = Icons.Default.Cake
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Country Dropdown
        ProfileDropdownMenu(
            label = stringResource(R.string.country_label),
            options = countryOptions,
            selectedOption = country,
            onOptionSelected = { country = it },
            icon = Icons.Rounded.Public
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mother Tongue Dropdown
        ProfileDropdownMenu(
            label = stringResource(R.string.mother_tongue_label),
            options = languageOptions.map { stringResource(it.first) },
            selectedOption = languageOptions.find { it.second == motherTongue }?.let { stringResource(it.first) } ?: "",
            onOptionSelected = { label -> 
                motherTongue = languageOptions.find { context.getString(it.first) == label }?.second ?: label 
            },
            icon = Icons.Rounded.Language
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Learning Language
        Text(stringResource(R.string.target_language_prompt), modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.Bold, color = DuoBlue)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val targetOptions = listOf(
                stringResource(R.string.lang_spanish) to "es",
                stringResource(R.string.lang_english) to "en",
                stringResource(R.string.lang_french) to "fr"
            )
            targetOptions.forEach { (name, code) ->
                FilterChip(
                    selected = targetLanguage == code,
                    onClick = { targetLanguage = code },
                    label = { Text(name) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DuoGreen,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        if (motherTongue == targetLanguage && motherTongue.isNotEmpty()) {
            errorMessage = "lang_conflict_error"
        } else if (errorMessage == "lang_conflict_error") {
            errorMessage = ""
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = ""
                    val profile = UserProfile(
                        fullName = fullName,
                        gender = gender,
                        age = age,
                        country = country,
                        motherTongue = motherTongue,
                        targetLanguage = targetLanguage
                    )
                    
                    try {
                        val result = authService.saveUserProfile(profile)
                        isLoading = false
                        if (result.isSuccess) {
                            // Apply the native language immediately
                            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(motherTongue)
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            onComplete()
                        } else {
                            errorMessage = "connection_error"
                        }
                    } catch (e: Exception) {
                        isLoading = false
                        errorMessage = "unexpected_error|${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isLoading) Color.Gray else DuoGreen),
            enabled = !isLoading && fullName.isNotBlank() && gender.isNotBlank() && age.isNotBlank() && country.isNotBlank() && motherTongue.isNotBlank() && targetLanguage.isNotBlank() && motherTongue != targetLanguage
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            } else {
                Text(stringResource(R.string.complete_profile_button), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }

        if (errorMessage.isNotEmpty()) {
            val displayError = when {
                errorMessage == "connection_error" -> stringResource(R.string.connection_error)
                errorMessage == "lang_conflict_error" -> stringResource(R.string.lang_conflict_error)
                errorMessage.startsWith("unexpected_error|") -> stringResource(R.string.unexpected_error, errorMessage.substringAfter("|"))
                else -> errorMessage
            }
            Text(displayError, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 16.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDropdownMenu(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = { Icon(icon, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(text = selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
