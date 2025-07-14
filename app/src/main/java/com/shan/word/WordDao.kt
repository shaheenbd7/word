package com.shan.word
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(words: List<Word>)

    @Query("SELECT * FROM words ORDER BY word ASC")
    fun getAllWords(): kotlinx.coroutines.flow.Flow<List<Word>>
} 