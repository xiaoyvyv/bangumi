package com.xiaoyv.bangumi.ui.discover.dollars

import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.xiaoyv.bangumi.databinding.ActivityDollarsItemBinding
import com.xiaoyv.common.api.parser.entity.DollarsEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.tint
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [DollarsAdapter]
 *
 * @author why
 * @since 1/4/24
 */
class DollarsAdapter : BaseQuickDiffBindingAdapter<DollarsEntity,
        ActivityDollarsItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<ActivityDollarsItemBinding>.converted(item: DollarsEntity) {
        binding.ivAvatarTheme.loadImageAnimate(item.showAvatar)
        binding.ivAvatarTheme.isVisible = !item.isMine

        binding.ivAvatarMine.loadImageAnimate(item.showAvatar)
        binding.ivAvatarMine.isVisible = item.isMine
        binding.tvContent.text = item.msg

        runCatching {
            if (item.isMine) {
                binding.tvContent.backgroundTintList = context.getColor(CommonColor.seed).tint
            } else {
                binding.tvContent.backgroundTintList = Color.parseColor(item.color).tint
            }
        }

        binding.tvContent.updateLayoutParams<ConstraintLayout.LayoutParams> {
            horizontalBias = if (item.isMine) 1f else 0f
        }

        binding.pbProgress.isVisible = item.isSending
        binding.pbProgress.updateLayoutParams<ConstraintLayout.LayoutParams> {
            if (item.isMine) {
                startToStart = binding.tvContent.id
                endToEnd = ConstraintLayout.NO_ID
            } else {
                startToStart = ConstraintLayout.NO_ID
                endToEnd = binding.tvContent.id
            }
        }
    }
}