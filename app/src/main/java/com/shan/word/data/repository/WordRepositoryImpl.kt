package com.shan.word.data.repository

import com.shan.word.data.local.WordDatabase
import com.shan.word.data.mapper.WordMapper
import com.shan.word.data.mapper.FilenameMapper
import com.shan.word.data.local.entity.WordEntity
import com.shan.word.data.local.entity.FilenameEntity
import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.Filename
import com.shan.word.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WordRepositoryImpl @Inject constructor(
    private val database: WordDatabase
) : WordRepository {

    override fun getAllWords(): Flow<List<Word>> {
        return database.wordDao().getAllWords().map { entities ->
            entities.map { WordMapper.mapToDomain(it) }
        }
    }

    override fun getAllFilenames(): Flow<List<Filename>> {
        return database.wordDao().getAllFilenames().map { entities ->
            entities.map { FilenameMapper.mapToDomain(it) }
        }
    }

    override fun getWordsForFilenameId(filenameId: Int): Flow<List<Word>> {
        return database.wordDao().getWordsForFilenameId(filenameId).map { entities ->
            entities.map { WordMapper.mapToDomain(it) }
        }
    }

    override suspend fun insertWords(words: List<String>, filename: String) {
        // Insert filename if not exists, get its id
        var filenameId = database.wordDao().getFilenameByName(filename)?.id
        if (filenameId == null) {
            filenameId = database.wordDao().insertFilename(FilenameEntity(name = filename)).toInt()
        }
        
        val wordEntities = words.map { word ->
            WordEntity(
                word = word,
                filenameId = filenameId!!
            )
        }
        database.wordDao().insertAll(wordEntities)
    }

    override suspend fun deleteAllWords() {
        database.wordDao().deleteAllWords()
    }

    override suspend fun getFilenameByName(name: String): Filename? {
        return database.wordDao().getFilenameByName(name)?.let { FilenameMapper.mapToDomain(it) }
    }

    override suspend fun getWordByText(word: String): Word? {
        return database.wordDao().getWordByText(word)?.let { WordMapper.mapToDomain(it) }
    }

    override suspend fun updateWordDetails(word: Word) {
        database.wordDao().updateWord(WordMapper.mapToData(word))
    }
} 