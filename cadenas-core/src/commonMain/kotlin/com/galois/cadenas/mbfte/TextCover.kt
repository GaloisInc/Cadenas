package com.galois.cadenas.mbfte

import com.galois.cadenas.crypto.SymmetricCryptoSystem
import com.galois.cadenas.model.LanguageModel
import com.galois.cadenas.model.TokenProbability
import com.galois.cadenas.utils.MutableUInt32
import com.galois.cadenas.utils.floor
import com.galois.cadenas.utils.toMutableUInt32
import java.math.BigInteger
import kotlin.math.ceil
import kotlin.random.Random


class TextCover(
    private val cryptoSystem: SymmetricCryptoSystem,
    private val languageModel: LanguageModel,
    private val prompt: String
) {
    /**
     * Encode the input [text] and return the cover text.
     *
     * @param text The text to encode
     * @param completeSentence Whether the generated cover text should be a complete sentence or not
     * @param returnOnlyDecodable if true, the method will only return a non-null
     *      cover text if it is guaranteed to be decodable.
     * @return The cover text or null if the attempt fails
     */
    fun encode(text: String, completeSentence: Boolean, returnOnlyDecodable: Boolean): String? {
        val cipherText = cryptoSystem.encrypt(text.toByteArray(Charsets.UTF_8))
        return encodeFixedWidth(cipherText, completeSentence, returnOnlyDecodable)
    }

    /**
     * Decode the cover text
     *
     * @param coverText The cover text to decode
     * @return The decoded plain text or null if the decoding failes
     */
    fun decode(coverText: String): String? {
        return decodeFixedWidth(coverText)
    }

    /**
     * Helper method that retries the encoding up to [maxAttempts] and
     * returns the first successful decodable result.
     *
     * Caveat: Note that this relies on the presence of randomness in the  [cryptoSystem]
     * passed to this class; for e.g. an instance of [com.galois.cadenas.crypto.RandomPadding].
     * If the instance is fully deterministic, this method will perform identically to the
     * [encode] method.
     *
     * @param text The text to encode
     * @param completeSentence Whether the generated cover text should be a complete sentence or not
     * @param maxAttempts The maximum number of attempts
     * @return An [EncodedValue] instance if successful or null if encoding fails
     */
    fun encodeUntilDecodable(
        text: String,
        completeSentence: Boolean = false,
        maxAttempts: Int = 10
    ): EncodedValue? {
        for (i in 1..maxAttempts) {
            val cipherText = cryptoSystem.encrypt(text.toByteArray(Charsets.UTF_8))
            val encoded = encodeFixedWidth(cipherText, completeSentence, true)
            if (encoded != null) {
                println("Found decodable encoding at index $i")
                return EncodedValue(encoded, i)
            }
        }
        return null
    }

    /**
     * Destroy the TextCover.
     *
     * Some LanguageModels leak memory, so a destroy() method is provided. By
     * default, this doesn't do anything - but if you use PyTorch, for example,
     * memory is leaked and it is important to clean up the TextCover when you
     * are done with it.
     */
    fun destroy() {
        languageModel.destroy()
    }

    /**
     * Encode the cipher text
     *
     *  This method uses a fixed width arithmetic encoding scheme to generate the cover text. The
     *  basic approach is described in the [video](https://www.youtube.com/watch?v=EqKbT3QdtOI&list=PLU4IQLU9e_OrY8oASHx0u3IXAL9TOdidm&index=14)
     *  and the book [Introduction to Data Compression, Chapter 4](http://students.aiu.edu/submissions/profiles/resources/onlineBook/E3B9W5_data%20compression%20computer%20information%20technology.pdf)
     *
     * @param ciphertext The cipher text to encode
     * @param completeSentence Whether the generated cover text should be a complete sentence or not
     * @param returnOnlyDecodable if true, the method will only return a non-null
     *      cover text if it is guaranteed to be decodable
     * @return The encoded text or null if encoding fails
     */
    private fun encodeFixedWidth(
        ciphertext: ByteArray,
        completeSentence: Boolean,
        returnOnlyDecodable: Boolean
    ): String? {
        val bits = bitGen(ciphertext).iterator()

        // Last token chosen based on ciphertext bits.
        var lastToken: String? = null

        // Any past state from the model.
        var past: Any? = null

        // The produced covertext.
        var covertext: String = ""

        // List of token indices chosen. It is used to check whether the
        // generated covertext after tokenization would result in the same list
        // of token indices.
        val tokenIds = mutableListOf<Int>()

        // List to store which cumulative probability index and values the algorithm picks
        // during encoding.
        val cumulativeProbabilityValues = mutableListOf<Triple<Int, Double, Double>>()

        // Max number of bits to encode.
        val maxBits = ciphertext.size * 8 + FIXED_POINT_EXTRA_BITS

        // Keeps track of the number of bits encoded so far.
        var numEncodedBits: Int = 0

        // The low bitrange for arithmetic decoding.
        var low: MutableUInt32 = MutableUInt32.MIN_VALUE

        // The high bitrange for arithmetic decoding.
        var high: MutableUInt32 = MutableUInt32.MAX_VALUE

        // The value we are encoding, which we use when selecting the next token.
        // We maintain the invariant that `low <= encoded <= high`.
        var encoded: MutableUInt32 = getBits(bits, 32)

        // DEBUG variable that keeps track of the bits we need to encode
        var toBeTakenOut: String = toBitString(encoded)

        // DEBUG variable that keeps track of the bits we have encoded
        val takenOut: StringBuilder = StringBuilder()

        // DEBUG print
        // TODO: Use a real logger
        println("Ciphertext Bits: ${toBitString(ciphertext)}")

        var done = false
        var iterationCount: Int = 0
        while (!done) {
            // Get predictions for the next token and compute cumulative
            // probabilities after arranging them in a best-to-worst order
            val predictionResult = if (past == null) {
                languageModel.prediction(prompt, null)
            } else {
                require(lastToken != null)
                languageModel.prediction(lastToken, past)
            }

            past = predictionResult.past

            val cumulativeProbabilities = predictionResult.predictions

            // Find the first token index with cumulative probability greater
            // than the probability scaled by the bitrange.
            // println("Low: ${low}, High: ${high}, Encoded: ${encoded}")

            var i: Int = 0
            require(encoded in low..high)
            val bitrange = getFixedWidthBitRange(low, high)
            while (i < cumulativeProbabilities.size && cumulativeProbabilities[i].probability < (encoded - low).toDouble() / bitrange.toDouble())
                i++

            // If we exited the while loop without hitting the `and` condition,
            // set `i` to the last possible cumulative probability.
            if (i == cumulativeProbabilities.size)
                i--

            // We will pick this token and add the corresponding text to our cover text
            // Note that we also store a list of token ids for a later check of unique tokenizability
            //   and a few additional bits of state for debugging
            lastToken =
                languageModel.tokenizer.untokenize(listOf(cumulativeProbabilities[i].tokenId))
            covertext += lastToken
            tokenIds.add(cumulativeProbabilities[i].tokenId)
            cumulativeProbabilityValues.add(
                Triple(
                    i,
                    cumulativeProbabilities[i].probability,
                    if (i > 0) cumulativeProbabilities[i - 1].probability else 0.0
                )
            )

            // Adjust the low and high values based on the index/symbol we picked.
            val (newLow, newHigh) = adjustFixedWidth(
                low,
                bitrange,
                if (i > 0) cumulativeProbabilities[i - 1].probability else 0.toDouble(),
                cumulativeProbabilities[i].probability
            )
            low = newLow
            high = newHigh
            //println("Low: ${low}, High: ${high}, Encoded: ${encoded}")

            // Encode bits
            while (true) {
                if (low[MutableUInt32.MSB_INDEX] == high[MutableUInt32.MSB_INDEX]) {
                    val nextEncodingBit = getBits(bits, 1)
                    numEncodedBits += 1

                    low = low shl 1
                    high = (high shl 1) or ONE
                    encoded = (encoded shl 1) or nextEncodingBit

                    takenOut.append(toBeTakenOut[0])
                    toBeTakenOut = toBeTakenOut.substring(1) + nextEncodingBit.toString()
                } else if (low[MutableUInt32.MSB_INDEX - 1] == ONE && high[MutableUInt32.MSB_INDEX - 1] == ZERO) {
                    val nextEncodingBit = getBits(bits, 1)
                    numEncodedBits += 1

                    low[MutableUInt32.MSB_INDEX - 1] = low[MutableUInt32.MSB_INDEX]
                    high[MutableUInt32.MSB_INDEX - 1] = high[MutableUInt32.MSB_INDEX]
                    encoded[MutableUInt32.MSB_INDEX - 1] = encoded[MutableUInt32.MSB_INDEX]

                    low = low shl 1
                    high = high shl 1 or ONE
                    encoded = encoded shl 1 or nextEncodingBit

                    takenOut.append(toBeTakenOut[0])
                    toBeTakenOut = toBeTakenOut.substring(1) + nextEncodingBit.toString()
                } else {
                    break;
                }

                if (numEncodedBits >= maxBits) {
                    if (completeSentence) {
                        // In this case we only finish the encoding once we encounter a '.'
                        // TODO: Consider whether we want to support other sentence ending characters
                        //   like '?', '!'
                        if (covertext.last() == '.') {
                            done = true
                            break
                        }
                    } else {
                        done = true
                        break
                    }
                }
            }

            iterationCount += 1
            println("Iteration ${iterationCount}: (${numEncodedBits} / ${maxBits})")
        }

        println("Encoded bits: $takenOut")

        // If we are expected to only return decodable cover texts, we must
        // check that
        if (returnOnlyDecodable) {
            // Decoding will work as long as the tokenization of the cover text is unique.
            // We check this by tokenizing the generated cover text and comparing against
            // the tokens we generated during the encoding process.
            if (languageModel.tokenizer.tokenize(covertext) != tokenIds) {
                println("Failed cover text is: $covertext")
                return null
            }
        }

        // Return the result
        return covertext
    }

    /**
     * Decode the cover text
     *
     * This method uses a fixed width arithmetic encoding scheme to generate the cover text. See
     * the documentation for the [encode] method for additional references for the scheme.
     *
     * @param coverText The cover text to decode
     * @return The decoded plain text or null if decoding fails
     */
    private fun decodeFixedWidth(coverText: String): String? {
        // The low bitrange for arithmetic encoding.
        var low: MutableUInt32 = MutableUInt32.MIN_VALUE

        // The high bitrange for arithmetic encoding.
        var high: MutableUInt32 = MutableUInt32.MAX_VALUE

        // The (eventual) ciphertext, encoded as an arbitrary precision integer.
        var encoded: BigInteger = 0.toBigInteger()

        //The number of encoded bits.
        var totalEncoded: Int = 0
        var underflowCounter: Int = 0

        // Given the seed and cover text, compute the (cumulative) probability ranges for each token
        // in the latter.
        for ((cumulativeProb, prevCumulativeProb) in computeTokenProbabilityRanges(coverText)) {
            val bitrange = getFixedWidthBitRange(low, high)
            val (newLow, newHigh) = adjustFixedWidth(
                low,
                bitrange,
                prevCumulativeProb,
                cumulativeProb
            )
            low = newLow
            high = newHigh
            //println("Low: ${low}, High: ${high}, Encoded: ${encoded}")

            // Extract the bits
            while (true) {
                if (low[MutableUInt32.MSB_INDEX] == high[MutableUInt32.MSB_INDEX]) {
                    val value = low[MutableUInt32.MSB_INDEX]
                    encoded = (encoded shl 1) + BigInteger(value.toString())
                    totalEncoded += 1

                    while (underflowCounter > 0) {
                        val notValue = if (value > ZERO) 0 else 1
                        encoded = (encoded shl 1) + notValue.toBigInteger()
                        totalEncoded += 1
                        underflowCounter -= 1
                    }

                    low = low shl 1
                    high = (high shl 1) or ONE
                } else if (low[MutableUInt32.MSB_INDEX - 1] == ONE && high[MutableUInt32.MSB_INDEX - 1] == ZERO) {
                    low[MutableUInt32.MSB_INDEX - 1] = low[MutableUInt32.MSB_INDEX]
                    high[MutableUInt32.MSB_INDEX - 1] = high[MutableUInt32.MSB_INDEX]

                    low = low shl 1
                    high = (high shl 1) or ONE
                    underflowCounter += 1
                } else {
                    break
                }
            }
        }

        // DEBUG: Print out the bytes
        val bytes = convertToBytes(encoded, totalEncoded)

        println("Decoded bits: ${toBitString(bytes)}")
        println("Decoded Bytes: ${bytes.contentToString()}")

        // Try to decrypt the converted bytes and return
        return tryDecrypt(encoded, totalEncoded)
    }

    /**
     * Given the prompt and cover text, determine the cumulative probability range in which
     * each token in the cover text falls. The returned list is in the order of the tokens
     * of the cover text.
     *
     * @param coverText The cover text
     * @param prompt The prompt used to generate the cover text
     * @return A list of pairs, one for each token of the cover text, where each pair indicates
     *     the range of cumulative probability in to which the respective token falls.
     */
    private fun computeTokenProbabilityRanges(coverText: String): List<Pair<Double, Double>> {
        var lastToken: String? = null
        var past: Any? = null

        // Tokenize the cover text
        val results = mutableListOf<Pair<Double, Double>>()
        val tokenIds = languageModel.tokenizer.tokenize(coverText)

        for (tokenId in tokenIds) {
            val predictionResult = if (past == null) {
                languageModel.prediction(prompt, null)
            } else {
                require(lastToken != null)
                languageModel.prediction(lastToken, past)
            }

            past = predictionResult.past

            val cumulativeProbabilities = predictionResult.predictions
            for ((j, tokenProb) in cumulativeProbabilities.withIndex()) {
                if (tokenProb.tokenId == tokenId) {
                    lastToken = languageModel.tokenizer.getToken(tokenId)

                    val prevProb = if (j > 0) {
                        cumulativeProbabilities[j - 1].probability
                    } else {
                        0.0
                    }

                    results.add(Pair(tokenProb.probability, prevProb))
                }
            }
        }
        return results
    }

    /**
     * Try to decrypt the encoded bits using the specified [cryptoSystem]
     *
     * @param encoded The encoded bits as a [BigInteger]
     * @param totalEncoded The number of encoded bits
     * @return The decoded value or null if decoding fails
     */
    private fun tryDecrypt(encoded: BigInteger, totalEncoded: Int): String? {
        if (totalEncoded / 8 < cryptoSystem.numBytesToCheck) {
            return null
        }

        val enc = convertToBytes(encoded, totalEncoded)

        val finish = cryptoSystem.beginDecrypt(
            enc.sliceArray(0 until cryptoSystem.numBytesToCheck)
        )
        if (finish != null) {
            for (i in cryptoSystem.numBytesToCheck..enc.lastIndex) {
                val decrypted =
                    finish(enc.sliceArray(cryptoSystem.numBytesToCheck..i))
                if (decrypted != null)
                    return decrypted.toString(Charsets.UTF_8)
            }
        }

        return null
    }

    private fun convertToBytes(value: BigInteger, totalEncoded: Int): ByteArray {
        val adjustedValue = if (totalEncoded % 8 != 0) value shl (8 - totalEncoded % 8) else value

        val bytes = mutableListOf<Byte>()

        var current = adjustedValue
        while (current > BigInteger.ZERO) {
            val byte = current.mod(256.toBigInteger())
            bytes.add(byte.toByte())
            current = current.shr(8)
        }

        val valueAsBytes = bytes.reversed().toByteArray()

        val prefixLength = ceil(totalEncoded.toDouble() / 8.toDouble()).toInt() - valueAsBytes.size
        require(prefixLength >= 0)

        val prefix = ByteArray(prefixLength)

        return prefix + valueAsBytes
    }

    private fun getFixedWidthBitRange(low: MutableUInt32, high: MutableUInt32): MutableUInt32 {
        require(high >= low)
        return if (high - low != MutableUInt32.MAX_VALUE)
            high - low + ONE
        else
            MutableUInt32.MAX_VALUE
    }

    private fun adjustFixedWidth(
        low: MutableUInt32, bitrange: MutableUInt32, previousCumulativeProb: Double,
        cumulativeProb: Double
    ): Pair<MutableUInt32, MutableUInt32> {
        val range = bitrange.toDouble().toBigDecimal()
        val newHigh = low + (range * cumulativeProb.toBigDecimal()).floor().toMutableUInt32()
        val newLow = low + (range * previousCumulativeProb.toBigDecimal()).floor().toMutableUInt32()
        require(newHigh >= newLow)
        return Pair(newLow, newHigh)
    }

    fun getNthBit(value: UInt, position: Int): UInt {
        return (value shr position) and 1u;
    }

    fun setNthBit(value: UInt, position: Int, turnOn: Boolean): UInt {
        val mask = 1u shl position
        return if (turnOn) value or mask else value and mask.inv()
    }

    /**
     * Generate a sequence of bits based on [bytes]. Once the bytes
     * run out, it currently generates a sequence of random bits
     */
    private fun bitGen(bytes: ByteArray): Sequence<UInt> {
        return sequence {
            var i = 0
            var offset = 0
            while (i < bytes.size) {
                val bit = (bytes[i].toUInt() shr (7 - offset)) % 2.toUInt()
                yield(bit)

                offset += 1
                i += offset / 8
                offset %= 8
            }

            yieldAll(generateSequence { Random.Default.nextBits(1).toUInt() })
        }
    }

    /** Extract the specified number of bits from [bits], combine and return it as [MutableUInt32] value */
    private fun getBits(bits: Iterator<UInt>, n: Int): MutableUInt32 {
        var v = 0u
        for (i in 0 until n) {
            v = (v shl 1) + bits.next()
        }

        return v.toMutableUInt32()
    }

    private fun toBitString(value: MutableUInt32): String {
        val bytes = value.toBigInteger().toByteArray()
        return bytes.joinToString(separator = "") { toBitString(it) }
    }

    private fun toBitString(bytes: ByteArray): String {
        return bytes.joinToString(separator = "") { toBitString(it) }
    }

    private fun toBitString(byte: Byte): String {
        return buildString(8) {
            val binaryStr = byte.toUByte().toString(2)
            val countOfLeadingZeros = 8 - binaryStr.count()
            repeat(countOfLeadingZeros) {
                append(0)
            }
            append(binaryStr)
        }
    }

    companion object {
        /*
         * The number of extra bits to encode to avoid potential decoding errors while
         * using variable bitwidth arithmetic encoding."""
         */
        private const val FIXED_POINT_EXTRA_BITS: Int = 8

        /** Representation of zero as [MutableUInt32] */
        private val ZERO = MutableUInt32(0)

        /** Representation of one as [MutableUInt32] */
        private val ONE = MutableUInt32(1)
    }
}

/**
 * Represents the result of successfully encoding the input text along
 * with additional information. Currently, this includes the generated
 * [coverText] as well as the number of [attempts] that were required
 * before we successfully encoded the value
 */
data class EncodedValue(val coverText: String, val attempts: Int)