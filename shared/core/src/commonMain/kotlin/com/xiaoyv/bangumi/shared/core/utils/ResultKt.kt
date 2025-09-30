package com.xiaoyv.bangumi.shared.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.annotation.OrbitDsl

@OrbitDsl
suspend inline fun <T> runResult(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    crossinline block: suspend () -> T,
) = withContext(dispatcher) {
    runCatching { block() }
}

inline fun <T> Result<T>.onCompletion(block: (T?) -> Unit): Result<T> {
    block(getOrNull())
    return this
}

data class ResultZip2<T1, T2>(val data1: T1, val data2: T2)
data class ResultZip3<T1, T2, T3>(val data1: T1, val data2: T2, val data3: T3)
data class ResultZip4<T1, T2, T3, T4>(val data1: T1, val data2: T2, val data3: T3, val data4: T4)
data class ResultZip5<T1, T2, T3, T4, T5>(val data1: T1, val data2: T2, val data3: T3, val data4: T4, val data5: T5)
data class ResultZip6<T1, T2, T3, T4, T5, T6>(val data1: T1, val data2: T2, val data3: T3, val data4: T4, val data5: T5, val data6: T6)
data class ResultZip7<T1, T2, T3, T4, T5, T6, T7>(
    val data1: T1,
    val data2: T2,
    val data3: T3,
    val data4: T4,
    val data5: T5,
    val data6: T6,
    val data7: T7,
)

data class ResultZip8<T1, T2, T3, T4, T5, T6, T7, T8>(
    val data1: T1,
    val data2: T2,
    val data3: T3,
    val data4: T4,
    val data5: T5,
    val data6: T6,
    val data7: T7,
    val data8: T8,
)

suspend fun <T1, T2> awaitAll(
    block1: suspend () -> Result<T1>,
    block2: suspend () -> Result<T2>,
    transform: (T1, T2) -> ResultZip2<T1, T2> = { data1, data2 -> ResultZip2(data1, data2) },
): Result<ResultZip2<T1, T2>> = coroutineScope {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val data1 = deferred1.await()
    val data2 = deferred2.await()
    if (data1.isSuccess && data2.isSuccess) {
        Result.success(transform(data1.getOrThrow(), data2.getOrThrow()))
    } else {
        val error = data1.exceptionOrNull() ?: data2.exceptionOrNull()
        Result.failure(error ?: Exception("Unknown error"))
    }
}

suspend fun <T1, T2, T3> awaitAll(
    block1: suspend () -> Result<T1>,
    block2: suspend () -> Result<T2>,
    block3: suspend () -> Result<T3>,
    transform: (T1, T2, T3) -> ResultZip3<T1, T2, T3> = { data1, data2, data3 -> ResultZip3(data1, data2, data3) },
): Result<ResultZip3<T1, T2, T3>> = coroutineScope {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val deferred3 = async { block3() }
    val data1 = deferred1.await()
    val data2 = deferred2.await()
    val data3 = deferred3.await()
    if (data1.isSuccess && data2.isSuccess && data3.isSuccess) {
        Result.success(transform(data1.getOrThrow(), data2.getOrThrow(), data3.getOrThrow()))
    } else {
        val error = data1.exceptionOrNull() ?: data2.exceptionOrNull() ?: data3.exceptionOrNull()
        Result.failure(error ?: Exception("Unknown error"))
    }
}

suspend fun <T1, T2, T3, T4> awaitAll(
    block1: suspend () -> Result<T1>,
    block2: suspend () -> Result<T2>,
    block3: suspend () -> Result<T3>,
    block4: suspend () -> Result<T4>,
    transform: (T1, T2, T3, T4) -> ResultZip4<T1, T2, T3, T4> = { d1, d2, d3, d4 -> ResultZip4(d1, d2, d3, d4) },
): Result<ResultZip4<T1, T2, T3, T4>> = coroutineScope {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val deferred3 = async { block3() }
    val deferred4 = async { block4() }

    val data1 = deferred1.await()
    val data2 = deferred2.await()
    val data3 = deferred3.await()
    val data4 = deferred4.await()

    if (data1.isSuccess && data2.isSuccess && data3.isSuccess && data4.isSuccess) {
        Result.success(transform(data1.getOrThrow(), data2.getOrThrow(), data3.getOrThrow(), data4.getOrThrow()))
    } else {
        val error = data1.exceptionOrNull() ?: data2.exceptionOrNull() ?: data3.exceptionOrNull() ?: data4.exceptionOrNull()
        Result.failure(error ?: Exception("Unknown error"))
    }
}

