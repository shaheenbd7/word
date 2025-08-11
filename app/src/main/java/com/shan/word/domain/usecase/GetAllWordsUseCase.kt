package com.shan.word.domain.usecase

import com.shan.word.domain.entity.Word
import com.shan.word.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllWordsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    operator fun invoke(): Flow<List<Word>> {
        return repository.getAllWords()
    }
} 