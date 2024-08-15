package com.galois.cadenas.mbfte

import org.pytorch.Module
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

private class ModelCache {
    private val cache = mutableMapOf<String, Module>()

    fun getOrCreate(key: String, creator: () -> Module): Module {
        return cache[key] ?: run {
            val module = creator()
            cache[key] = module
            module
        }
    }
}

actual object ModelLoaderFactory {
    actual fun createModelLoader(): ModelLoader = JvmModelLoader
}

object JvmModelLoader : ModelLoader {
    private val cache: ModelCache = ModelCache()

    override fun loadModel(modelDir: String): Module {
        val modelFile = Path(modelDir, "gpt2.ptl")
        return cache.getOrCreate(modelFile.absolutePathString()) {
            Module.load(modelFile.absolutePathString())
        }
    }

    override fun unloadModel() {}
}