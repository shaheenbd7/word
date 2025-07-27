package com.shan.word.domain.usecase

import com.shan.word.domain.entity.Filename
import com.shan.word.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFilenamesUseCase @Inject constructor(
    private val repository: WordRepository
) {
    operator fun invoke(): Flow<List<Filename>> {
        return repository.getAllFilenames()
    }
} 