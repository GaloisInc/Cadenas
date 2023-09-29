package com.hashapps.cadenas.data

import android.util.LruCache
import com.galois.cadenas.crypto.RandomPadding
import com.galois.cadenas.crypto.SivAesWithSentinel
import com.galois.cadenas.mbfte.TextCover

data class CadenasConfig(
    val modelDir: String,
    val key: ByteArray,
    val seed: String,
    val temperature: Float = 1.0f,
    val topK: Int = 100,
    val topP: Float = 0.0f,
    val indexMax: Int = 5
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CadenasConfig

        if (modelDir != other.modelDir) return false
        if (!key.contentEquals(other.key)) return false
        if (seed != other.seed) return false
        if (temperature != other.temperature) return false
        if (topK != other.topK) return false
        if (topP != other.topP) return false
        if (indexMax != other.indexMax) return false

        return true
    }

    override fun hashCode(): Int {
        var result = modelDir.hashCode()
        result = 31 * result + key.contentHashCode()
        result = 31 * result + seed.hashCode()
        result = 31 * result + temperature.hashCode()
        result = 31 * result + topK
        result = 31 * result + topP.hashCode()
        result = 31 * result + indexMax
        return result
    }
}

class Cadenas(config: CadenasConfig) {

    private var cover: TextCover

    private var decodeCache: LruCache<String, String> = LruCache(CACHE_SIZE)

    init {
        cover = TextCover(
            dataDirectory = config.modelDir,
            cryptoSystem = RandomPadding(SivAesWithSentinel(config.key)),
            seed = config.seed,
            temperature = config.temperature,
            topK = config.topK,
            topP = config.topP,
            indexMax = config.indexMax
        )
    }

    fun encode(text: String): String? {
        return cover.encodeUntilDecodable(text)?.apply {
            decodeCache.put(this, text)
        }
    }

    fun decode(msg: String): String? {
        decodeCache.get(msg)?.let { return it }

        return cover.decode(msg)?.apply {
            decodeCache.put(msg, this)
        }
    }

    companion object {
        private const val CACHE_SIZE = 100

        private var cadenas: Cadenas? = null

        fun initialize(config: CadenasConfig) {
            cadenas = Cadenas(config)
        }

        fun getInstance(): Cadenas? = cadenas
    }
}