package com.xiaoyv.bangumi.ui.media.detail.overview.binder

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseMultiItemAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.bangumi.databinding.FragmentOverviewEpBinding
import com.xiaoyv.common.api.parser.entity.MediaDetailEntity
import com.xiaoyv.common.api.response.api.ApiUserEpEntity
import com.xiaoyv.common.config.annotation.MediaType
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
    private val clickItemListener: BaseQuickAdapter.OnItemChildClickListener<ApiUserEpEntity>,
    private val clickAddEpProgress: (MediaDetailEntity, Boolean) -> Unit,
) : BaseMultiItemAdapter.OnMultiItemAdapterListener<AdapterTypeItem, BaseQuickBindingHolder<FragmentOverviewEpBinding>> {

    override fun onBind(
        holder: BaseQuickBindingHolder<FragmentOverviewEpBinding>,
        position: Int,
        item: AdapterTypeItem?,
    ) {
        item ?: return
        holder.binding.tvTitleEp.title = item.title

        item.entity.forceCast<MediaDetailEntity>().apply {
            val canEditEpProgress = MediaType.canEditEpProgress(mediaType)

            holder.binding.epGrid.isVisible = canEditEpProgress

            // 格子加载提示
            holder.binding.tvLoading.isVisible = epList == null
            holder.binding.tvLoading.text = when (mediaType) {
                MediaType.TYPE_BOOK, MediaType.TYPE_GAME -> "该条目暂无章节数据"
                else -> "章节数据加载中..."
            }

            // 总进度绑定
            holder.binding.pb1.bind(this, true, clickAddEpProgress)
            holder.binding.pb2.bind(this, false, clickAddEpProgress)

            // 定位按钮
            holder.binding.ivLocation.isVisible = canEditEpProgress
                    && EpGridView.isHorizontalGrid(epList.orEmpty().size)
            holder.binding.ivLocation.setOnFastLimitClickListener {
                holder.binding.epGrid.scrollToWatched()
            }

            holder.binding.epGrid.addOnItemTouchListener(touchedListener)
            holder.binding.epGrid.fillMediaChapters(epList.orEmpty(), clickItemListener)
        }
    }

    override fun onCreate(
        context: Context,
        parent: ViewGroup,
        viewType: Int,
    ) = BaseQuickBindingHolder(
        FragmentOverviewEpBinding.inflate(context.inflater, parent, false)
    )
}