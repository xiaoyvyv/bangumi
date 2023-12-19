package com.xiaoyv.bangumi.ui.discover.mono.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.widget.menu.AnimeSectionView
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [MonoHeaderBinder]
 *
 * @author why
 * @since 12/12/23
 */
class MonoHeaderBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, RecyclerView.ViewHolder> {
    override fun onBind(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: AdapterTypeItem?,
    ) {
        item ?: return
        (holder.itemView as AnimeSectionView).apply {
            title = item.entity.toString()
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ) = object : RecyclerView.ViewHolder(AnimeSectionView(context).apply {
        leftPadding = 8.dpi
        layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }) {}

    override fun isFullSpanItem(itemType: Int): Boolean {
        return true
    }
}