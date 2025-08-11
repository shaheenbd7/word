package com.shan.word.domain.usecase

import com.shan.word.domain.entity.Word
import com.shan.word.domain.repository.WordRepository
import javax.inject.Inject

class GetWordDetailsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(word: String): Word? {
        return repository.getWordByText(word)
    }
} 