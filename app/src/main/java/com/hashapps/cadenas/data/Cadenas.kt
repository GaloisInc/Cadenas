package com.hashapps.cadenas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.galois.rocky.butkus.mbfte.Context
import org.galois.rocky.butkus.mbfte.TextCover

class DecodeCache {

    private var cacheOrder: ArrayList<String> = ArrayList()

    private var cache: MutableMap<String, String> = mutableMapOf()

    fun add(decoded: String, encoded: String) {
        if (decoded !in cache) {
            evictIfRequired()
            cache[decoded] = encoded
            cacheOrder.add(0, decoded)
        } else {
            adjustLRU(decoded)
        }
    }

    fun get(decoded: String): String? {
        return if (decoded !in cache)
            null
        else {
            val encoded = cache[decoded]
            adjustLRU(decoded)
            encoded
        }
    }

    private fun adjustLRU(key: String) {
        cacheOrder.remove(key)
        cacheOrder.add(0, key)
    }

    private fun evictIfRequired() {
        if (cache.size >= MAX_ELEMENTS) {
            cache.remove(cacheOrder.last())
            cacheOrder.removeLast()
        }
    }

    companion object {
        private const val MAX_ELEMENTS = 100
    }
}

data class CadenasConfig(
    val modelDir: String,
    val key: String,
    val seed: String,
    val temperature: Float = 1.0f,
    val topK: Int = 100,
    val topP: Float = 0.0f,
    val indexMax: Int = 5
)

class Cadenas(config: CadenasConfig) {

    private var cover: TextCover

    private var decodeCache: DecodeCache = DecodeCache()

    init {
        cover = TextCover(
            context = Context(),
            dataDirectory = config.modelDir,
            key = config.key,
            seed = config.seed,
            temperature = config.temperature,
            topK = config.topK,
            topP = config.topP,
            indexMax = config.indexMax
        )
    }

    suspend fun encode(text: String): String? {
        return coroutineScope {
            withContext(Dispatchers.Default) {
                cover.encodeUntilDecodable(text)?.apply {
                    decodeCache.add(this, text)
                }
            }
        }
    }

    suspend fun decode(msg: String): String? {
        decodeCache.get(msg)?.let { return it }

        return coroutineScope {
            withContext(Dispatchers.Default) {
                cover.decode(msg)?.apply {
                    decodeCache.add(msg, this)
                }
            }
        }
    }

    companion object {
        private var cadenas: Cadenas? = null

        suspend fun initialize(config: CadenasConfig) {
            withContext(Dispatchers.IO) {
                cadenas = Cadenas(config)
            }
        }

        fun getInstance(): Cadenas? = cadenas
    }
}