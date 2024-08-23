package com.galois.cadenas.mbfte

import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

actual object ModelLoaderFactory {
    actual fun createModelLoader(): ModelLoader = AndroidModelLoader
}

object AndroidModelLoader : ModelLoader {
    private var loadedModule: Module? = null

    override fun loadModel(modelDir: String): Module {
        val modelFile = Path(modelDir, "gpt2.ptl")

        // If we don't manually destroy the currently-loaded module, attempting
        // to load another will cause a silent app crash.
        loadedModule?.destroy()
        LiteModuleLoader.load(modelFile.absolutePathString()).also {
            loadedModule = it
            return it
        }
    }

    override fun unloadModel() {
        loadedModule?.destroy()
        loadedModule = null
    }
}