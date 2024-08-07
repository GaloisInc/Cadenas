package com.galois.cadenas.utils

data class Tuple1<T1>(val _1: T1)
fun <T1> tupleOf(_1: T1) = Tuple1(_1)
fun <T> Tuple1<T>.toList() = listOf(_1)
fun <T1> Tuple1<T1>.toMutableTuple() = mutableTupleOf(_1)
fun <T1> Tuple1<T1>.size() = 1

data class MutableTuple1<T1>(var _1: T1)
fun <T1> mutableTupleOf(_1: T1) = MutableTuple1(_1)
fun <T> MutableTuple1<T>.toList() = listOf(_1)
fun <T1> MutableTuple1<T1>.toTuple() = tupleOf(_1)
fun <T1> MutableTuple1<T1>.size() = 1

data class Tuple2<T1, T2>(val _1: T1, val _2: T2)
fun <T1, T2> tupleOf(_1: T1, _2: T2) = Tuple2(_1, _2)
fun <T> Tuple2<T, T>.toList() = listOf(_1, _2)
fun <T1, T2> Tuple2<T1, T2>.toMutableTuple() = mutableTupleOf(_1, _2)
fun <T1, T2> Tuple2<T1, T2>.size() = 2

data class MutableTuple2<T1, T2>(var _1: T1, var _2: T2)
fun <T1, T2> mutableTupleOf(_1: T1, _2: T2) = MutableTuple2(_1, _2)
fun <T> MutableTuple2<T, T>.toList() = listOf(_1, _2)
fun <T1, T2> MutableTuple2<T1, T2>.toTuple() = tupleOf(_1, _2)
fun <T1, T2> MutableTuple2<T1, T2>.size() = 2

data class Tuple3<T1, T2, T3>(val _1: T1, val _2: T2, val _3: T3)
fun <T1, T2, T3> tupleOf(_1: T1, _2: T2, _3: T3) = Tuple3(_1, _2, _3)
fun <T> Tuple3<T, T, T>.toList() = listOf(_1, _2, _3)
fun <T1, T2, T3> Tuple3<T1, T2, T3>.toMutableTuple() = mutableTupleOf(_1, _2, _3)
fun <T1, T2, T3> Tuple3<T1, T2, T3>.size() = 3

data class MutableTuple3<T1, T2, T3>(var _1: T1, var _2: T2, var _3: T3)
fun <T1, T2, T3> mutableTupleOf(_1: T1, _2: T2, _3: T3) = MutableTuple3(_1, _2, _3)
fun <T> MutableTuple3<T, T, T>.toList() = listOf(_1, _2, _3)
fun <T1, T2, T3> MutableTuple3<T1, T2, T3>.toTuple() = tupleOf(_1, _2, _3)
fun <T1, T2, T3> MutableTuple3<T1, T2, T3>.size() = 3

data class Tuple4<T1, T2, T3, T4>(val _1: T1, val _2: T2, val _3: T3, val _4: T4)
fun <T1, T2, T3, T4> tupleOf(_1: T1, _2: T2, _3: T3, _4: T4) = Tuple4(_1, _2, _3, _4)
fun <T> Tuple4<T, T, T, T>.toList() = listOf(_1, _2, _3, _4)
fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.toMutableTuple() = mutableTupleOf(_1, _2, _3, _4)
fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.size() = 4

data class MutableTuple4<T1, T2, T3, T4>(var _1: T1, var _2: T2, var _3: T3, var _4: T4)
fun <T1, T2, T3, T4> mutableTupleOf(_1: T1, _2: T2, _3: T3, _4: T4) = MutableTuple4(_1, _2, _3, _4)
fun <T> MutableTuple4<T, T, T, T>.toList() = listOf(_1, _2, _3, _4)
fun <T1, T2, T3, T4> MutableTuple4<T1, T2, T3, T4>.toTuple() = tupleOf(_1, _2, _3, _4)
fun <T1, T2, T3, T4> MutableTuple4<T1, T2, T3, T4>.size() = 4

data class Tuple5<T1, T2, T3, T4, T5>(val _1: T1, val _2: T2, val _3: T3, val _4: T4, val _5: T5)
fun <T1, T2, T3, T4, T5> tupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5) = Tuple5(_1, _2, _3, _4, _5)
fun <T> Tuple5<T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5)
fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.toMutableTuple() = mutableTupleOf(_1, _2, _3, _4, _5)
fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.size() = 5

data class MutableTuple5<T1, T2, T3, T4, T5>(var _1: T1, var _2: T2, var _3: T3, var _4: T4, var _5: T5)
fun <T1, T2, T3, T4, T5> mutableTupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5) = MutableTuple5(_1, _2, _3, _4, _5)
fun <T> MutableTuple5<T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5)
fun <T1, T2, T3, T4, T5> MutableTuple5<T1, T2, T3, T4, T5>.toTuple() = tupleOf(_1, _2, _3, _4, _5)
fun <T1, T2, T3, T4, T5> MutableTuple5<T1, T2, T3, T4, T5>.size() = 5

