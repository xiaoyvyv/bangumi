package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.atomicfu.AtomicInt
import kotlinx.atomicfu.atomic

interface Node<N : Node<N>> {
    val children: SerializeList<N>
}

fun <N : Node<N>> LazyListScope.nodes(
    nodes: List<N>,
    key: ((node: N) -> Any)? = null,
    contentType: (N) -> Any? = { null },
    level: Int = 0,
    content: @Composable LazyItemScope.(node: N, level: Int) -> Unit,
) {
    nodes.forEach { node ->
        item(
            key = key?.invoke(node),
            contentType = contentType(node)
        ) {
            content(node, level)
        }

        if (node.children.isNotEmpty()) {
            nodes(
                nodes = node.children,
                key = key,
                contentType = contentType,
                content = content,
                level = level + 1
            )
        }
    }
}

fun <N : Node<N>> LazyListScope.nodesIndexed(
    nodes: List<N>,
    key: ((node: N) -> Any)? = null,
    contentType: (N) -> Any? = { null },
    level: Int = 0,
    counter: AtomicInt = atomic(0),
    content: @Composable LazyItemScope.(node: N, level: Int, index: Int) -> Unit,
) {

    nodes.forEach { node ->
        val currentIndex = counter.getAndIncrement()
        item(
            key = key?.invoke(node),
            contentType = contentType(node)
        ) {
            content(node, level, currentIndex)
        }

        if (node.children.isNotEmpty()) {
            nodesIndexed(
                nodes = node.children,
                key = key,
                contentType = contentType,
                level = level + 1,
                counter = counter,
                content = content
            )
        }
    }
}



