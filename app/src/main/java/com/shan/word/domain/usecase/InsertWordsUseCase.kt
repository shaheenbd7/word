package com.shan.word.domain.usecase

import com.shan.word.domain.repository.WordRepository
import javax.inject.Inject

class InsertWordsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(words: List<String>, filename: String) {
        repository.insertWords(words, filename)
    }
} 