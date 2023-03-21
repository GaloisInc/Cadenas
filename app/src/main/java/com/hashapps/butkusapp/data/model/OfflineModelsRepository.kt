package com.hashapps.butkusapp.data.model

class OfflineModelsRepository(private val modelDao: ModelDao) : ModelsRepository {
    override fun getAllModelsStream() = modelDao.getAllModels()

    override suspend fun insertModel(model: Model) = modelDao.insert(model)

    override suspend fun deleteModel(model: Model) = modelDao.delete(model)
}