package com.xiaoyv.bangumi.ui.media.detail.preview

import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.ConvertUtils
import com.xiaoyv.bangumi.databinding.ActivityMediaPreviewItemBinding
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * MediaPreviewAdapter
 *
 * @author why
 * @since 11/19/23
 */
class MediaPreviewAdapter : BaseQuickDiffBindingAdapter<GalleryEntity,
        ActivityMediaPreviewItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityMediaPreviewItemBinding>.converted(item: GalleryEntity) {
        binding.ivImage.loadImageAnimate(item.imageUrl)
        if (item.size > 0) {
            binding.tvTip.text = String.format(
                "%dx%d %.2f MB", item.width, item.height,
                ConvertUtils.byte2MemorySize(item.size, MemoryConstants.MB)
            )
        } else {
            binding.tvTip.text = String.format("%dx%d", item.width, item.height)
        }
    }
}