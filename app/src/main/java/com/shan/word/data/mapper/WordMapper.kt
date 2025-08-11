package com.shan.word.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shan.word.data.local.entity.WordEntity
import com.shan.word.data.local.entity.FilenameEntity
import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.Filename

object WordMapper {
    private val gson = Gson()
    
    fun mapToDomain(entity: WordEntity): Word {
        return Word(
            id = entity.id,
            word = entity.word,
            filenameId = entity.filenameId,
            translation = entity.translation,
            pronunciation = entity.pronunciation,
            synonyms = entity.synonyms?.let { parseJsonList(it) } ?: emptyList(),
            antonyms = entity.antonyms?.let { parseJsonList(it) } ?: emptyList(),
            examples = entity.examples?.let { parseJsonList(it) } ?: emptyList(),
            definition = entity.definition
        )
    }

    fun mapToData(domain: Word): WordEntity {
        return WordEntity(
            id = domain.id,
            word = domain.word,
            filenameId = domain.filenameId,
            translation = domain.translation,
            pronunciation = domain.pronunciation,
            synonyms = domain.synonyms.takeIf { it.isNotEmpty() }?.let { gson.toJson(it) },
            antonyms = domain.antonyms.takeIf { it.isNotEmpty() }?.let { gson.toJson(it) },
            examples = domain.examples.takeIf { it.isNotEmpty() }?.let { gson.toJson(it) },
            definition = domain.definition
        )
    }
    
    private fun parseJsonList(json: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

object FilenameMapper {
    fun mapToDomain(entity: FilenameEntity): Filename {
        return Filename(
            id = entity.id,
            name = entity.name
        )
    }

    fun mapToData(domain: Filename): FilenameEntity {
        return FilenameEntity(
            id = domain.id,
            name = domain.name
        )
    }
} 