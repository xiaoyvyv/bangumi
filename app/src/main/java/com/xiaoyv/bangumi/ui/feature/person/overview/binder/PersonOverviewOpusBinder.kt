package com.xiaoyv.bangumi.ui.feature.person.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewListBinding
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewListOpusBinding
import com.xiaoyv.bangumi.ui.feature.person.overview.PersonOverviewAdapter
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [PersonOverviewOpusBinder]
 *
 * @author why
 * @since 12/6/23
 */
class PersonOverviewOpusBinder(
    private val clickItem: (PersonEntity.RecentlyOpus) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentPersonOverviewListBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_opus, block = clickItem)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentPersonOverviewListBinding>,
        position: Int,
        item: AdapterTypeItem?
    ) {
        item ?: return
        holder.binding.tvItemTitle.text = item.title
        holder.binding.rvItems.adapter = itemAdapter

        item.entity.forceCast<List<PersonEntity.RecentlyOpus>>().apply {
            itemAdapter.submitList(this)
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentPersonOverviewListBinding> {
        val binding = FragmentPersonOverviewListBinding.inflate(context.inflater, parent, false)
        binding.rvItems.layoutManager =
            GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        binding.rvItems.setPadding(8.dpi, 8.dpi, 8.dpi, 8.dpi)
        return BaseQuickBindingHolder(binding)
    }

    /**
     * 最近参与的子条目
     */
    private class ItemAdapter : BaseQuickDiffBindingAdapter<PersonEntity.RecentlyOpus,
            FragmentPersonOverviewListOpusBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<FragmentPersonOverviewListOpusBinding>.converted(item: PersonEntity.RecentlyOpus) {
            binding.ivCover.loadImageAnimate(item.cover)
            binding.tvTag.text = String.format(
                "%s -（%s）",
                GlobalConfig.mediaTypeName(item.mediaType),
                item.jobs.joinToString("、")
            )
            binding.tvTitle.text = item.titleCn.ifBlank { item.titleNative }
        }
    }
}