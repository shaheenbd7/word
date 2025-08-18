package com.shan.word.domain.usecase

import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.WordStatus
import com.shan.word.domain.repository.WordRepository
import javax.inject.Inject

class GetWordsByStatusUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(status: WordStatus): List<Word> {
        return repository.getWordsByStatus(status)
    }
}
