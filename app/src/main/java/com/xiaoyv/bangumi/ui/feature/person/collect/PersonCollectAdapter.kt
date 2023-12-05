package com.xiaoyv.bangumi.ui.feature.person.collect

import androidx.recyclerview.widget.DiffUtil
import com.xiaoyv.bangumi.databinding.FragmentPersonCollectItemBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PersonCollectAdapter]
 *
 * @author why
 * @since 12/5/23
 */
class PersonCollectAdapter : BaseQuickDiffBindingAdapter<MediaDetailEntity.MediaWho,
        FragmentPersonCollectItemBinding>(ItemDiffItemCallback) {

    override fun BaseQuickBindingHolder<FragmentPersonCollectItemBinding>.converted(item: MediaDetailEntity.MediaWho) {
        binding.ivAvatar.loadImageAnimate(item.userAvatar)
        binding.tvTip.text = item.userName
    }

    private object ItemDiffItemCallback : DiffUtil.ItemCallback<MediaDetailEntity.MediaWho>() {
        override fun areItemsTheSame(
            oldItem: MediaDetailEntity.MediaWho,
            newItem: MediaDetailEntity.MediaWho
        ): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(
            oldItem: MediaDetailEntity.MediaWho,
            newItem: MediaDetailEntity.MediaWho
        ): Boolean {
            return oldItem == newItem
        }
    }
}