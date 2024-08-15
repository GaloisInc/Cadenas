package com.galois.cadenas.model

/**
 * A single token prediction made by a [LanguageModel].
 *
 * @property tokenId The ID of the token in the model's vocabulary. Must be compatible with [LanguageModel.Tokenizer].
 * @property probability The probability that [tokenId] is the next token.
 */
data class TokenProbability(val tokenId: Int, val probability: Double) {
    /**
     * Alternate constructor that creates an instance from a [Pair] instance.
     *
     * @param value A [Pair] of a token id and its probability.
     */
    constructor(value: Pair<Int, Double>) : this(value.first, value.second)
}