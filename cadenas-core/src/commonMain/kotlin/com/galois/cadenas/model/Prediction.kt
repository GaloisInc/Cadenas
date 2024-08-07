package com.galois.cadenas.model

/**
 * The result of predicting the next token using a [LanguageModel]
 *
 * @property predictions All [TokenProbability] values for the prediction.
 * @property past A representation of the [LanguageModel]'s prediction history.
 */
data class Prediction(val predictions: List<TokenProbability>, val past: Any) {
    /**
     * Alternate constructor that creates an instance given an [Iterable] of token id - probability
     * [Pair]s and past prediction data.
     *
     * @param values An [Iterable] of [Pair]s consisting of tokens and their associated probability.
     * @param past A representation of the [LanguageModel]'s prediction history.
     */
    constructor(values: Iterable<Pair<Int, Double>>, past: Any)
            : this (values.map(::TokenProbability), past)
}