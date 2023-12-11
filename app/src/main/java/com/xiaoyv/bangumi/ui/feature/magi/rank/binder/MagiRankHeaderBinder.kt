package com.xiaoyv.bangumi.ui.feature.magi.rank.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.ui.feature.magi.rank.MagiRankAdapter
import com.xiaoyv.common.widget.menu.AnimeSectionView

/**
 * Class: [MagiRankHeaderBinder]
 *
 * @author why
 * @since 12/12/23
 */
class MagiRankHeaderBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<MagiRankAdapter.Item, RecyclerView.ViewHolder> {
    override fun onBind(
        holder: RecyclerView.ViewHolder,
        position: Int,
        item: MagiRankAdapter.Item?
    ) {
        item ?: return
        (holder.itemView as AnimeSectionView).apply {
            title = item.entity.toString()
            more = ""
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