package com.galois.cadenas.mbfte

import org.pytorch.Module
import java.io.InputStream
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path
import kotlin.io.path.inputStream

interface ModelLoader {
    fun loadModel(modelDir: String): Module

    fun unloadModel()

    fun getAuxiliaryFileStream(modelDir: String, file: String): InputStream {
        val auxFile = Path(modelDir, file)
        return auxFile.inputStream(StandardOpenOption.READ)
    }
}

expect object ModelLoaderFactory {
    fun createModelLoader(): ModelLoader
}