package com.xiaoyv.common.widget.grid

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.tint
import com.xiaoyv.common.widget.text.AnimeTextView
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [EpGridHelper]
 *
 * @author why
 * @since 12/22/23
 */
object EpGridHelper {
    fun converted(
        context: Context,
        item: MediaChapterEntity,
        tvEp: AnimeTextView,
        tvEpType: AnimeTextView,
    ) {
        tvEp.text = item.number
        tvEp.paintFlags = tvEp.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        tvEpType.text = item.epType
        when {
            // 分隔符
            item.splitter -> {
                tvEp.backgroundTintList = Color.TRANSPARENT.tint
                tvEp.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
                tvEpType.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
            }
            // 看过
            item.collectType == InterestType.TYPE_COLLECT -> {
                tvEp.backgroundTintList =
                    context.getColor(CommonColor.save_collect).tint
                tvEp.setTextColor(context.getColor(CommonColor.save_collect_text))
                tvEpType.setTextColor(context.getColor(CommonColor.save_collect_text))
            }
            // 想看
            item.collectType == InterestType.TYPE_WISH -> {
                tvEp.backgroundTintList = context.getColor(CommonColor.save_wish).tint
                tvEp.setTextColor(context.getColor(CommonColor.save_wish_text))
                tvEpType.setTextColor(context.getColor(CommonColor.save_wish_text))
            }
            // 抛弃
            item.collectType == InterestType.TYPE_DROPPED -> {
                tvEp.backgroundTintList =
                    context.getColor(CommonColor.save_dropped).tint
                tvEp.setTextColor(context.getColor(CommonColor.save_dropped_text))
                tvEp.paintFlags = tvEp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvEpType.setTextColor(context.getColor(CommonColor.save_dropped_text))
            }
            // 放送中
            item.isAiring -> {
                tvEp.setTextColor(context.getColor(CommonColor.state_airing_text))
                tvEp.backgroundTintList =
                    context.getColor(CommonColor.state_airing).tint
                tvEpType.setTextColor(context.getColor(CommonColor.state_airing_text))
            }
            // 已播出
            item.isAired -> {
                tvEp.backgroundTintList =
                    context.getColor(CommonColor.state_aired).tint
                tvEp.setTextColor(context.getColor(CommonColor.state_aired_text))
                tvEpType.setTextColor(context.getColor(CommonColor.state_aired_text))
            }
            // 未播出
            else -> {
                tvEp.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorSurfaceContainer).tint
                tvEp.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
                tvEpType.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurface))
            }
        }
    }
}