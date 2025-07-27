package com.shan.word.domain.repository

import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.Filename
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getAllWords(): Flow<List<Word>>
    fun getAllFilenames(): Flow<List<Filename>>
    fun getWordsForFilenameId(filenameId: Int): Flow<List<Word>>
    suspend fun insertWords(words: List<String>, filename: String)
    suspend fun deleteAllWords()
    suspend fun getFilenameByName(name: String): Filename?
} 