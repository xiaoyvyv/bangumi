package com.xiaoyv.bangumi.ui.discover.index.detail.page

import androidx.core.view.isVisible
import com.xiaoyv.bangumi.databinding.FragmentIndexAttachItemBinding
import com.xiaoyv.common.api.parser.entity.IndexAttachEntity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [IndexAttachAdapter]
 *
 * @author why
 * @since 12/17/23
 */
class IndexAttachAdapter : BaseQuickDiffBindingAdapter<IndexAttachEntity,
        FragmentIndexAttachItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentIndexAttachItemBinding>.converted(item: IndexAttachEntity) {
        binding.ivCover.loadImageAnimate(item.coverImage)
        binding.tvTitle.text = item.title
        binding.tvInfo.text = item.desc
        binding.tvComment.text = item.comment
        binding.tvComment.isVisible = item.comment.isNotBlank()
        binding.tvRank.text = String.format("No. %d", item.no)

        binding.tvTag.text = if (item.pathType == BgmPathType.TYPE_SUBJECT) {
            GlobalConfig.mediaTypeName(item.mediaType)
        } else {
            BgmPathType.string(item.pathType)
        }
    }
}