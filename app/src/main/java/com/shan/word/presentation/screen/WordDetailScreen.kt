package com.shan.word.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.nl.translate.Translation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDetailScreen(word: String, onBack: () -> Unit) {
    var translation by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(word) {
        loading = true
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.BENGALI)
            .build()
        val translator = Translation.getClient(options)
        translator.downloadModelIfNeeded()
            .addOnSuccessListener {
                translator.translate(word)
                    .addOnSuccessListener { translatedText ->
                        translation = translatedText
                        loading = false
                    }
                    .addOnFailureListener {
                        translation = "Translation failed"
                        loading = false
                    }
            }
            .addOnFailureListener {
                translation = "Model download failed"
                loading = false
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(word) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
        if (loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Text(
                text = translation ?: "",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
} 