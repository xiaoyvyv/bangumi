package com.xiaoyv.common.kts

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.util.addOnDebouncedChildClick
import com.chad.library.adapter.base.util.setOnDebouncedItemClick

/**
 * @author why
 * @since 11/24/23
 */
inline fun <T, VH : RecyclerView.ViewHolder> BaseQuickAdapter<T, VH>.setOnDebouncedItemClickListener(
    time: Long = 500,
    crossinline block: (T) -> Unit = {}
) {
    setOnDebouncedItemClick(time) { adapter, _, position ->
        val item = adapter.getItem(position)
        if (item != null) block(item)
    }
}

inline fun <T, VH : RecyclerView.ViewHolder> BaseQuickAdapter<T, VH>.setOnDebouncedChildClickListener(
    @IdRes childId: Int,
    time: Long = 500,
    crossinline block: (T) -> Unit = {}
) {
    addOnDebouncedChildClick(childId, time) { adapter, _, position ->
        val item = adapter.getItem(position)
        if (item != null) block(item)
    }
}

@JvmName("setOnDebouncedChildClickKtListener")
inline fun <T, VH : RecyclerView.ViewHolder, reified CAST> BaseQuickAdapter<T, VH>.setOnDebouncedChildClickListener(
    @IdRes childId: Int,
    time: Long = 500,
    crossinline block: (CAST) -> Unit = {}
) {
    addOnDebouncedChildClick(childId, time) { adapter, _, position ->
        val item = adapter.getItem(position)
        if (item != null) block(item as CAST)
    }
}