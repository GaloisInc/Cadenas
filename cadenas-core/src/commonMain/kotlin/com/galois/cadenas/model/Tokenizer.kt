package com.galois.cadenas.model

import com.galois.cadenas.mbfte.EncoderDecoder
import java.nio.charset.StandardCharsets

class Tokenizer(
    private val encoder: Map<String, Int>,
    private val decoder: Map<Int, String>,
    private val bpeRanks: Map<Pair<String, String>, Int>
) : LanguageModel.Tokenizer {
    private val encodeRegex = Regex("""'s|'t|'re|'ve|'m|'ll|'d| ?\p{L}+| ?\p{N}+| ?[^\s\p{L}\p{N}]+|\s+(?!\S)|\s+""")

    override fun vocabSize(): Int {
        return encoder.size
    }

    override fun getToken(tokenId: Int): String {
        require(tokenId in decoder)
        return untokenize(listOf(tokenId))
    }

    /**
     * Return the text corresponding to the list of token ids
     *
     * @param tokens A list of token ids.
     * @return The token text corresponding to the list of tokens.
     */
    override fun untokenize(tokens: List<Int>): String {
        val text = tokens.joinToString("") { decoder.getOrDefault(it, "") }
        val utfCodepoints = text.map { EncoderDecoder.byteDecoder[it.toString()]!!.toByte() }
        return utfCodepoints.toByteArray().toString(java.nio.charset.StandardCharsets.UTF_8)
    }

    /**
     * Tokenize the given string and return a list of token ids
     *
     * @param text The string to tokenize
     * @return A list of token ids
     */
    override fun tokenize(text: String): List<Int> {
        val tokens = encodeRegex.findAll(text).map { result ->
            val tokens: MutableList<String> = mutableListOf()
            result.value.toByteArray().toString(java.nio.charset.StandardCharsets.UTF_8).map {
                // Jank: Convert character to Int, then to UByte (remove the
                // leading bits and interpret as a positive value), then back
                // to Int to use in the `EncoderDecoder` map.
                tokens.add(EncoderDecoder.byteEncoder[it.toInt().toUByte().toInt()]!!)
            }
            tokens.joinToString("")
        }

        return tokens
            .map { bpe(it) }
            .flatten()
            .map { encoder[it]!! }
            .toList()
    }

    private fun bpe(token: String): List<String> {
        if (token.length <= 1) return listOf(token)

        var word = token.map { it.toString() }
        var pairs = getPairs(word)

        while (true) {
            if (!pairs.any { bpeRanks.containsKey(it) }) break
            val (first, second) = pairs.minByOrNull { bpeRanks.getOrDefault(it, Int.MAX_VALUE) } ?: break

            var i = 0
            val newWord = mutableListOf<String>()
            while (i < word.size) {
                val j = word.withIndex().indexOfFirst { it.index >= i && it.value == first }
                if (j != -1) {
                    newWord.addAll(word.subList(i, j))
                    i = j
                } else {
                    newWord.addAll(word.subList(i, word.size))
                    break
                }

                i += if (word[i] == first && i < word.size-1 && word[i+1] == second) {
                    newWord.add(first+second)
                    2
                } else {
                    newWord.add(word[i])
                    1
                }
            }

            word = newWord
            if (word.size == 1) {
                break
            } else {
                pairs = getPairs(word)
            }
        }

        return word
    }

    private fun getPairs(word: List<String>): Set<Pair<String, String>> {
        return mutableSetOf<Pair<String, String>>().apply {
            for (i in 0 until word.size-1) {
                add(word[i] to word[i+1])
            }
        }
    }
}