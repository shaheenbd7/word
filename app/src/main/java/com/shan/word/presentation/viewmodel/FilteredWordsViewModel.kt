package com.shan.word.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.WordStatus
import com.shan.word.domain.usecase.GetWordsByStatusUseCase
import com.shan.word.domain.usecase.GetFavoriteWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilteredWordsViewModel @Inject constructor(
    private val getWordsByStatusUseCase: GetWordsByStatusUseCase,
    private val getFavoriteWordsUseCase: GetFavoriteWordsUseCase
) : ViewModel() {

    private val _words = MutableStateFlow<List<Word>>(emptyList())
    val words: StateFlow<List<Word>> = _words

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentFilter = MutableStateFlow<WordFilter>(WordFilter.ALL)
    val currentFilter: StateFlow<WordFilter> = _currentFilter

    fun loadWords(filter: WordFilter) {
        _currentFilter.value = filter
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val wordList = when (filter) {
                    WordFilter.ALL -> emptyList() // This will be handled by AllWordsViewModel
                    WordFilter.KNOWN -> getWordsByStatusUseCase(WordStatus.KNOWN)
                    WordFilter.UNKNOWN -> getWordsByStatusUseCase(WordStatus.UNKNOWN)
                    WordFilter.DISCARDED -> getWordsByStatusUseCase(WordStatus.DISCARDED)
                    WordFilter.FAVORITES -> getFavoriteWordsUseCase().first().toList()
                }
                _words.value = wordList
            } catch (e: Exception) {
                _words.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

enum class WordFilter {
    ALL,
    KNOWN,
    UNKNOWN,
    DISCARDED,
    FAVORITES
}
