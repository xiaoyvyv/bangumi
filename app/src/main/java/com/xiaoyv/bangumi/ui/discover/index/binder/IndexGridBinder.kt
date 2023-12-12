package com.xiaoyv.bangumi.ui.discover.index.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.updatePadding
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentIndexGridBinding
import com.xiaoyv.bangumi.ui.discover.index.IndexAdapter
import com.xiaoyv.common.api.parser.entity.IndexEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [IndexGridBinder]
 *
 * @author why
 * @since 12/12/23
 */
class IndexGridBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<IndexAdapter.IndexItem, BaseQuickBindingHolder<FragmentIndexGridBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentIndexGridBinding>,
        position: Int,
        item: IndexAdapter.IndexItem?
    ) {
        val entity = item?.entity as? IndexEntity.Grid ?: return
        holder.binding.ivCover.loadImageAnimate(entity.images.firstOrNull())
        holder.binding.tvTitle.text = entity.title
        holder.binding.tvTag.text = entity.desc

        when (position) {
            0 -> holder.binding.root.updatePadding(left = 16.dpi, right = 8.dpi)
            1 -> holder.binding.root.updatePadding(left = 8.dpi, right = 8.dpi)
            2 -> holder.binding.root.updatePadding(left = 8.dpi, right = 16.dpi)
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentIndexGridBinding> {
        return BaseQuickBindingHolder(
            FragmentIndexGridBinding.inflate(context.inflater, parent, false)
        )
    }
}