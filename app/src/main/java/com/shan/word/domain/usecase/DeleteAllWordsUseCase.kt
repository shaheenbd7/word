package com.shan.word.domain.usecase

import com.shan.word.domain.repository.WordRepository
import javax.inject.Inject

class DeleteAllWordsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllWords()
    }
} 