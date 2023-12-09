package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewSaveBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.config.annotation.InterestType
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
class OverviewSaveBinder(private var onSaveBtnClickListener: (OverviewAdapter.Item, Int) -> Unit) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.Item, BaseQuickBindingHolder<FragmentOverviewSaveBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewSaveBinding>,
        position: Int,
        item: OverviewAdapter.Item?
    ) {
        item ?: return
        item.entity.forceCast<MediaDetailEntity>().apply {
            holder.binding.tvSaveSummary.text = StringUtils.getString(
                CommonString.media_save_summary,
                countWish,
                countCollect,
                countDoing,
                countOnHold,
                countDropped
            )

            holder.binding.tvSave.text = StringUtils.getString(
                CommonString.media_save_tip,
                InterestType.string(collectState.interest),
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
                    }

                    InterestType.TYPE_COLLECT -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_collect).tint
                    }

                    InterestType.TYPE_DO -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_do).tint
                    }

                    InterestType.TYPE_ON_HOLD -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_on_hold).tint
                    }

                    InterestType.TYPE_DROPPED -> {
                        holder.binding.tvSave.backgroundTintList =
                            ColorUtils.getColor(CommonColor.save_dropped).tint
                    }

                    else -> {
                        holder.binding.tvSave.backgroundTintList =
                            holder.binding.root.context.getAttrColor(GoogleAttr.colorPrimary).tint
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
        viewType: Int
    ) = BaseQuickBindingHolder(
        FragmentOverviewSaveBinding.inflate(context.inflater, parent, false)
    )
}