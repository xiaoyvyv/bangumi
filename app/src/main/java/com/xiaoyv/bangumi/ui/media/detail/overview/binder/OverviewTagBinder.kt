package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewTagBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewTagItemBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor


/**
 * Class: [OverviewTagBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewTagBinder(private val clickItemListener: (MediaDetailEntity.MediaTag) -> Unit) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewTagBinding>> {

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewTagBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.boxTag.removeAllViews()

        val context = holder.binding.root.context
        for (tag in item.mediaDetailEntity.tags) {
            val binding = FragmentOverviewTagItemBinding.inflate(
                context.inflater,
                holder.binding.boxTag,
                true
            )
            binding.tvTitleTag.setOnFastLimitClickListener {
                clickItemListener(tag)
            }

            SpanUtils.with(binding.tvTitleTag)
                .append(tag.title)
                .setBold()
                .setForegroundColor(context.getAttrColor(GoogleAttr.colorOnSurface))
                .appendSpace(4.dpi)
                .append(tag.count.toString())
                .setForegroundColor(context.getAttrColor(GoogleAttr.colorPrimary))
                .create()
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewTagBinding.inflate(context.inflater, parent, false)
    )
}