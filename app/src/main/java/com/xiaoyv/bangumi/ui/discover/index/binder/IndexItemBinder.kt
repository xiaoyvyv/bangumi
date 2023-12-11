package com.xiaoyv.bangumi.ui.feature.magi.rank.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentIndexItemBinding
import com.xiaoyv.bangumi.ui.discover.index.IndexAdapter
import com.xiaoyv.common.api.parser.entity.IndexItemEntity
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder

/**
 * Class: [IndexItemBinder]
 *
 * @author why
 * @since 12/12/23
 */
class IndexItemBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<IndexAdapter.IndexItem, BaseQuickBindingHolder<FragmentIndexItemBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentIndexItemBinding>,
        position: Int,
        item: IndexAdapter.IndexItem?
    ) {
        val entity = item?.entity as? IndexItemEntity ?: return

        holder.binding.ivAvatar.loadImageAnimate(entity.userAvatar)
        holder.binding.tvTitle.text = entity.title
        holder.binding.tvDesc.text = entity.desc
        holder.binding.tvTime.text = entity.time
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentIndexItemBinding> {
        return BaseQuickBindingHolder(
            FragmentIndexItemBinding.inflate(context.inflater, parent, false)
        )
    }

    override fun isFullSpanItem(itemType: Int): Boolean {
        return true
    }
}