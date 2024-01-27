package com.xiaoyv.bangumi.special.syncer.list

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.ActivitySyncerItemBinding
import com.xiaoyv.common.api.response.anime.AnimeSyncEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [SyncerListAdapter]
 *
 * @author why
 * @since 1/26/24
 */
class SyncerListAdapter : BaseQuickDiffBindingAdapter<AnimeSyncEntity,
        ActivitySyncerItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivitySyncerItemBinding>.converted(item: AnimeSyncEntity) {
        binding.ivCover.loadImageAnimate(item.image)
        binding.tvName.text = item.name
        binding.tvStatus.text = item.interestText
        binding.tvDesc.text = item.summary

        binding.tvComment.text = item.comment
        binding.tvComment.isVisible = item.comment.isNotBlank()

        binding.ivStar.rating = (item.score / 2).toFloat()
        binding.ivStar.isVisible = item.score != 0.0

        binding.llComment.isGone = item.score == 0.0 && item.comment.isBlank()
    }
}