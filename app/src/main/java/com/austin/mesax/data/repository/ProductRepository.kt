package com.austin.mesax.data.repository

import android.util.Log
import com.austin.mesax.data.api.ProductApi
import com.austin.mesax.data.local.dao.CategoryDao
import com.austin.mesax.data.local.dao.ProductDao
import com.austin.mesax.data.local.entity.CategoryEntity
import com.austin.mesax.data.local.entity.ProductEntity
import com.austin.mesax.data.local.mapper.toEntity
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ProductRepository @Inject constructor(
    private val api: ProductApi,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {
    // 🔹 UI sempre observa o banco
    fun observeProducts(
        categoryId: Int?,
        search: String?
    ): Flow<List<ProductEntity>> {
        return productDao.observeProducts(categoryId, search)
    }

    fun observeCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.observeCategories()
    }

    // 🔹 Sincronização remota

    suspend fun syncAll() {
        try {
            val productsResponse = api.getProducts(null, null)
            Log.d("SYNC_DEBUG", "prod inseridas: ${productsResponse}")
            val categoriesResponse = api.getCategories()
            Log.d("SYNC_DEBUG", "categorias inseridas: ${categoriesResponse}")

            val productEntities = productsResponse.data.map { it.toEntity() }

            Log.d("SYNC_DEBUG", "prod inseridas: ${productEntities.size}")
            val categoryEntities = categoriesResponse.data.map { it.toEntity() }

            Log.d("SYNC_DEBUG", "Antes limpar categorias")
            categoryDao.clearAll()
            categoryDao.insertAll(categoryEntities)
            Log.d("SYNC_DEBUG", "Categorias inseridas: ${categoryEntities.size}")

            productDao.clearAll()
            productDao.insertAll(productEntities)
            Log.d("SYNC_DEBUG", "Produtos inseridos: ${productEntities.size}")

        } catch (e: Exception) {
            Log.e("SYNC_ERROR", "Erro sync", e)
        }
    }


}