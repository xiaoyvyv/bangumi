package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewSaveBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.annotation.MediaType
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.kts.tint
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [OverviewSaveBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewSaveBinder(private var onSaveBtnClickListener: (AdapterTypeItem, Int) -> Unit) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewSaveBinding>> {

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewSaveBinding>,
        position: Int,
        item: AdapterTypeItem?,
    ) {
        item ?: return
        item.entity.forceCast<MediaDetailEntity>().apply {
            val action = MediaType.action(mediaType)
            holder.binding.sectionSave.title = "收藏"
            holder.binding.sectionSave.more =
                if (collectState.interest != InterestType.TYPE_UNKNOWN) "删除 >>" else null

            holder.binding.tvSaveSummary.text = StringUtils.getString(
                CommonString.media_save_summary,
                countWish, action,
                countCollect, action,
                countDoing, action,
                countOnHold,
                countDropped
            )

            holder.binding.tvSave.text = StringUtils.getString(
                CommonString.media_save_tip,
                InterestType.string(collectState.interest, mediaType),
                GlobalConfig.mediaTypeName(mediaType)
            )
            holder.binding.tvSave.setOnFastLimitClickListener {
                onSaveBtnClickListener(item, position)
            }

            collectState.apply {
                when (interest) {
                    InterestType.TYPE_WISH -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_wish).tint
                        holder.binding.tvSave.setTextColor(ColorUtils.getColor(CommonColor.save_wish_text))
                    }

                    InterestType.TYPE_COLLECT -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_collect).tint
                        holder.binding.tvSave.setTextColor(ColorUtils.getColor(CommonColor.save_collect_text))
                    }

                    InterestType.TYPE_DO -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_do).tint
                        holder.binding.tvSave.setTextColor(ColorUtils.getColor(CommonColor.save_do_text))
                    }

                    InterestType.TYPE_ON_HOLD -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_on_hold).tint
                        holder.binding.tvSave.setTextColor(ColorUtils.getColor(CommonColor.save_on_hold_text))
                    }

                    InterestType.TYPE_DROPPED -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_dropped).tint
                        holder.binding.tvSave.setTextColor(ColorUtils.getColor(CommonColor.save_dropped_text))
                    }

                    else -> {
                        holder.binding.tvSave.backgroundTintList =
                            holder.binding.root.context.getAttrColor(GoogleAttr.colorPrimary).tint
                        holder.binding.tvSave.setTextColor(
                            holder.binding.root.context.getAttrColor(GoogleAttr.colorOnPrimary)
                        )
                        holder.binding.tvSave.text = StringUtils.getString(
                            CommonString.media_save_click,
                            GlobalConfig.mediaTypeName(mediaType)
                        )
                    }
                }
            }
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ) = BaseQuickBindingHolder(
        FragmentOverviewSaveBinding.inflate(context.inflater, parent, false)
    )
}