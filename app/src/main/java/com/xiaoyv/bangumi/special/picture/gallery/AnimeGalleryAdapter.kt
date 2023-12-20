package com.xiaoyv.bangumi.special.picture.gallery

import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.ConvertUtils
import com.xiaoyv.bangumi.databinding.ActivityAnimeGalleryItemBinding
import com.xiaoyv.common.api.response.ImageGalleryEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * AnimeGalleryAdapter
 *
 * @author why
 * @since 11/19/23
 */
class AnimeGalleryAdapter : BaseQuickDiffBindingAdapter<ImageGalleryEntity.Post,
        ActivityAnimeGalleryItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityAnimeGalleryItemBinding>.converted(item: ImageGalleryEntity.Post) {
        binding.ivImage.loadImageAnimate(item.url)
        binding.tvTip.text = String.format(
            "%dx%d %.2f MB", item.width, item.height,
            ConvertUtils.byte2MemorySize(item.size, MemoryConstants.MB)
        )
    }
}