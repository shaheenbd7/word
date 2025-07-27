package com.shan.word.di

import android.content.Context
import com.shan.word.data.local.WordDatabase
import com.shan.word.data.repository.WordRepositoryImpl
import com.shan.word.domain.repository.WordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): WordDatabase {
        return WordDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideWordRepository(db: WordDatabase): WordRepository {
        return WordRepositoryImpl(db)
    }
} 