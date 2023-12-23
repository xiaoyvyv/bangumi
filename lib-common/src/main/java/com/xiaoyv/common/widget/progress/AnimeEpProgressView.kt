package com.xiaoyv.common.widget.progress

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.databinding.ViewEpProgressBinding
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [AnimeEpProgressView]
 *
 * @author why
 * @since 12/23/23
 */
class AnimeEpProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {
    private val binding = ViewEpProgressBinding.inflate(context.inflater, this)

    private val MediaDetailEntity.totalProgressTip
        get() = if (progressMax == 0) "*" else progressMax.toString()

    private val MediaDetailEntity.totalProgressSecondTip
        get() = if (progressSecondMax == 0) "*" else progressSecondMax.toString()

    /**
     * 绑定
     *
     * @param epOrVol 是否为第一个进度条
     */
    fun bind(
        entity: MediaDetailEntity,
        epOrVol: Boolean,
        clickAddListener: (MediaDetailEntity, Boolean) -> Unit,
    ) {
        val progressTip = when {
            entity.mediaType == MediaType.TYPE_BOOK && epOrVol -> "Chap"
            entity.mediaType == MediaType.TYPE_BOOK -> "Vol"
            else -> "我的完成度"
        }
        if (entity.mediaType == MediaType.TYPE_BOOK && epOrVol.not()) {
            binding.pbMedia.max = entity.progressSecondMax
            binding.pbMedia.setProgress(entity.progressSecond, true)
            binding.tvProgress.text = buildString {
                append(progressTip)
                append("：")
                append(entity.progressSecond)
                append(" / ")
                append(entity.totalProgressSecondTip)
            }
            binding.ivAdd.isVisible = entity.progressSecond != entity.progressSecondMax
                    || entity.progressSecondMax == 0
        } else {
            binding.pbMedia.max = entity.progressMax
            binding.pbMedia.setProgress(entity.progress, true)
            binding.tvProgress.text = buildString {
                append(progressTip)
                append("：")
                append(entity.progress)
                append(" / ")
                append(entity.totalProgressTip)
            }
            binding.ivAdd.isVisible = entity.progress != entity.progressMax
                    || entity.progressMax == 0
        }

        binding.ivAdd.setOnFastLimitClickListener {
            clickAddListener(entity, epOrVol)
        }
    }
}