package com.shan.word.data.mapper

import com.shan.word.data.local.entity.WordEntity
import com.shan.word.data.local.entity.FilenameEntity
import com.shan.word.domain.entity.Word
import com.shan.word.domain.entity.Filename

object WordMapper {
    fun mapToDomain(entity: WordEntity): Word {
        return Word(
            id = entity.id,
            word = entity.word,
            filenameId = entity.filenameId
        )
    }

    fun mapToData(domain: Word): WordEntity {
        return WordEntity(
            id = domain.id,
            word = domain.word,
            filenameId = domain.filenameId
        )
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