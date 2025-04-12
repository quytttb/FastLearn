package com.app.fastlearn.di

import android.content.Context
import androidx.room.Room
import com.app.fastlearn.data.local.AppDatabase
import com.app.fastlearn.data.local.dao.DocumentDao
import com.app.fastlearn.data.local.dao.FlashcardDao
import com.app.fastlearn.data.local.dao.ProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun provideDocumentDao(appDatabase: AppDatabase): DocumentDao {
        return appDatabase.documentDao()
    }

    @Singleton
    @Provides
    fun provideFlashcardDao(appDatabase: AppDatabase): FlashcardDao {
        return appDatabase.flashcardDao()
    }

    @Singleton
    @Provides
    fun provideProgressDao(appDatabase: AppDatabase): ProgressDao {
        return appDatabase.progressDao()
    }
}
