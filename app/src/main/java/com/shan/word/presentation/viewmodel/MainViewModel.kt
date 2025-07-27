package com.shan.word.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shan.word.domain.entity.Filename
import com.shan.word.domain.usecase.DeleteAllWordsUseCase
import com.shan.word.domain.usecase.GetAllFilenamesUseCase
import com.shan.word.domain.usecase.InsertWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllFilenamesUseCase: GetAllFilenamesUseCase,
    private val insertWordsUseCase: InsertWordsUseCase,
    private val deleteAllWordsUseCase: DeleteAllWordsUseCase
) : ViewModel() {

    val filenames: StateFlow<List<Filename>> = getAllFilenamesUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _parsingInProgress = MutableStateFlow(false)
    val parsingInProgress: StateFlow<Boolean> get() = _parsingInProgress

    private val _wordsFound = MutableStateFlow(0)
    val wordsFound: StateFlow<Int> get() = _wordsFound

    fun insertWords(words: List<String>, filename: String) {
        viewModelScope.launch {
            insertWordsUseCase(words, filename)
        }
    }

    fun deleteAllWords() {
        viewModelScope.launch {
            deleteAllWordsUseCase()
        }
    }

    fun setParsingInProgress(inProgress: Boolean) {
        _parsingInProgress.value = inProgress
    }

    fun setWordsFound(count: Int) {
        _wordsFound.value = count
    }

    fun selectFilenameId(id: Int?) {
        // This method is kept for compatibility but not used in the new architecture
        // The WordsViewModel handles the filename selection directly
    }
} 