package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.google.android.flexbox.FlexboxLayout
import com.xiaoyv.bangumi.databinding.FragmentOverviewTagBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.widget.text.AnimeTextView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder


/**
 * Class: [OverviewTagBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewTagBinder :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewTagBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewTagBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.boxTag.removeAllViews()
        for (tag in item.mediaDetailEntity.tags) {
            val tagView = AnimeTextView(holder.binding.root.context)
            tagView.text = tag.title
            tagView.setBackgroundResource(com.xiaoyv.widget.R.drawable.ui_shape_rectangle_corner_6)
            tagView.setPadding(16, 8, 16, 8)
            val layoutParams = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8, 8, 8, 8)
            tagView.setLayoutParams(layoutParams)
            holder.binding.boxTag.addView(tagView)
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