suspend fun <T1, T2, T3, T4, T5> awaitAll(
    block1: suspend () -> Result<T1>,
    block2: suspend () -> Result<T2>,
    block3: suspend () -> Result<T3>,
    block4: suspend () -> Result<T4>,
    block5: suspend () -> Result<T5>,
    transform: (T1, T2, T3, T4, T5) -> ResultZip5<T1, T2, T3, T4, T5> = { d1, d2, d3, d4, d5 -> ResultZip5(d1, d2, d3, d4, d5) },
): Result<ResultZip5<T1, T2, T3, T4, T5>> = coroutineScope {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val deferred3 = async { block3() }
    val deferred4 = async { block4() }
    val deferred5 = async { block5() }

    val data1 = deferred1.await()
    val data2 = deferred2.await()
    val data3 = deferred3.await()
    val data4 = deferred4.await()
    val data5 = deferred5.await()

    if (data1.isSuccess && data2.isSuccess && data3.isSuccess && data4.isSuccess && data5.isSuccess) {
        Result.success(transform(data1.getOrThrow(), data2.getOrThrow(), data3.getOrThrow(), data4.getOrThrow(), data5.getOrThrow()))
    } else {
        val error = data1.exceptionOrNull() ?: data2.exceptionOrNull() ?: data3.exceptionOrNull() ?: data4.exceptionOrNull()
        ?: data5.exceptionOrNull()
        Result.failure(error ?: Exception("Unknown error"))
    }
}

suspend fun <T1, T2, T3, T4, T5, T6> awaitAll(
    block1: suspend () -> Result<T1>,
    block2: suspend () -> Result<T2>,
    block3: suspend () -> Result<T3>,
    block4: suspend () -> Result<T4>,
    block5: suspend () -> Result<T5>,
    block6: suspend () -> Result<T6>,
    transform: (T1, T2, T3, T4, T5, T6) -> ResultZip6<T1, T2, T3, T4, T5, T6> = { d1, d2, d3, d4, d5, d6 ->
        ResultZip6(
            data1 = d1,
            data2 = d2,
            data3 = d3,
            data4 = d4,
            data5 = d5,
            data6 = d6
        )
    },
): Result<ResultZip6<T1, T2, T3, T4, T5, T6>> = coroutineScope {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val deferred3 = async { block3() }
    val deferred4 = async { block4() }
    val deferred5 = async { block5() }
    val deferred6 = async { block6() }

    val data1 = deferred1.await()
    val data2 = deferred2.await()
    val data3 = deferred3.await()
    val data4 = deferred4.await()
    val data5 = deferred5.await()
    val data6 = deferred6.await()

    if (data1.isSuccess && data2.isSuccess && data3.isSuccess && data4.isSuccess && data5.isSuccess && data6.isSuccess) {
        Result.success(
            transform(
                data1.getOrThrow(),
                data2.getOrThrow(),
                data3.getOrThrow(),
                data4.getOrThrow(),
                data5.getOrThrow(),
                data6.getOrThrow()
            )
        )
    } else {
        val error = data1.exceptionOrNull() ?: data2.exceptionOrNull() ?: data3.exceptionOrNull() ?: data4.exceptionOrNull()
        ?: data5.exceptionOrNull() ?: data6.exceptionOrNull()
        Result.failure(error ?: Exception("Unknown error"))
    }
}

