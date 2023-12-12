package com.xiaoyv.bangumi.ui.discover.index.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.discover.index.IndexAdapter
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.widget.menu.AnimeSectionView

/**
 * Class: [IndexTitleBinder]
 *
 * @author why
 * @since 12/12/23
 */
class IndexTitleBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<IndexAdapter.IndexItem, RecyclerView.ViewHolder> {
    override fun onBind(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: IndexAdapter.IndexItem?
    ) {
        item ?: return
        (holder.itemView as AnimeSectionView).apply {
            title = item.entity.forceCast<Pair<String, Boolean>>().first
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = object : RecyclerView.ViewHolder(AnimeSectionView(context).apply {
        layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }) {
    }

    override fun isFullSpanItem(itemType: Int): Boolean {
        return true
    }
}