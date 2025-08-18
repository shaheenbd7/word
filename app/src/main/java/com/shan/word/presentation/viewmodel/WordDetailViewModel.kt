package com.shan.word.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shan.word.domain.entity.Word
import com.shan.word.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    private val getWordDetailsUseCase: GetWordDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val markWordAsKnownUseCase: MarkWordAsKnownUseCase,
    private val markWordAsDiscardedUseCase: MarkWordAsDiscardedUseCase
) : ViewModel() {

    private val _wordDetails = MutableStateFlow<Word?>(null)
    val wordDetails: StateFlow<Word?> = _wordDetails

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadWordDetails(word: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val details = getWordDetailsUseCase(word)
                _wordDetails.value = details
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(word: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(word)
            // Reload word details to get updated state
            loadWordDetails(word)
        }
    }

    fun markAsKnown(word: String) {
        viewModelScope.launch {
            markWordAsKnownUseCase(word)
            // Reload word details to get updated state
            loadWordDetails(word)
        }
    }

    fun markAsDiscarded(word: String) {
        viewModelScope.launch {
            markWordAsDiscardedUseCase(word)
            // Reload word details to get updated state
            loadWordDetails(word)
        }
    }
}
