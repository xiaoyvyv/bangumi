package com.xiaoyv.bangumi.ui.feature.person.picture

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.ConvertUtils
import com.xiaoyv.bangumi.databinding.ActivityAnimeGalleryItemBinding
import com.xiaoyv.common.api.response.GalleryEntity
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
class PersonPictureAdapter : BaseQuickDiffBindingAdapter<GalleryEntity,
        ActivityAnimeGalleryItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityAnimeGalleryItemBinding>.converted(item: GalleryEntity) {
        binding.ivImage.loadImageAnimate(item.imageUrl, cropType = ImageView.ScaleType.FIT_XY)
        binding.ivImage.updateLayoutParams<ConstraintLayout.LayoutParams> {
            dimensionRatio = when {
                item.width == 0 || item.height == 0 -> "2:3"
                else -> "${item.width}:${item.height}"
            }
        }
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