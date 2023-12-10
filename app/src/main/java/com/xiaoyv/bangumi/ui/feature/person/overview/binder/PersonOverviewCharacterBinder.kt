package com.xiaoyv.bangumi.ui.feature.person.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewListBinding
import com.xiaoyv.bangumi.databinding.FragmentPersonOverviewListCharacterBinding
import com.xiaoyv.bangumi.ui.feature.person.overview.PersonOverviewAdapter
import com.xiaoyv.common.api.parser.entity.CharacterEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonOverviewCharacterBinder]
 *
 * @author why
 * @since 12/6/23
 */
class PersonOverviewCharacterBinder(
    private val clickCharacterItem: (CharacterEntity) -> Unit,
    private val clickMediaItem: (CharacterEntity) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<PersonOverviewAdapter.Item, BaseQuickBindingHolder<FragmentPersonOverviewListBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_character, block = clickCharacterItem)
            setOnDebouncedChildClickListener(R.id.iv_avatar, block = clickCharacterItem)
            setOnDebouncedChildClickListener(R.id.iv_cover, block = clickMediaItem)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentPersonOverviewListBinding>,
        position: Int,
        item: PersonOverviewAdapter.Item?
    ) {
        item ?: return
        holder.binding.tvItemTitle.text = item.title
        holder.binding.rvItems.adapter = itemAdapter

        item.entity.forceCast<List<CharacterEntity>>().apply {
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
     * 最近出演的角色条目
     */
    private class ItemAdapter : BaseQuickDiffBindingAdapter<CharacterEntity,
            FragmentPersonOverviewListCharacterBinding>(IdDiffItemCallback()) {

        override fun BaseQuickBindingHolder<FragmentPersonOverviewListCharacterBinding>.converted(
            item: CharacterEntity
        ) {
            binding.ivAvatar.loadImageAnimate(item.avatar)
            binding.tvName.text = item.nameNative
            binding.tvNameCn.text = item.nameCn

            item.from.firstOrNull().let { relative ->
                binding.ivCover.loadImageAnimate(relative?.cover.orEmpty())
                binding.tvTitle.text = relative?.titleNative
                binding.tvTitleCn.text = relative?.titleCn
                binding.tvTag.text = relative?.characterJobs.orEmpty().joinToString("、")
            }
        }
    }
}