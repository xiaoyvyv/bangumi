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
                    val y = typeDataListMap.map { map ->
                        map[it.id]?.toString()?.toDoubleOrNull() ?: 0.0
                    }
                    series(y)
                }
            } else {
                val xValueList = buildList { repeat(10) { add(0) } }
                val xList = xValueList.indices.toList()
                series(xList, xValueList)
            }
        }

    }

    /**
     * 计算平均值
     */
    fun computeAverage(
        which: KMutableProperty1<MediaStatsEntity, MediaStatsEntity.TypeData?>,
        seriesSet: List<AnimeChartTipView.ChartTipItem>,
    ): Pair<Int, Double> {
        val empty = (0 to 0.0)
        val typeData = which.invoke(mediaStatsEntity.value ?: return empty) ?: return empty
        val typeDataListMap = typeData.dataMap.orEmpty()

        // 分数-人数 对应的 Map
        val scoreMap = mutableMapOf<Int, MutableList<Int>>()

        seriesSet.forEach {
            typeDataListMap.mapIndexed { index, map ->
                val itemCountY = (map[it.id]?.toString()?.toDoubleOrNull() ?: 0.0).toInt()
                val score = 10 - index
                scoreMap.getOrPut(score) { arrayListOf() }.add(itemCountY)
            }
        }

        val averageMap = scoreMap.entries.associate { it.key to it.value.sum() }
        val totalCount = averageMap.values.sum()
        val totalScore = averageMap.entries.sumOf { it.key * it.value }
        return if (totalCount == 0) empty else totalCount to totalScore / totalCount.toDouble()
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