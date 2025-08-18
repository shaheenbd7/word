package com.shan.word.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shan.word.data.local.dao.WordDao
import com.shan.word.data.local.entity.WordEntity
import com.shan.word.data.local.entity.FilenameEntity
import com.shan.word.domain.entity.WordStatus

@Database(entities = [WordEntity::class, FilenameEntity::class], version = 6, exportSchema = false)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        @Volatile
        private var INSTANCE: WordDatabase? = null

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add status column with default value UNKNOWN
                database.execSQL(
                    "ALTER TABLE words ADD COLUMN status TEXT NOT NULL DEFAULT 'UNKNOWN'"
                )
            }
        }

        fun getDatabase(context: Context): WordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase::class.java,
                    "word_database"
                )
                .addMigrations(MIGRATION_5_6)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 