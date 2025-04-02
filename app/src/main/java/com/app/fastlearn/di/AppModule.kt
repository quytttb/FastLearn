package com.app.fastlearn.di

import android.content.Context
import com.app.fastlearn.domain.service.OCRService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
    @Singleton
    @Provides
    fun provideOCRService(@ApplicationContext context: Context): OCRService {
        return OCRService(context)
    }
    // You can add other app-level dependencies here (e.g., SharedPreferences)
}