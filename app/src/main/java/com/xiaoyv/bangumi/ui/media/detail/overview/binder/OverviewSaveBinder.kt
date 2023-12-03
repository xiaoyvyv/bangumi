package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.StringUtils
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewSaveBinding
import com.xiaoyv.bangumi.ui.media.detail.overview.OverviewAdapter
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.GoogleAttr
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
class OverviewSaveBinder(private var onSaveBtnClickListener: (View) -> Unit) :
    BaseMultiItemAdapter.OnMultiItemAdapterListener<OverviewAdapter.OverviewItem, BaseQuickBindingHolder<FragmentOverviewSaveBinding>> {
    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewSaveBinding>,
        position: Int,
        item: OverviewAdapter.OverviewItem?
    ) {
        item ?: return

        holder.binding.tvSaveSummary.text = StringUtils.getString(
            CommonString.media_save_summary,
            item.mediaDetailEntity.countWish,
            item.mediaDetailEntity.countCollect,
            item.mediaDetailEntity.countDoing,
            item.mediaDetailEntity.countOnHold,
            item.mediaDetailEntity.countDropped
        )
        holder.binding.tvSave.text = StringUtils.getString(
            CommonString.media_save_tip,
            InterestType.string(item.mediaDetailEntity.collectState.interest)
        )
        holder.binding.tvSave.setOnFastLimitClickListener(onMultiClick = onSaveBtnClickListener)

        item.mediaDetailEntity.collectState.apply {
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
                    holder.binding.tvSave.text =
                        StringUtils.getString(CommonString.media_save_click)
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