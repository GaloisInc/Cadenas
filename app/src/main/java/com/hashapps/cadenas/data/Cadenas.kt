package com.hashapps.cadenas.data

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.galois.rocky.butkus.mbfte.Context
import org.galois.rocky.butkus.mbfte.ModelLoader
import org.galois.rocky.butkus.mbfte.TextCover
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.pathString

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
    val modelDir: String = "", //TODO: We want to use a directory, but this requires change in butkuscoremodels
    val key: String = "SecretKey",
    val seed: String = "Little Lilly was so upset after the loss of Pluto, her beloved pet dog. She was missing him so much, and in grief, she did not even come for the dinner and went to sleep without eating.<|endoftext|>",
    val temperature: Float = 1.0f,
    val topK: Int = 100,
    val topP: Float = 0.0f,
    val indexMax: Int = 5
)

class Cadenas(private val configFile: String) {

    private var _cover: TextCover

    private var decodeCache: DecodeCache = DecodeCache()

    init {
        _cover = makeCover()
    }

    suspend fun encode(text: String): String? {
        return coroutineScope {
            withContext(Dispatchers.Default) {
                val cover = makeCover()
                val coverText = cover.encodeUntilDecodable(text)
                coverText?.apply {
                    decodeCache.add(this, text)
                }
            }
        }
    }

    //NOTE: We expect a string after stripping off superfluous tags (if any) here
    suspend fun decode(msg: String): String? {
        decodeCache.get(msg)?.let { return it }

        return coroutineScope {
            withContext(Dispatchers.Default) {
                val cover = makeCover()
                val text = cover.decode(msg)

                text?.apply {
                    decodeCache.add(msg, text)
                }
            }
        }
    }

    private fun makeCover(): TextCover {
        val config = fetchCadenasConfig()
        return TextCover(
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

    private fun fetchCadenasConfig(): CadenasConfig {
        val file = File(configFile)
        if (file.exists()) {
            val text = file.readText()

            try {
                val json = Gson().fromJson(text, CadenasConfig::class.java)
                if (json != null)
                    return json
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            // If we got here, our parse failed !
            print("Unable to parse config file! Using defaults")
            return CadenasConfig()

        } else {
            val config = CadenasConfig()
            val text = Gson().toJson(config)
            file.writeText(text)
            return config
        }
    }

    companion object {
        private var cadenas: Cadenas? = null

        suspend fun initialize(context: android.content.Context) {
            withContext(Dispatchers.IO) {
                ModelLoader.initialize(context)

                val x = context.assets.list("")
                for (a in (x ?: arrayOf())) {
                    println("ASSET: $a")
                }
                val file = Path(context.filesDir.absolutePath, "butkusconfig.json")
                cadenas = Cadenas(file.pathString)
            }
        }

        fun getInstance(): Cadenas? {
            return cadenas
        }
    }
}