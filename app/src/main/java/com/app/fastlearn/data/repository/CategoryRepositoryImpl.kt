package com.app.fastlearn.data.repository

import com.app.fastlearn.data.local.dao.CategoryDao
import com.app.fastlearn.data.util.DataMapper
import com.app.fastlearn.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val dataMapper: DataMapper
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { dataMapper.mapCategoryEntityToDomain(it) }
        }
    }

    override suspend fun getCategoryById(categoryId: String): Category? {
        val categoryEntity = categoryDao.getCategoryById(categoryId)
        return categoryEntity?.let { dataMapper.mapCategoryEntityToDomain(it) }
    }

    override suspend fun insertCategory(category: Category) {
        val categoryEntity = dataMapper.mapCategoryDomainToEntity(category)
        categoryDao.insertCategory(categoryEntity)
    }

    override suspend fun updateCategory(category: Category) {
        val categoryEntity = dataMapper.mapCategoryDomainToEntity(category)
        categoryDao.insertCategory(categoryEntity) // Using insert with REPLACE strategy for update
    }

    override suspend fun deleteCategory(category: Category) {
        val categoryEntity = dataMapper.mapCategoryDomainToEntity(category)
        categoryDao.deleteCategory(categoryEntity)
    }


}