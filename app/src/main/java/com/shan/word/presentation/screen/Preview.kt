package com.shan.word.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shan.word.ui.theme.WordTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    WordTheme {
        MainScreen(
            onSelectPdf = {},
            onDeleteAll = {},
            filenamesFlow = MutableStateFlow(emptyList()),
            onFileSelected = {},
            parsingInProgress = MutableStateFlow(false),
            wordsFound = MutableStateFlow(0)
        )
    }
} 