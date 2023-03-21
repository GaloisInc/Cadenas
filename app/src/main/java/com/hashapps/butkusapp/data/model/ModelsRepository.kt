package com.hashapps.butkusapp.data.model

import kotlinx.coroutines.flow.Flow

interface ModelsRepository {
    fun getAllModelsStream(): Flow<List<Model>>

    suspend fun insertModel(model: Model)

    suspend fun deleteModel(model: Model)
}