package com.xiaoyv.bangumi.ui.media.detail.chart

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ColorUtils
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MediaStatsEntity
import com.xiaoyv.common.api.parser.impl.parserMediaStats
import com.xiaoyv.common.kts.CommonColor
import com.xiaoyv.common.widget.state.AnimeChartTipView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.KMutableProperty1

/**
 * Class: [MediaChartViewModel]
 *
 * @author why
 * @since 1/11/24
 */
class MediaChartViewModel : BaseViewModel() {
    internal val mediaStatsEntity = MutableLiveData<MediaStatsEntity?>()

    internal var mediaId: String = ""

    internal val styleColors by lazy {
        listOf(
            ColorUtils.getColor(CommonColor.chart_c1),
            ColorUtils.getColor(CommonColor.chart_c2),
            ColorUtils.getColor(CommonColor.chart_c3),
            ColorUtils.getColor(CommonColor.chart_c4),
            ColorUtils.getColor(CommonColor.chart_c5),
            ColorUtils.getColor(CommonColor.chart_c6),
            ColorUtils.getColor(CommonColor.chart_c7)
        )
    }

    internal val columnStyles by lazy {
        listOf(
            lineComponent(color = ColorUtils.getColor(CommonColor.chart_c1)),
            lineComponent(color = ColorUtils.getColor(CommonColor.chart_c2)),
            lineComponent(color = ColorUtils.getColor(CommonColor.chart_c3)),
            lineComponent(color = ColorUtils.getColor(CommonColor.chart_c4)),
            lineComponent(color = ColorUtils.getColor(CommonColor.chart_c5)),
            lineComponent(color = ColorUtils.getColor(CommonColor.chart_c6)),
            lineComponent(color = ColorUtils.getColor(CommonColor.chart_c7)),
        )
    }

    private inline fun lineComponent(
        color: Int,
        block: LineComponent.() -> Unit = {},
    ) = LineComponent(color, 12f).apply(block)

    fun queryStats() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                require(mediaId.isNotBlank()) { "媒体ID不存在" }

                mediaStatsEntity.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryMediaStats(mediaId)
                }.parserMediaStats()
            }
        )
    }

    /**
     * 更新图表数据
     */
    fun fetchChartDataLayer(
        which: KMutableProperty1<MediaStatsEntity, MediaStatsEntity.TypeData?>,
        seriesSet: List<AnimeChartTipView.ChartTipItem>,
    ): ColumnCartesianLayerModel.Partial? {
        val typeData = which.invoke(mediaStatsEntity.value ?: return null) ?: return null
        val typeDataListMap = typeData.dataMap.orEmpty()

        return ColumnCartesianLayerModel.partial {
            if (seriesSet.isNotEmpty()) {
                seriesSet.forEach {
                    val xValueList = typeDataListMap.map { map ->
                        map[it.id]?.toString()?.toDoubleOrNull() ?: 0.0
                    }
                    val xList = xValueList.indices.toList()

                    series(xList, xValueList)
                }
            } else {
                val xValueList = buildList { repeat(10) { add(0) } }
                val xList = xValueList.indices.toList()
                series(xList, xValueList)
            }
        }
    }

    /**
     * 柱状图样式
     */
    fun createLineStyle(seriesSet: List<AnimeChartTipView.ChartTipItem>): List<LineComponent> {
        return if (seriesSet.isEmpty()) listOf(lineComponent(color = Color.GRAY)) else seriesSet.map {
            lineComponent(color = it.color)
        }
    }
}