data class Tuple6<T1, T2, T3, T4, T5, T6>(val _1: T1, val _2: T2, val _3: T3, val _4: T4, val _5: T5, val _6: T6)
fun <T1, T2, T3, T4, T5, T6> tupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6) = Tuple6(_1, _2, _3, _4, _5, _6)
fun <T> Tuple6<T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6)
fun <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6>.toMutableTuple() = mutableTupleOf(_1, _2, _3, _4, _5, _6)
fun <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6>.size() = 6

data class MutableTuple6<T1, T2, T3, T4, T5, T6>(var _1: T1, var _2: T2, var _3: T3, var _4: T4, var _5: T5, var _6: T6)
fun <T1, T2, T3, T4, T5, T6> mutableTupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6) = MutableTuple6(_1, _2, _3, _4, _5, _6)
fun <T> MutableTuple6<T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6)
fun <T1, T2, T3, T4, T5, T6> MutableTuple6<T1, T2, T3, T4, T5, T6>.toTuple() = tupleOf(_1, _2, _3, _4, _5, _6)
fun <T1, T2, T3, T4, T5, T6> MutableTuple6<T1, T2, T3, T4, T5, T6>.size() = 6

data class Tuple7<T1, T2, T3, T4, T5, T6, T7>(val _1: T1, val _2: T2, val _3: T3, val _4: T4, val _5: T5, val _6: T6, val _7: T7)
fun <T1, T2, T3, T4, T5, T6, T7> tupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6, _7: T7) = Tuple7(_1, _2, _3, _4, _5, _6, _7)
fun <T> Tuple7<T, T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6, _7)
fun <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7>.toMutableTuple() = mutableTupleOf(_1, _2, _3, _4, _5, _6, _7)
fun <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7>.size() = 7

data class MutableTuple7<T1, T2, T3, T4, T5, T6, T7>(var _1: T1, var _2: T2, var _3: T3, var _4: T4, var _5: T5, var _6: T6, var _7: T7)
fun <T1, T2, T3, T4, T5, T6, T7> mutableTupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6, _7: T7) = MutableTuple7(_1, _2, _3, _4, _5, _6, _7)
fun <T> MutableTuple7<T, T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6, _7)
fun <T1, T2, T3, T4, T5, T6, T7> MutableTuple7<T1, T2, T3, T4, T5, T6, T7>.toTuple() = tupleOf(_1, _2, _3, _4, _5, _6, _7)
fun <T1, T2, T3, T4, T5, T6, T7> MutableTuple7<T1, T2, T3, T4, T5, T6, T7>.size() = 7

data class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>(val _1: T1, val _2: T2, val _3: T3, val _4: T4, val _5: T5, val _6: T6, val _7: T7, val _8: T8)
fun <T1, T2, T3, T4, T5, T6, T7, T8> tupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6, _7: T7, _8: T8) = Tuple8(_1, _2, _3, _4, _5, _6, _7, _8)
fun <T> Tuple8<T, T, T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6, _7, _8)
fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>.toMutableTuple() = mutableTupleOf(_1, _2, _3, _4, _5, _6, _7, _8)
fun <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8>.size() = 8

data class MutableTuple8<T1, T2, T3, T4, T5, T6, T7, T8>(var _1: T1, var _2: T2, var _3: T3, var _4: T4, var _5: T5, var _6: T6, var _7: T7, var _8: T8)
fun <T1, T2, T3, T4, T5, T6, T7, T8> mutableTupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6, _7: T7, _8: T8) = MutableTuple8(_1, _2, _3, _4, _5, _6, _7, _8)
fun <T> MutableTuple8<T, T, T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6, _7, _8)
fun <T1, T2, T3, T4, T5, T6, T7, T8> MutableTuple8<T1, T2, T3, T4, T5, T6, T7, T8>.toTuple() = tupleOf(_1, _2, _3, _4, _5, _6, _7, _8)
fun <T1, T2, T3, T4, T5, T6, T7, T8> MutableTuple8<T1, T2, T3, T4, T5, T6, T7, T8>.size() = 8

data class Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(val _1: T1, val _2: T2, val _3: T3, val _4: T4, val _5: T5, val _6: T6, val _7: T7, val _8: T8, val _9: T9)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> tupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6, _7: T7, _8: T8, _9: T9) = Tuple9(_1, _2, _3, _4, _5, _6, _7, _8, _9)
fun <T> Tuple9<T, T, T, T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6, _7, _8, _9)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toMutableTuple() = mutableTupleOf(_1, _2, _3, _4, _5, _6, _7, _8, _9)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.size() = 9

data class MutableTuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>(var _1: T1, var _2: T2, var _3: T3, var _4: T4, var _5: T5, var _6: T6, var _7: T7, var _8: T8, var _9: T9)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> mutableTupleOf(_1: T1, _2: T2, _3: T3, _4: T4, _5: T5, _6: T6, _7: T7, _8: T8, _9: T9) = MutableTuple9(_1, _2, _3, _4, _5, _6, _7, _8, _9)
fun <T> MutableTuple9<T, T, T, T, T, T, T, T, T>.toList() = listOf(_1, _2, _3, _4, _5, _6, _7, _8, _9)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> MutableTuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.toTuple() = tupleOf(_1, _2, _3, _4, _5, _6, _7, _8, _9)
fun <T1, T2, T3, T4, T5, T6, T7, T8, T9> MutableTuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9>.size() = 9
