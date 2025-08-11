package com.shan.word.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shan.word.domain.entity.Word
import com.shan.word.domain.usecase.GetAllWordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AllWordsViewModel @Inject constructor(
    private val getAllWordsUseCase: GetAllWordsUseCase
) : ViewModel() {

    val allWords: StateFlow<List<Word>> = getAllWordsUseCase()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
} 