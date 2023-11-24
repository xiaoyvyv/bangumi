package com.xiaoyv.common.kts

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
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