suspend fun <T1, T2, T3, T4, T5, T6, T7> awaitAll(
    block1: suspend () -> Result<T1>,
    block2: suspend () -> Result<T2>,
    block3: suspend () -> Result<T3>,
    block4: suspend () -> Result<T4>,
    block5: suspend () -> Result<T5>,
    block6: suspend () -> Result<T6>,
    block7: suspend () -> Result<T7>,
    transform: (T1, T2, T3, T4, T5, T6, T7) -> ResultZip7<T1, T2, T3, T4, T5, T6, T7> = { d1, d2, d3, d4, d5, d6, d7 ->
        ResultZip7(
            data1 = d1,
            data2 = d2,
            data3 = d3,
            data4 = d4,
            data5 = d5,
            data6 = d6,
            data7 = d7
        )
    },
): Result<ResultZip7<T1, T2, T3, T4, T5, T6, T7>> = coroutineScope {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val deferred3 = async { block3() }
    val deferred4 = async { block4() }
    val deferred5 = async { block5() }
    val deferred6 = async { block6() }
    val deferred7 = async { block7() }

    val data1 = deferred1.await()
    val data2 = deferred2.await()
    val data3 = deferred3.await()
    val data4 = deferred4.await()
    val data5 = deferred5.await()
    val data6 = deferred6.await()
    val data7 = deferred7.await()

    if (data1.isSuccess && data2.isSuccess && data3.isSuccess && data4.isSuccess && data5.isSuccess && data6.isSuccess && data7.isSuccess) {
        Result.success(
            transform(
                data1.getOrThrow(),
                data2.getOrThrow(),
                data3.getOrThrow(),
                data4.getOrThrow(),
                data5.getOrThrow(),
                data6.getOrThrow(),
                data7.getOrThrow()
            )
        )
    } else {
        val error = data1.exceptionOrNull() ?: data2.exceptionOrNull() ?: data3.exceptionOrNull() ?: data4.exceptionOrNull()
        ?: data5.exceptionOrNull() ?: data6.exceptionOrNull() ?: data7.exceptionOrNull()
        Result.failure(error ?: Exception("Unknown error"))
    }
}

suspend fun <T1, T2, T3, T4, T5, T6, T7, T8> awaitAll(
    block1: suspend () -> Result<T1>,
    block2: suspend () -> Result<T2>,
    block3: suspend () -> Result<T3>,
    block4: suspend () -> Result<T4>,
    block5: suspend () -> Result<T5>,
    block6: suspend () -> Result<T6>,
    block7: suspend () -> Result<T7>,
    block8: suspend () -> Result<T8>,
    transform: (T1, T2, T3, T4, T5, T6, T7, T8) -> ResultZip8<T1, T2, T3, T4, T5, T6, T7, T8> = { d1, d2, d3, d4, d5, d6, d7, d8 ->
        ResultZip8(
            data1 = d1,
            data2 = d2,
            data3 = d3,
            data4 = d4,
            data5 = d5,
            data6 = d6,
            data7 = d7,
            data8 = d8
        )
    },
): Result<ResultZip8<T1, T2, T3, T4, T5, T6, T7, T8>> = coroutineScope {
    val deferred1 = async { block1() }
    val deferred2 = async { block2() }
    val deferred3 = async { block3() }
    val deferred4 = async { block4() }
    val deferred5 = async { block5() }
    val deferred6 = async { block6() }
    val deferred7 = async { block7() }
    val deferred8 = async { block8() }

    val data1 = deferred1.await()
    val data2 = deferred2.await()
    val data3 = deferred3.await()
    val data4 = deferred4.await()
    val data5 = deferred5.await()
    val data6 = deferred6.await()
    val data7 = deferred7.await()
    val data8 = deferred8.await()

    if (data1.isSuccess && data2.isSuccess && data3.isSuccess && data4.isSuccess && data5.isSuccess && data6.isSuccess && data7.isSuccess && data8.isSuccess) {
        Result.success(
            transform(
                data1.getOrThrow(),
                data2.getOrThrow(),
                data3.getOrThrow(),
                data4.getOrThrow(),
                data5.getOrThrow(),
                data6.getOrThrow(),
                data7.getOrThrow(),
                data8.getOrThrow()
            )
        )
    } else {
        val error = data1.exceptionOrNull() ?: data2.exceptionOrNull() ?: data3.exceptionOrNull() ?: data4.exceptionOrNull()
        ?: data5.exceptionOrNull() ?: data6.exceptionOrNull() ?: data7.exceptionOrNull() ?: data8.exceptionOrNull()
        Result.failure(error ?: Exception("Unknown error"))
    }
}
