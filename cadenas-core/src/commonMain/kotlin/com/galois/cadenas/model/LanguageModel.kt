package com.galois.cadenas.model

interface LanguageModel {
    interface Tokenizer {
        /**
         * Tokenize the given string and return a list of token ids
         *
         * @param text The string to tokenize
         * @return A list of token ids
         */
        fun tokenize(text: String): List<Int>

        /**
         * Return the text corresponding to the list of token ids
         *
         * @param tokens A list of token ids.
         * @return The token text corresponding to the list of tokens.
         */
        fun untokenize(tokens: List<Int>): String

        /**
         * Return the token string associated with the given token ID
         *
         * @param tokenId The token ID
         * @return The token string corresponding to [`tokenId`]
         */
        fun getToken(tokenId: Int): String

        /**
         * Return the size of the vocabulary
         *
         * @return The vocabulary size
         */
        fun vocabSize(): Int
    }

    /**
     * The tokenizer associated with this [LanguageModel]
     */
    val tokenizer: Tokenizer

    /**
     * Generate predictions for the next token given a seed text
     * and past data.
     *
     * @param text The seed text. When passing in a non-null [past] value, the
     *     seed text should not contain any text that went in to the creation of
     *     the [past] value. That is, if:
     *       pred1 = prediction(initial_seed, null)
     *       pred2 = prediction(seed, pred1.past)
     *     `seed` should not include `initial_seed`.
     * @param past Optional past data. Valid [past] data must have been generated
     *     by a previous call to [prediction].
     * @return The results of the prediction. See [Prediction].
     */
    fun prediction(text: String, past: Any?): Prediction

    /**
     * Destroy the LanguageModel.
     *
     * NOTE: This method should only be overwritten for LanguageModels that
     * risk leaking memory.
     */
    fun destroy() {}
}