package com.shan.word.domain.usecase

import com.shan.word.domain.repository.WordRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(word: String) {
        val existingWord = repository.getWordByText(word)
        existingWord?.let {
            val updatedWord = it.copy(isFavorite = !it.isFavorite)
            repository.updateWordDetails(updatedWord)
        }
    }
} 