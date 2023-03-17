package com.hashapps.butkusapp.data.model

class OfflineModelsRepository(private val modelDao: ModelDao) : ModelsRepository {
    override fun getAllModelNamesStream() = modelDao.getAllModelNames()

    override fun getModelIdStream(name: String) = modelDao.getModelId(name)

    override fun getModelNameStream(id: Int) = modelDao.getModelName(id)

    override fun getModelUriStream(id: Int) = modelDao.getModelUri(id)

    override suspend fun insertModel(model: Model) = modelDao.insert(model)

    override suspend fun deleteModel(model: Model) = modelDao.delete(model)

    override suspend fun updateModel(model: Model) = modelDao.update(model)
}