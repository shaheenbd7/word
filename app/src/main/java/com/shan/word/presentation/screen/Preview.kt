package com.shan.word.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.shan.word.ui.theme.WordTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    WordTheme {
        MainScreen(
            onDeleteAll = {},
            filenamesFlow = MutableStateFlow(emptyList()),
            onFileSelected = {},
            parsingInProgress = MutableStateFlow(false),
            wordsFound = MutableStateFlow(0),
            onOpenDrawer = {
            },
            onShowAllWords = {

            }
        )
    }
}
