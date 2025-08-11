package com.shan.word.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface WordApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordDetails(@Path("word") word: String): List<WordApiResponse>
}

data class WordApiResponse(
    val word: String,
    val phonetic: String?,
    val phonetics: List<Phonetic>?,
    val meanings: List<Meaning>?
)

data class Phonetic(
    val text: String?,
    val audio: String?
)

data class Meaning(
    val partOfSpeech: String?,
    val definitions: List<Definition>?,
    val synonyms: List<String>?,
    val antonyms: List<String>?
)

data class Definition(
    val definition: String?,
    val example: String?
) 