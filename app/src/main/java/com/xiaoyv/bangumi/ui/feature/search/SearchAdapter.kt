package com.xiaoyv.bangumi.ui.feature.search

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseDifferAdapter
import com.xiaoyv.bangumi.databinding.ActivitySearchItemBinding
import com.xiaoyv.common.config.bean.SearchItem
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [SearchAdapter]
 *
 * @author why
 * @since 12/10/23
 */
class SearchAdapter(private val showIcon: Boolean) : BaseDifferAdapter<SearchItem,
        BaseQuickBindingHolder<ActivitySearchItemBinding>>(IdDiffItemCallback()) {

    override fun onBindViewHolder(
        holder: BaseQuickBindingHolder<ActivitySearchItemBinding>,
        position: Int,
        item: SearchItem?
    ) {
        item ?: return
        holder.binding.tvItem.text = item.keyword.ifBlank { item.label }
        holder.binding.ivIcon.isVisible = showIcon
        holder.binding.tvItem.setTextColor(
            if (showIcon) {
                context.getAttrColor(GoogleAttr.colorOnSurface)
            } else {
                context.getAttrColor(GoogleAttr.colorPrimary)
            }
        )
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<ActivitySearchItemBinding> {
        return BaseQuickBindingHolder(
            ActivitySearchItemBinding.inflate(context.inflater, parent, false)
        )
    }
}