package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.blankj.utilcode.util.SpanUtils
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewCharacterBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewCharacterItemBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.spi

/**
 * Class: [OverviewCharacterBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewCharacterBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (MediaDetailEntity.MediaCharacter) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.Item, BaseQuickBindingHolder<FragmentOverviewCharacterBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter().apply {
            setOnDebouncedChildClickListener(R.id.item_character, block = clickItemListener)
        }
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewCharacterBinding>,
        position: Int,
        item: OverviewAdapter.Item?
    ) {
        item ?: return
        holder.binding.rvCharacter.adapter = itemAdapter
        holder.binding.rvCharacter.addOnItemTouchListener(touchedListener)
        holder.binding.rvCharacter.setInitialPrefetchItemCount(5)
        itemAdapter.submitList(item.entity.forceCast())
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewCharacterBinding.inflate(context.inflater, parent, false)
    )

    private class ItemAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaCharacter,
            FragmentOverviewCharacterItemBinding>(DiffItemCallback) {
        override fun BaseQuickBindingHolder<FragmentOverviewCharacterItemBinding>.converted(item: MediaDetailEntity.MediaCharacter) {
            binding.ivAvatar.loadImageAnimate(item.avatar)
            binding.tvJobs.text = item.jobs.joinToString(";")
            binding.tvTimes.text = String.format("+%d", item.saveCount)
            binding.tvTimes.isVisible = item.saveCount != 0

            SpanUtils.with(binding.tvName)
                .append(item.characterNameCn.ifBlank { item.characterName })
                .appendLine()
                .append(item.personJob + "：" + item.persons.joinToString("、") { it.personName })
                .setFontSize(12.spi)
                .create()
        }

        private object DiffItemCallback :
            DiffUtil.ItemCallback<MediaDetailEntity.MediaCharacter>() {
            override fun areItemsTheSame(
                oldItem: MediaDetailEntity.MediaCharacter,
                newItem: MediaDetailEntity.MediaCharacter
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MediaDetailEntity.MediaCharacter,
                newItem: MediaDetailEntity.MediaCharacter
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}