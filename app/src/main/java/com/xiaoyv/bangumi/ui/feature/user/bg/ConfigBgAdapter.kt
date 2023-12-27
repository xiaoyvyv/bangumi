package com.xiaoyv.bangumi.ui.feature.user.bg

import com.xiaoyv.bangumi.databinding.ActivityUserBgItemBinding
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [ConfigBgAdapter]
 *
 * @author why
 * @since 12/27/23
 */
class ConfigBgAdapter :
    BaseQuickDiffBindingAdapter<GalleryEntity, ActivityUserBgItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityUserBgItemBinding>.converted(item: GalleryEntity) {
        binding.ivBanner.loadImageAnimate(item.imageUrl)
    }
}