package com.galois.cadenas.utils

import com.galois.cadenas.mbfte.EncodedValue
import com.galois.cadenas.mbfte.TextCover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.io.File
import java.io.InputStream
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.system.measureTimeMillis

enum class RunStatus {
    SUCCESS, FAILED_ENCODE, FAILED_DECODE
}

@Serializable
data class RunData(
    val index: Int,
    val input: String,
    val output: String?,
    val status: RunStatus,
    val expansion: Double,
    val attempts: Int,
    val encodeTimeMillis: Long,
    val decodeTimeMillis: Long
)

@Serializable
class Metrics internal constructor(@Transient val data: List<RunData> = mutableListOf()) {

    private var successCount: Int = 0
    private var failedEncodeCount: Int = 0
    private var failedDecodeCount: Int = 0
    val failedCount: Int
        get() = failedEncodeCount + failedDecodeCount
    private var averageEncodeTimeMillis: Double = 0.0
    private var averageDecodeTimeMillis: Double = 0.0
    private var medianEncodeTimeMillis: Double = 0.0
    private var medianDecodeTimeMillis: Double = 0.0
    private var averageExpansion: Double = 0.0
    private var stddevEncodeTimeMillis: Double = 0.0
    private var stddevDecodeTimeMillis: Double = 0.0


    init {
        //Run through the data once and compute everything that you can
        var totalEncodeTime: Long = 0
        var totalDecodeTime: Long = 0
        var totalExpansion = 0.0
        val allEncodeTimes = mutableListOf<Long>()
        val allDecodeTimes = mutableListOf<Long>()
        for (d in data) {
            when (d.status) {
                RunStatus.SUCCESS -> successCount += 1
                RunStatus.FAILED_ENCODE -> failedEncodeCount += 1
                RunStatus.FAILED_DECODE -> failedDecodeCount += 1
            }

            if (d.status == RunStatus.SUCCESS) {
                totalEncodeTime += d.encodeTimeMillis
                totalDecodeTime += d.decodeTimeMillis
                totalExpansion += d.expansion

                allEncodeTimes.add(d.encodeTimeMillis)
                allDecodeTimes.add(d.decodeTimeMillis)
            }
        }

        averageEncodeTimeMillis = totalEncodeTime.toDouble() / successCount
        averageDecodeTimeMillis = totalDecodeTime.toDouble() / successCount
        averageExpansion = totalExpansion / successCount

        //Medians are done separately
        medianEncodeTimeMillis = destructiveMedian(allEncodeTimes)
        medianDecodeTimeMillis = destructiveMedian(allDecodeTimes)
        
        //So are standard deviations
        stddevEncodeTimeMillis = stddev(data.map { it.encodeTimeMillis }, averageEncodeTimeMillis)
        stddevDecodeTimeMillis = stddev(data.map { it.decodeTimeMillis }, averageDecodeTimeMillis)
    }

    private fun destructiveMedian(lst: MutableList<Long>): Double {
        if (lst.size == 0)
            return 0.0

        lst.sort()
        return if (lst.size % 2 == 0)
            ((lst[lst.size / 2] + lst[lst.size / 2 - 1]) / 2).toDouble()
        else
            lst[lst.size / 2].toDouble()
    }
    
    private fun stddev(lst: List<Long>, mean: Double): Double {
        val diffSqSum = lst.sumOf { v -> (v - mean) * (v - mean) }
        return sqrt(diffSqSum / lst.size)
    }
}

class MetricsRunner(private val encoder: TextCover, private val messages: List<String>) {

    fun run(): Metrics {
        println("Handling ${messages.size} tests")
        val data = runBlocking {
            messages.mapIndexed { id, text ->
                async(Dispatchers.Default) { encodeAndDecode(id, text) }
            }.awaitAll()
        }

        return Metrics(data)
    }

    private fun encodeAndDecode(id: Int, text: String): RunData {
        val encodedValue: EncodedValue?
        val encodeTime = measureTimeMillis {
            encodedValue = encoder.encodeUntilDecodable(text)
        }

        if (encodedValue == null) {
            return makeFailedRunData(id, text, null, RunStatus.FAILED_ENCODE)
        }

        val decoded: String?
        val decodeTime = measureTimeMillis {
            decoded = encoder.decode(encodedValue.coverText)
        }

        if (decoded == null) {
            return makeFailedRunData(id, text, encodedValue.coverText, RunStatus.FAILED_DECODE)
        }

        return RunData(
            id, text, encodedValue.coverText, RunStatus.SUCCESS,
            encodedValue.coverText.length.toDouble() / text.length.toDouble(),
            encodedValue.attempts, encodeTime, decodeTime
        )
    }

    private fun makeFailedRunData(i: Int, inp: String, outp: String?, st: RunStatus): RunData {
        assert(st != RunStatus.SUCCESS)
        return RunData(
            index = i, input = inp, output = outp,  status = st, expansion = 0.0, attempts = -1, encodeTimeMillis = 0, decodeTimeMillis = 0
        )
    }

    companion object {
        private val basicPunctuation = ",;()/@#$%*!+-&%".toCharArray().toSet()

        private val fullAsciiCharSet = CharRange(0.toChar(), 255.toChar()).filter { v ->
            v in basicPunctuation || Character.isLetter(v.code) || Character.isDigit(v.code)
        }

        private val simpleAsciiCharSet = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789$basicPunctuation").toList()

        fun generateRandomStrings(sizeRange: IntRange, count: Int, seed: Int, useFullAscii: Boolean = false): List<String> {
            val results = mutableListOf<String>()

            val charSet = if (useFullAscii) fullAsciiCharSet else simpleAsciiCharSet

            val rnd: Random = Random(seed)
            repeat(count) {
                val rndLength = rnd.nextInt(sizeRange.first, sizeRange.last)

                val buffer = StringBuffer()
                repeat (rndLength) {
                    val index = rnd.nextInt(0, charSet.lastIndex)
                    val ch = charSet[index]
                    buffer.append(ch)
                }

                println(buffer.toString())
                results.add(buffer.toString())
            }

            return results
        }

        fun saveRandomStrings(sizeRange: IntRange, count: Int, filePath: String, seed: Int) {
            val lst = generateRandomStrings(sizeRange, count, seed)
            val preamble = "---- seed = $seed, min-len = ${sizeRange.first}, max-len = ${sizeRange.last}"
            val fullText = preamble + "\n" + lst.joinToString("\n")
            File(filePath).writeText(fullText)
        }

        private fun extractRelevantLines(lines: Sequence<String>): List<String> {
            return lines.dropWhile { it.trim().startsWith("----") }.map { it.trim() }.toList()
        }

        fun loadRandomStrings(stream: InputStream): List<String> {
            return stream.bufferedReader().useLines(Companion::extractRelevantLines)
        }
    }
}