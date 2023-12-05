package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.FragmentOverviewAvatarItemBinding
import com.xiaoyv.bangumi.databinding.FragmentOverviewCharacterBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.helper.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [OverviewCharacterBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewCharacterBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: (MediaDetailEntity.MediaCharacter) -> Unit
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewCharacterBinding>> {

    private val itemAdapter by lazy {
        ItemAdapter()
    }

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewCharacterBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return
        holder.binding.rvCharacter.adapter = itemAdapter
        holder.binding.rvCharacter.addOnItemTouchListener(touchedListener)
        holder.binding.rvCharacter.setInitialPrefetchItemCount(item.mediaDetailEntity.characters.size)
        itemAdapter.submitList(item.mediaDetailEntity.characters)
        itemAdapter.setOnDebouncedChildClickListener(R.id.item_character, block = clickItemListener)
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewCharacterBinding.inflate(context.inflater, parent, false)
    )

    private class ItemAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaCharacter,
            FragmentOverviewAvatarItemBinding>(DiffItemCallback) {
        override fun BaseQuickBindingHolder<FragmentOverviewAvatarItemBinding>.converted(item: MediaDetailEntity.MediaCharacter) {
            binding.ivAvatar.loadImageAnimate(item.avatar)
            binding.tvName.text = item.characterNameCn.ifBlank { item.characterName }
            binding.tvJob.text = item.jobs.joinToString(";")
            binding.tvComment.text = String.format("+%d", item.saveCount)
            binding.tvComment.isVisible = item.saveCount != 0
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