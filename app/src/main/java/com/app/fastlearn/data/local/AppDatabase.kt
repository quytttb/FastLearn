package com.app.fastlearn.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.app.fastlearn.data.local.dao.CategoryDao
import com.app.fastlearn.data.local.dao.DocumentDao
import com.app.fastlearn.data.local.dao.FlashcardDao
import com.app.fastlearn.data.local.dao.ProgressDao
import com.app.fastlearn.data.local.entity.CategoryEntity
import com.app.fastlearn.data.local.entity.DocumentEntity
import com.app.fastlearn.data.local.entity.FlashcardEntity
import com.app.fastlearn.data.local.entity.ProgressEntity
import com.app.fastlearn.data.util.Converters


@Database(
    entities = [
        DocumentEntity::class,
        FlashcardEntity::class,
        ProgressEntity::class,
        CategoryEntity::class
    ],
    version = 2,// Đổi version khi thay đổi cấu trúc database
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun progressDao(): ProgressDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fastlearn_database"
                )
                     .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}