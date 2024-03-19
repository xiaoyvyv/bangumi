package com.xiaoyv.common.kts

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.util.addOnDebouncedChildClick
import com.chad.library.adapter.base.util.setOnDebouncedItemClick

/**
 * @author why
 * @since 11/24/23
 */
inline fun <reified T, VH : RecyclerView.ViewHolder> BaseQuickAdapter<T, VH>.setOnDebouncedItemClickListener(
    time: Long = 500,
    crossinline block: (T) -> Unit = {},
) {
    setOnDebouncedItemClick(time) { adapter, _, position ->
        val item = adapter.getItem(position)
        if (item != null) block(item)
    }
}

inline fun <reified T, VH : RecyclerView.ViewHolder> BaseQuickAdapter<T, VH>.setOnDebouncedChildClickListener(
    @IdRes childId: Int,
    time: Long = 500,
    crossinline block: (T) -> Unit = {},
) {
    addOnDebouncedChildClick(childId, time) { adapter, _, position ->
        block(requireNotNull(adapter.getItem(position)))
    }
}

inline fun <reified T, VH : RecyclerView.ViewHolder> BaseQuickAdapter<T, VH>.setOnItemChildLongClickListener(
    @IdRes childId: Int,
    crossinline block: (View, T) -> Boolean = { _, _ -> true },
) {
    addOnItemChildLongClickListener(childId) { adapter, view, position ->
        block(view, requireNotNull(adapter.getItem(position)))
    }
}