package com.xiaoyv.common.widget.state

import android.content.Context
import android.graphics.Color
import android.os.Parcelable
import android.util.AttributeSet
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.common.R
import com.xiaoyv.common.databinding.ViewChartTipBinding
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.helper.callback.IdEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.tint
import com.xiaoyv.common.widget.scroll.AnimeLinearLayoutManager
import com.xiaoyv.common.widget.scroll.AnimeRecyclerView
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.getDpx

/**
 * Class: [AnimeChartTipView]
 *
 * @author why
 * @since 1/11/24
 */
class AnimeChartTipView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : AnimeRecyclerView(context, attrs) {
    private val itemAdapter by lazy { ItemAdapter() }

    var listener: BaseQuickAdapter.OnItemChildClickListener<ChartTipItem>? = null
        set(value) {
            field = value
            if (value != null) {
            }
        }

    /**
     * 数据标记开关
     */
    var onTipChangeListener: (List<ChartTipItem>) -> Unit = {}

    init {
        itemAnimator = null
        layoutManager = AnimeLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = itemAdapter

        updatePadding(left = getDpx(16f), right = getDpx(16f))

        itemAdapter.addOnItemChildClickListener(R.id.item_chart_tip) { adapter, _, position ->
            val tipItem = adapter.getItem(position) ?: return@addOnItemChildClickListener
            tipItem.enable = tipItem.enable.not()
            adapter.notifyItemChanged(position)

            refreshTipData()
        }
    }

    /**
     * 填充数据
     */
    fun fillData(data: Map<String, String>?, colors: List<Int> = emptyList()) {
        itemAdapter.submitList(data.orEmpty().entries.mapIndexed { index, entry ->
            ChartTipItem(
                id = entry.value,
                tip = entry.key,
                color = colors.getOrElse(index) { Color.BLUE }
            )
        }) {
            refreshTipData()
        }
    }

    /**
     * 刷新回调
     */
    private fun refreshTipData() {
        onTipChangeListener.invoke(itemAdapter.items.filter { it.enable })
    }

    private class ItemAdapter :
        BaseQuickDiffBindingAdapter<ChartTipItem, ViewChartTipBinding>(IdDiffItemCallback()) {
        override fun BaseQuickBindingHolder<ViewChartTipBinding>.converted(item: ChartTipItem) {
            binding.tvTip.text = item.tip

            if (item.enable) {
                binding.tvTip.setTextColor(item.color)
                binding.vTip.backgroundTintList = item.color.tint
            } else {
                binding.tvTip.setTextColor(context.getAttrColor(GoogleAttr.colorOnSurfaceVariant))
                binding.vTip.backgroundTintList =
                    context.getAttrColor(GoogleAttr.colorOnSurfaceVariant).tint
            }
        }
    }

    @kotlinx.parcelize.Parcelize
    data class ChartTipItem(
        override var id: String = "",
        var tip: String = "",
        var color: Int,
        var enable: Boolean = true,
    ) : IdEntity, Parcelable
}