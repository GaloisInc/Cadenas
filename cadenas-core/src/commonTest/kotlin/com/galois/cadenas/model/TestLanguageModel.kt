package com.galois.cadenas.model

import java.nio.file.Path
import kotlin.io.path.Path

class TestLanguageModel (
    private val MODEL_DIR: Path =
        Path(System.getProperty("java.io.tmpdir"), "__butkuscore_test", "model"),
    public val languageModel: LanguageModel=PyTorchGPT2LanguageModel(MODEL_DIR.toString())
)