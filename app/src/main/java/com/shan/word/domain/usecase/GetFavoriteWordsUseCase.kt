package com.shan.word.domain.usecase

import com.shan.word.domain.entity.Word
import com.shan.word.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteWordsUseCase @Inject constructor(
    private val repository: WordRepository
) {
    operator fun invoke(): Flow<List<Word>> {
        return repository.getAllWords().map { words ->
            words.filter { it.isFavorite }
        }
    }
} 