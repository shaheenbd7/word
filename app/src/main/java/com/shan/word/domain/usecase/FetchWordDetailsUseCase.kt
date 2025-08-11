package com.shan.word.domain.usecase

import com.shan.word.data.repository.DictionaryRepositoryImpl
import com.shan.word.domain.entity.Word
import javax.inject.Inject

class FetchWordDetailsUseCase @Inject constructor(
    private val dictionaryRepository: DictionaryRepositoryImpl
) {
    suspend operator fun invoke(word: String): Word? {
        return dictionaryRepository.fetchAndSaveWordDetails(word)
    }
} 