package com.xiaoyv.bangumi.ui.media.detail.chapter

import androidx.core.view.isGone
import com.xiaoyv.bangumi.databinding.FragmentMediaChapterItemBinding
import com.xiaoyv.common.api.response.api.ApiEpisodeEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.EpApiType
import com.xiaoyv.common.config.annotation.EpCollectType
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.tint
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [MediaChapterAdapter]
 *
 * @author why
 * @since 11/24/23
 */
class MediaChapterAdapter : BaseQuickDiffBindingAdapter<ApiUserEpEntity,
        FragmentMediaChapterItemBinding>(IdDiffItemCallback()) {
    private val empty by lazy { ApiEpisodeEntity() }

    override fun BaseQuickBindingHolder<FragmentMediaChapterItemBinding>.converted(item: ApiUserEpEntity) {
        val episode = item.episode ?: empty
        binding.titleNative.text = buildString {
            append(EpApiType.toAbbrType(episode.type))
            append(episode.epText)
            append(". ")
            append(episode.name)
        }

        binding.titleCn.text = episode.nameCn
        binding.titleCn.isGone = episode.nameCn.isNullOrBlank()
        binding.tvTime.text = String.format("首播：%s", episode.airdate)
        binding.tvComment.text = String.format("讨论：%d", episode.comment)

        when {
            // 看过
            item.type == EpCollectType.TYPE_COLLECT -> {
                binding.vAired.text = episode.infoState.collectStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.save_collect).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.save_collect_text))
            }
            // 想看
            item.type == EpCollectType.TYPE_WISH -> {
                binding.vAired.text = episode.infoState.collectStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.save_wish).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.save_wish_text))
            }
            // 抛弃
            item.type == EpCollectType.TYPE_DROPPED -> {
                binding.vAired.text = episode.infoState.collectStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.save_dropped).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.save_dropped_text))
            }
            // 放送中
            episode.infoState.isAiring -> {
                binding.vAired.text = episode.infoState.airedStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.state_airing).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.state_airing_text))
            }
            // 已经播出
            episode.infoState.isAired -> {
                binding.vAired.text = episode.infoState.airedStateText
                binding.vAired.backgroundTintList = context.getColor(CommonColor.state_aired).tint
                binding.vAired.setTextColor(context.getColor(CommonColor.state_aired_text))
            }
            // 未播出
            else -> {
                binding.vAired.text = episode.infoState.airedStateText
                binding.vAired.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorSurfaceContainer).tint
                binding.vAired.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurfaceVariant))
            }
        }
    }
}