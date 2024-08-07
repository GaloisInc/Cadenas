package com.galois.cadenas.mbfte

import android.hardware.Camera
import org.junit.*
import org.junit.Assert.*

import com.galois.cadenas.crypto.RandomPadding
import com.galois.cadenas.crypto.SivAesWithSentinel
import com.galois.cadenas.model.PyTorchGPT2LanguageModel
import com.galois.cadenas.utils.Metrics
import com.galois.cadenas.utils.MetricsRunner
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URL
import java.net.URLConnection
import java.nio.file.Path
import java.util.zip.ZipInputStream
import kotlin.io.path.*

internal class RunnerTest {
    @Before
    fun setUp() {
        val fileList = arrayOf("gpt2.ptl", "gpt2-vocab.json", "gpt2-merges.txt")
        val exists = fileList.all {
            println(MODEL_DIR.resolve(it))
            MODEL_DIR.resolve(it).exists()
        }

        MODEL_DIR.createDirectories()
        if (!exists) downloadFromUrl()
    }

    @Test
    fun checkCamera() {
        val c = Camera.getNumberOfCameras()
        assertNotEquals(0, c)
    }

    @Test
    fun testRoundTrip() {
        val cover = makeTextCover()

        val text = "Secret"

        val coverValue = cover.encodeUntilDecodable(text, completeSentence = false)
        println(coverValue)
        assertNotNull("Failed to encode message", coverValue)

        val uncovered = cover.decode(coverValue!!.coverText)
        assertNotNull(uncovered, "Failed to decode message")
        println(uncovered)

        assertEquals(text, uncovered)
    }



    private fun makeTextCover(): TextCover {
        val defaultSeed = "One day it dawns on you: You don't really need superpowers to protect the innocent from the forces of evil."
        return TextCover(
            RandomPadding(SivAesWithSentinel(DEFAULT_KEY)),
            PyTorchGPT2LanguageModel(MODEL_DIR.toString()),
            defaultSeed
        )
    }

    private fun runTestFile(path: String): Metrics? {
        val cover = makeTextCover()

        val lines = javaClass.classLoader?.let { cl -> cl.getResourceAsStream(path)
            ?.let { MetricsRunner.loadRandomStrings(it) } }
        return lines?.let { MetricsRunner(cover, it).run() }.also {
            println("Writing ${"$path.metrics.json"}")
            Path(path + ".metrics.json").writeText(
                Json.encodeToString(it!!)
            )
            Path(path + ".rundata.json").writeText(
                Json.encodeToString(it.data)
            )
        }
    }

    private fun downloadFromUrl() {
        // NOTE: See this: https://stackoverflow.com/questions/47208272/android-zipinputstream-only-deflated-entries-can-have-ext-descriptor
        // for why we are currently doing this in two steps instead of a directly wrapping
        // the stream from the URL.
        val connection = URL(MODEL_DIR_DOWNLOAD_URL).openConnection() as URLConnection


        //println(connection.headerFields)
        ZipInputStream(connection.inputStream).use { stream ->
            generateSequence { stream.nextEntry }
                .filterNot { it.isDirectory }
                .forEach {
                    val destFileName = MODEL_DIR.resolve(it.name)
                    println("FILE IS $destFileName")
                    destFileName.outputStream().use { outStream ->
                        stream.copyTo(outStream)
                    }
                }
        }
    }

    private companion object {
        //Location of a downloadable model (directory) for testing
        //TODO: This is currently an owncloud public link; find a better public location
        private const val MODEL_DIR_DOWNLOAD_URL =
            "https://owncloud-tng.galois.com/index.php/s/jRkJVwQfNXnjg4b/download"

        private val MODEL_DIR: Path =
            Path(System.getProperty("java.io.tmpdir"), "__butkuscore_test", "model")

        private val DEFAULT_KEY =
            byteArrayOf(
                52, 56, 100, 100, 53, 98, 54, 100, 100, 53, 56, 52, 57, 50, 99, 55,
                55, 52, 9, 99, 102, 56, 54, 49, 55, 102, 48, 57, 52, 55, 51, 56
            )
    }
}