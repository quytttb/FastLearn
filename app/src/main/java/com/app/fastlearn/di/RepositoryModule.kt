package com.app.fastlearn.di

import com.app.fastlearn.data.repository.*
import com.app.fastlearn.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDocumentRepository(
        documentRepositoryImpl: DocumentRepositoryImpl
    ): DocumentRepository

    @Binds
    @Singleton
    abstract fun bindFlashcardRepository(
        flashcardRepositoryImpl: FlashcardRepositoryImpl
    ): FlashcardRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository

    companion object {
        @Provides
        @Singleton
        fun provideRecognizedTextRepository(): RecognizedTextRepository {
            return RecognizedTextRepository()
        }
    }
}