package com.xiaoyv.bangumi.shared.core.utils

inline fun <T> Iterable<T>.forEachWithSeparator(
    onItem: (T) -> Unit,
    onSeparator: () -> Unit,
    onStart: () -> Unit = {},
    onEnd: () -> Unit = {},
) {
    onStart()
    val iterator = iterator()
    if (!iterator.hasNext()) {
        onEnd()
        return
    }

    while (true) {
        val item = iterator.next()
        onItem(item)
        if (!iterator.hasNext()) break
        onSeparator()
    }
    onEnd()
}
