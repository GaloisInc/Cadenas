package com.hashapps.butkusapp

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import org.galois.rocky.butkus.mbfte.Context
import org.galois.rocky.butkus.mbfte.ModelLoader
import org.galois.rocky.butkus.mbfte.TextCover
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.pathString

class DecodeCache {

    private val MAXELEMS = 100

    private var cacheOrder: ArrayList<String> = ArrayList()

    private var cache: MutableMap<String, String> = mutableMapOf()

    fun add(decoded: String, encoded: String) {
        if (!(decoded in cache)) {
            evictIfRequired()
            cache.put(decoded, encoded)
            cacheOrder.add(0, decoded)
        } else {
            adjustLRU(decoded)
        }
    }

    fun get(decoded: String): String? {
        if (!(decoded in cache))
            return null
        else {
            val encoded = cache[decoded]
            adjustLRU(decoded)
            return encoded
        }
    }

    private fun adjustLRU(key: String) {
        cacheOrder.remove(key)
        cacheOrder.add(0, key)
    }

    private fun evictIfRequired() {
        if (cache.size >= MAXELEMS) {
            cache.remove(cacheOrder.last())
            cacheOrder.removeLast()
        }
    }
}

data class ButkusConfig(
    val modelDir: String = "", //TODO: We want to use a directory, but this requires change in butkuscoremodels
    val key: String = "SecretKey",
    val seed: String = "Little Lilly was so upset after the loss of Pluto, her beloved pet dog. She was missing him so much, and in grief, she did not even come for the dinner and went to sleep without eating.<|endoftext|>",
    val temperature: Float = 1.0f,
    val topK: Int = 100,
    val topP: Float = 0.0f,
    val indexMax: Int = 5
)

class Butkus(val configFile: String) {

    private val tags = listOf("#talktherapy")

    private lateinit var _cover: TextCover

    private var decodeCache: DecodeCache = DecodeCache()

    init {
        _cover = makeCover()
    }

    suspend fun encode(text: String): String? {
        val cover = makeCover()
        val coverText = cover.encodeUntilDecodable(text)
        return coverText?.apply {
            decodeCache.add(this, text)
        }
    }

    //NOTE: We expect a string after stripping off superfluous tags (if any) here
    suspend fun decode(msg: String): String? {
        decodeCache.get(msg)?.let { return it }

        val cover = makeCover()
        val text = cover.decode(msg)

        return text?.apply {
            decodeCache.add(msg, text)
        }
    }

    private fun makeCover(): TextCover {
        val config = fetchButkusConfig()
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

    private fun fetchButkusConfig(): ButkusConfig {
        val file = File(configFile)
        if (file.exists()) {
            val text = file.readText()

            try {
                val json = Gson().fromJson(text, ButkusConfig::class.java)
                if (json != null)
                    return json
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            // If we got here, our parse failed !
            print("Unable to parse config file! Using defaults")
            return ButkusConfig()

        } else {
            val config = ButkusConfig()
            val text = Gson().toJson(config)
            file.writeText(text)
            return config
        }
    }

    companion object {
        var butkus: Butkus? = null

        suspend fun initialize(context: android.content.Context) {
            withContext(Dispatchers.IO) {
                ModelLoader.initialize(context)

                val x = context.assets.list("")
                for(a in (x ?: arrayOf())) {
                    println("ASSET: " + a)
                }
                val file = Path(context.filesDir.absolutePath, "butkusconfig.json")
                butkus = Butkus(file.pathString)
            }
        }

        fun getInstance(): Butkus {
            return butkus ?: throw IllegalStateException("Butkus instance has not been initialized")
        }
    }
}