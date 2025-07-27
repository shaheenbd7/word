package com.shan.word.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shan.word.domain.entity.Word
import com.shan.word.domain.usecase.GetWordsForFilenameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class WordsViewModel @Inject constructor(
    private val getWordsForFilenameUseCase: GetWordsForFilenameUseCase
) : ViewModel() {

    fun getWordsForFilename(filenameId: Int): StateFlow<List<Word>> {
        return getWordsForFilenameUseCase(filenameId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
} 