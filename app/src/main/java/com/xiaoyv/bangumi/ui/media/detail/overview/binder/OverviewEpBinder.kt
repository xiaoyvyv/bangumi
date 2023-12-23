package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewEpBinding
import com.xiaoyv.common.api.parser.entity.MediaChapterEntity
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.config.annotation.InterestType
import com.xiaoyv.common.config.bean.AdapterTypeItem
import com.xiaoyv.common.helper.callback.RecyclerItemTouchedListener
import com.xiaoyv.common.kts.forceCast
import com.xiaoyv.common.kts.inflater
import com.xiaoyv.common.widget.grid.EpGridView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.callback.setOnFastLimitClickListener


/**
 * Class: [OverviewEpBinder]
 *
 * @author why
 * @since 11/30/23
 */
class OverviewEpBinder(
    private val touchedListener: RecyclerItemTouchedListener,
    private val clickItemListener: BaseQuickAdapter.OnItemChildClickListener<MediaChapterEntity>,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewEpBinding>> {
    private var autoScrollWatched = true

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewEpBinding>,
        position: Int,
        item: AdapterTypeItem?,
    ) {
        item ?: return
        holder.binding.tvTitleEp.title = item.title

        item.entity.forceCast<MediaDetailEntity>().apply {
            val canShowProgress = collectState.interest != InterestType.TYPE_UNKNOWN
                    && collectState.interest != InterestType.TYPE_WISH

            holder.binding.pbMedia.max = totalProgress
            holder.binding.pbMedia.setProgress(myProgress, true)

            holder.binding.tvEpMyProgress.text = String.format(
                "我的完成度：%d / %s",
                myProgress, if (totalProgress == 0) "*" else totalProgress.toString()
            )

            holder.binding.tvEpMyProgress.isVisible = canShowProgress
            holder.binding.ivAdd.isVisible = canShowProgress && myProgress != totalProgress
            holder.binding.ivLocation.isVisible =
                canShowProgress && EpGridView.isHorizontalGrid(epList.size)

            holder.binding.ivLocation.setOnFastLimitClickListener {
                holder.binding.epGrid.scrollToWatched()
            }

            holder.binding.epGrid.addOnItemTouchListener(touchedListener)
            holder.binding.epGrid.fillMediaChapters(epList, autoScrollWatched, clickItemListener)

            // 仅初始化刷新操作才自动滚到定位
            autoScrollWatched = false
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ): BaseQuickBindingHolder<FragmentOverviewEpBinding> = BaseQuickBindingHolder(
        FragmentOverviewEpBinding.inflate(context.inflater, parent, false)
    )
}