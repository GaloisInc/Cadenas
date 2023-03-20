package com.hashapps.butkusapp.data.model

import kotlinx.coroutines.flow.Flow

interface ModelsRepository {
    fun getAllModelsStream(): Flow<List<Model>>

    fun getAllModelNamesStream(): Flow<List<String>>

    fun getModelStream(id: Int): Flow<Model>

    fun getModelIdStream(name: String): Flow<Int?>

    fun getModelNameStream(id: Int): Flow<String?>

    fun getModelUrlStream(id: Int): Flow<String?>

    suspend fun insertModel(model: Model)

    suspend fun deleteModel(model: Model)

    suspend fun updateModel(model: Model)
}