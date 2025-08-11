package com.shan.word.data.repository

import com.shan.word.data.remote.DictionaryApiService
import com.shan.word.domain.entity.Word
import com.shan.word.domain.repository.WordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DictionaryRepositoryImpl @Inject constructor(
    private val apiService: DictionaryApiService,
    private val wordRepository: WordRepository
) {
    
    suspend fun fetchAndSaveWordDetails(wordText: String): Word? = withContext(Dispatchers.IO) {
        try {
            // First check if we have cached data
            val existingWord = wordRepository.getWordByText(wordText)
            if (existingWord?.definition != null && existingWord.lastUpdated > System.currentTimeMillis() - 24 * 60 * 60 * 1000) {
                // Return cached data if it's less than 24 hours old
                return@withContext existingWord
            }
            
            // Fetch from API
            val apiResponse = apiService.getWordDetails(wordText)
            if (apiResponse.isNotEmpty()) {
                val response = apiResponse[0]
                
                // Extract data from API response
                val pronunciation = response.phonetic ?: response.phonetics?.firstOrNull()?.text
                val audioUrl = response.phonetics?.firstOrNull { it.audio != null }?.audio
                val partOfSpeech = response.meanings?.firstOrNull()?.partOfSpeech
                
                val allSynonyms = mutableListOf<String>()
                val allAntonyms = mutableListOf<String>()
                val allExamples = mutableListOf<String>()
                val allDefinitions = mutableListOf<String>()
                
                response.meanings?.forEach { meaning ->
                    meaning.synonyms?.let { allSynonyms.addAll(it) }
                    meaning.antonyms?.let { allAntonyms.addAll(it) }
                    meaning.definitions?.forEach { definition ->
                        definition.definition?.let { allDefinitions.add(it) }
                        definition.example?.let { allExamples.add(it) }
                    }
                }
                
                // Create or update word
                val updatedWord = existingWord?.copy(
                    translation = existingWord.translation, // Keep existing translation
                    pronunciation = pronunciation,
                    synonyms = allSynonyms.distinct(),
                    antonyms = allAntonyms.distinct(),
                    examples = allExamples.distinct(),
                    definition = allDefinitions.firstOrNull(),
                    audioUrl = audioUrl,
                    partOfSpeech = partOfSpeech,
                    lastUpdated = System.currentTimeMillis()
                ) ?: Word(
                    word = wordText,
                    filenameId = 0, // This will be set when inserting
                    pronunciation = pronunciation,
                    synonyms = allSynonyms.distinct(),
                    antonyms = allAntonyms.distinct(),
                    examples = allExamples.distinct(),
                    definition = allDefinitions.firstOrNull(),
                    audioUrl = audioUrl,
                    partOfSpeech = partOfSpeech,
                    lastUpdated = System.currentTimeMillis()
                )
                
                // Save to database
                wordRepository.updateWordDetails(updatedWord)
                return@withContext updatedWord
            }
        } catch (e: Exception) {
            // Return cached data if available, otherwise null
            return@withContext wordRepository.getWordByText(wordText)
        }
        
        return@withContext null
    }
} 