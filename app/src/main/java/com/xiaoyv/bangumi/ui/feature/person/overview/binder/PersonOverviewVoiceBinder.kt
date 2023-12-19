package com.xiaoyv.bangumi.ui.feature.person.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewListBinding
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewListVoiceBinding
import com.xiaoyv.bangumi.ui.feature.person.overview.PersonOverviewAdapter
import com.xiaoyv.common.api.parser.entity.PersonEntity
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonOverviewVoiceBinder]
 *
 * @author why
 * @since 12/6/23
 */
class PersonOverviewVoiceBinder(
    private val clickPersonItem: (PersonEntity.RecentlyPerformer) -> Unit,
    private val clickMediaItem: (PersonEntity.RecentlyPerformer) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentPersonOverviewListBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_performer, block = clickMediaItem)
            setOnDebouncedChildClickListener(R.id.iv_avatar, block = clickPersonItem)
            setOnDebouncedChildClickListener(R.id.iv_cover, block = clickMediaItem)
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

        item.entity.forceCast<List<PersonEntity.RecentlyPerformer>>().apply {
            itemAdapter.submitList(this)
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): BaseQuickBindingHolder<FragmentPersonOverviewListBinding> {
        val binding =
            FragmentPersonOverviewListBinding.inflate(context.inflater, parent, false)
        binding.rvItems.layoutManager =
            AnimeLinearLayoutManager(context, GridLayoutManager.VERTICAL, false)
        return BaseQuickBindingHolder(binding)
    }


    /**
     * 虚拟角色扮演者条目
     */
    private class ItemAdapter : BaseQuickDiffBindingAdapter<PersonEntity.RecentlyPerformer,
            FragmentPersonOverviewListVoiceBinding>(IdDiffItemCallback()) {

        override fun BaseQuickBindingHolder<FragmentPersonOverviewListVoiceBinding>.converted(
            item: PersonEntity.RecentlyPerformer
        ) {
            binding.ivAvatar.isInvisible = item.character.id.isBlank()
            binding.ivAvatar.loadImageAnimate(item.character.avatar)
            binding.tvName.text = item.character.characterName
            binding.tvNameCn.text = item.character.personJob

            binding.ivCover.loadImageAnimate(item.media.cover)
            binding.tvTitle.text = item.media.titleNative
            binding.tvTitleCn.text = item.media.titleCn
            binding.tvTag.text = item.character.jobs.joinToString("、")
        }
    }
}