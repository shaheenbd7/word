package com.shan.word.domain.entity

enum class WordStatus {
    UNKNOWN,
    KNOWN,
    DISCARDED
}

data class Word(
    val id: Int = 0,
    val word: String,
    val filenameId: Int,
    val translation: String? = null,
    val pronunciation: String? = null,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    val examples: List<String> = emptyList(),
    val definition: String? = null,
    val isFavorite: Boolean = false,
    val status: WordStatus = WordStatus.UNKNOWN,
    val audioUrl: String? = null,
    val partOfSpeech: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) 