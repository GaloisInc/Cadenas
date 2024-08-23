package com.galois.cadenas.crypto

import com.galois.cadenas.model.TestLanguageModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

/**
 * Unit Tests for the Tokenizer.
 */
class TokenizerUnitTests : StringSpec({
    val testLLM = TestLanguageModel()
    val testStrings: List<String> = listOf<String>(
        "",
        "!",
        "!!",
        " !",
        "0",
        " ",
        "#",
        "$",
        "+",
        "-",
        "--",
        "---------------------------------------------------",
        "--------------------------------------------------",
        "@",
        ".",
        "...",
        "Hello",
        "hello",
        "hell0",
        "Hello there my name is TokenizerUnitTest",
        "Hello there my name is TokenizerUnitTest.",
        "Hello there, my name is TokenizerUnitTest",
        "Hello there, my name is TokenizerUnitTest!",
        "Hello there, my name is TokenizerUnitTest !",
        "123 456",
        "123  456",
        "123   456",
        "123    456",
        "123     456",
        "123\t456",
        "123\n456",
        "123\t\t456",
        "123\n\n456",
        "123\n \n456",
        "123\n\t\n456",
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ",
    )
    val correctOutputs: List<List<Int>> = listOf<List<Int>>(
        listOf<Int>(),//""
        listOf<Int>(0),//"!"
        listOf<Int>(3228),//"!!"
        listOf<Int>(5145),//" !"
        listOf<Int>(15),//"0"
        listOf<Int>(220),//" "
        listOf<Int>(2),//"#"
        listOf<Int>(3),//"$"
        listOf<Int>(10),//"+"
        listOf<Int>(12),//"-"
        listOf<Int>(438),//"--"
        listOf<Int>(47232, 6329),//"-...-"
        listOf<Int>(47232, 438),//"-...-"
        listOf<Int>(31),//"@"
        listOf<Int>(13),//"."
        listOf<Int>(986),//"..."
        listOf<Int>(15496),//Hello
        listOf<Int>(31373),//hello
        listOf<Int>(12758, 15),//Hell0
        listOf<Int>(15496, 612, 616, 1438, 318, 29130, 7509, 26453, 14402),
        listOf<Int>(15496, 612, 616, 1438, 318, 29130, 7509, 26453, 14402, 13),
        listOf<Int>(15496, 612, 11, 616, 1438, 318, 29130, 7509, 26453, 14402),
        listOf<Int>(15496, 612, 11, 616, 1438, 318, 29130, 7509, 26453, 14402, 0),
        listOf<Int>(15496, 612, 11, 616, 1438, 318, 29130, 7509, 26453, 14402, 5145),
        listOf<Int>(10163, 604, 3980),//123 456
        listOf<Int>(10163, 220, 604, 3980),//123  456
        listOf<Int>(10163, 220, 220, 604, 3980),//123   456
        listOf<Int>(10163, 220, 220, 220, 604, 3980),//123    456
        listOf<Int>(10163, 220, 220, 220, 220, 604, 3980),//123     456
        listOf<Int>(10163, 197, 29228),//123\t456)
        listOf<Int>(10163, 198, 29228),//123\n456)
        listOf<Int>(10163, 197, 197, 29228),//123\t\t456)
        listOf<Int>(10163, 198, 198, 29228),//123\n\n456)
        listOf<Int>(10163, 198, 220, 198, 29228),//123\n \n456)
        listOf<Int>(10163, 198, 197, 198, 29228),//123\n\t\n456)
        listOf<Int>(39305, 4299, 456, 2926, 41582, 10295, 404, 80, 81, 301, 14795, 86, 5431, 89, 24694, 32988, 17511, 23852, 42, 31288, 45, 3185, 48, 49, 2257, 52, 30133, 34278, 57),//a..zA..Z
    )

    "Tokenize Strings" {
        val tsi: ListIterator<String> = testStrings.listIterator()
        val coi: ListIterator<List<Int>> = correctOutputs.listIterator()
        while (tsi.hasNext() && coi.hasNext()) {
            val inputString = tsi.next()
            val correctOutput = coi.next()
            val output = testLLM.languageModel.tokenizer.tokenize(inputString)
            //System.out.println("MYTEST: "+output+" = "+inputString)
            output shouldBe correctOutput
        }
    }

    "Untokenize to Strings" {
        val coi: ListIterator<String> = testStrings.listIterator()
        val iti: ListIterator<List<Int>> = correctOutputs.listIterator()
        while (coi.hasNext() && iti.hasNext()) {
            val correctOutput = coi.next()
            val inputTokens = iti.next()
            val output = testLLM.languageModel.tokenizer.untokenize(inputTokens)
            //System.out.println("MYTEST: "+output+" = "+inputTokens)
            output shouldBe correctOutput
        }
    }
})
