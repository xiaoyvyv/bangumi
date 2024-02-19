package com.xiaoyv.bangumi.special.collection

import androidx.core.view.isVisible
import com.blankj.utilcode.util.TimeUtils
import com.xiaoyv.bangumi.databinding.ActivityCollectionItemBinding
import com.xiaoyv.common.config.annotation.LocalCollectionType
import com.xiaoyv.common.database.collection.Collection
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [CollectionAdapter]
 *
 * @author why
 * @since 1/3/24
 */
class CollectionAdapter :
    BaseQuickDiffBindingAdapter<Collection, ActivityCollectionItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityCollectionItemBinding>.converted(item: Collection) {
        binding.ivCover.isVisible = item.tImage.isNotBlank()
        binding.ivCover.loadImageAnimate(item.tImage)

        binding.tvTitle.text = item.title
        binding.tvDesc.text = item.content
        binding.tvDesc.isVisible = item.content.isNotBlank()
        binding.tvTime.text = TimeUtils.millis2String(item.time, "yyyy-MM-dd")

        binding.tvEp.text = when (item.type) {
            LocalCollectionType.TYPE_TOPIC -> "话题"
            LocalCollectionType.TYPE_BLOG -> "日志"
            else -> "未知"
        }
    }
}