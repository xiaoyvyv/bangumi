package com.xiaoyv.bangumi.ui.media.detail.chart

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.SpanUtils
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.CartesianChart
import com.patrykandpatrick.vico.core.chart.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.component.OverlayingComponent
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.component.shape.cornered.Corner
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.component.text.VerticalPosition
import com.patrykandpatrick.vico.core.component.text.textComponent
import com.patrykandpatrick.vico.core.extension.appendCompat
import com.patrykandpatrick.vico.core.extension.copyColor
import com.patrykandpatrick.vico.core.extension.sumOf
import com.patrykandpatrick.vico.core.extension.transformToSpannable
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.CartesianLayerModel
import com.patrykandpatrick.vico.core.model.ColumnCartesianLayerModel
import com.patrykandpatrick.vico.core.model.LineCartesianLayerModel
import com.patrykandpatrick.vico.views.chart.CartesianChartView
import com.patrykandpatrick.vico.views.dimensions.dimensionsOf
import com.xiaoyv.bangumi.databinding.FragmentMediaChartBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaStatsEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.widget.state.AnimeChartTipView
import com.xiaoyv.common.widget.text.AnimeTextView
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.spi
import com.xiaoyv.widget.kts.useNotNull
import kotlin.reflect.KMutableProperty1

/**
 * Class: [MediaChartFragment]
 *
 * @author why
 * @since 1/11/24
 */
class MediaChartFragment : BaseViewModelFragment<FragmentMediaChartBinding, MediaChartViewModel>() {
    /**
     * 左侧轴定义
     */
    private val startAxisItemPlacer = AxisItemPlacer.Vertical.default()

    /**
     * 左侧轴显示整形数字
     */
    private val startAxisItemFormatter =
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _, _ ->
            value.toInt().toString()
        }

    /**
     * 底部轴显示 10-1 评分
     */
    private val bottomAxisItemFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _, _ ->
            (10 - value.toInt()).toString()
        }

    /**
     * 图表 Chart
     */
    private val CartesianChartView.requireChart: CartesianChart
        get() = requireNotNull(chart)

    /**
     * 左侧垂直轴
     */
    private val CartesianChartView.requireStartVerticalAxis: VerticalAxis<AxisPosition.Vertical.Start>
        get() = requireChart.startAxis as VerticalAxis

    /**
     * 底部水平轴
     */
    private val CartesianChartView.requireBottomHorizontalAxis: HorizontalAxis<AxisPosition.Horizontal.Bottom>
        get() = requireChart.bottomAxis as HorizontalAxis

    /**
     * 柱状图层
     */
    private val CartesianChartView.requireColumnLayer: ColumnCartesianLayer
        get() = requireChart.layers.first() as ColumnCartesianLayer

    override fun initArgumentsData(arguments: Bundle) {
        viewModel.mediaId = arguments.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.srlRefresh.initRefresh { false }
        binding.srlRefresh.setColorSchemeColors(hostActivity.getAttrColor(GoogleAttr.colorPrimary))

        binding.chartInterest.configCommonChart()
        binding.chartCount.configCommonChart()
        binding.chartReg.configCommonChart()
        binding.chartVib.configCommonChart()

        binding.sectionInterest.more = null
        binding.sectionCount.more = null
        binding.sectionReg.more = null
        binding.sectionVib.more = null
    }

    override fun initData() {
        viewModel.queryStats()
    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryStats()
        }

        // 全站用户评价 / 按收藏类型
        binding.ctInterest.onTipChangeListener = {
            binding.chartInterest.requireColumnLayer.columns = viewModel.createLineStyle(it)
            fillAverage(binding.tvCountInterest, MediaStatsEntity::interestType, it)
            useNotNull(viewModel.fetchChartDataLayer(MediaStatsEntity::interestType, it)) {
                binding.chartInterest.modelProducer?.tryRunTransaction { add(this@useNotNull) }
            }
        }

        // 用户收藏量分布 / 按用户「看过」总量
        binding.ctCount.onTipChangeListener = {
            binding.chartCount.requireColumnLayer.columns = viewModel.createLineStyle(it)
            fillAverage(binding.tvCountCollect, MediaStatsEntity::totalCollects, it)
            useNotNull(viewModel.fetchChartDataLayer(MediaStatsEntity::totalCollects, it)) {
                binding.chartCount.modelProducer?.tryRunTransaction { add(this@useNotNull) }
            }
        }

        // 用户注册时间
        binding.ctReg.onTipChangeListener = {
            binding.chartReg.requireColumnLayer.columns = viewModel.createLineStyle(it)
            fillAverage(binding.tvCountReg, MediaStatsEntity::regDate, it)
            useNotNull(viewModel.fetchChartDataLayer(MediaStatsEntity::regDate, it)) {
                binding.chartReg.modelProducer?.tryRunTransaction { add(this@useNotNull) }
            }
        }

        // VIB 评价 / Beta Release
        binding.ctVib.onTipChangeListener = {
            binding.chartVib.requireColumnLayer.columns = viewModel.createLineStyle(it)
            fillAverage(binding.tvCountVib, MediaStatsEntity::vib, it)
            useNotNull(viewModel.fetchChartDataLayer(MediaStatsEntity::vib, it)) {
                binding.chartVib.modelProducer?.tryRunTransaction { add(this@useNotNull) }
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingBias = 0.3f,
            loadingViewState = viewModel.loadingViewState,
            canShowLoading = { binding.srlRefresh.isRefreshing.not() }
        )

        viewModel.mediaStatsEntity.observe(this) {
            fillChartData(it ?: return@observe)

            binding.gsInterest.data = it.interestGridState.orEmpty()
            binding.gsVib.data = it.vibGridState.orEmpty()
        }
    }

    /**
     * 填充平均分
     */
    private fun fillAverage(
        view: AnimeTextView,
        which: KMutableProperty1<MediaStatsEntity, MediaStatsEntity.TypeData?>,
        seriesSet: List<AnimeChartTipView.ChartTipItem>,
    ) {
        val (totalCount, average) = viewModel.computeAverage(which, seriesSet)

        SpanUtils.with(view)
            .append(String.format("%.2f", average))
            .setFontSize(18.spi)
            .setForegroundColor(hostActivity.getAttrColor(GoogleAttr.colorPrimary))
            .setTypeface(Typeface.DEFAULT_BOLD)
            .appendSpace(4.dpi)
            .append(String.format("%d votes", totalCount))
            .setFontSize(12.spi)
            .setForegroundColor(hostActivity.getAttrColor(GoogleAttr.colorOnSurfaceVariant))
            .setTypeface(Typeface.DEFAULT)
            .create()
    }

    /**
     * 填充数据
     */
    private fun fillChartData(entity: MediaStatsEntity) {
        val interestType = entity.interestType
        val totalCollects = entity.totalCollects
        val regDate = entity.regDate
        val vib = entity.vib

        if (interestType != null) {
            binding.ctInterest.fillData(interestType.seriesSet, viewModel.styleColors)

            binding.sectionInterest.title = interestType.title.orEmpty()
            binding.sectionInterest.more = interestType.desc.orEmpty().ifBlank { "按收藏类型" }
            binding.tvCountInterest.text = "2141"
        }

        if (totalCollects != null) {
            binding.ctCount.fillData(totalCollects.seriesSet, viewModel.styleColors)

            binding.sectionCount.title = totalCollects.title.orEmpty()
            binding.sectionCount.more = totalCollects.desc.orEmpty().ifBlank { "按用户「看过」总量" }
            binding.tvCountCollect.text = "2141"
        }

        if (regDate != null) {
            binding.ctReg.fillData(regDate.seriesSet, viewModel.styleColors)

            binding.sectionReg.title = regDate.title.orEmpty()
            binding.sectionReg.more = regDate.desc
            binding.tvCountReg.text = "2141"
        }

        if (vib != null) {
            binding.ctVib.fillData(vib.seriesSet, viewModel.styleColors)

            binding.sectionVib.title = vib.title.orEmpty()
            binding.sectionVib.more = vib.desc.orEmpty().ifBlank { "Very Important Bgmer" }
            binding.tvCountVib.text = "2141"
        }
    }

    /**
     * 图表公共配置
     */
    private fun CartesianChartView.configCommonChart() {
        requireStartVerticalAxis.itemPlacer = startAxisItemPlacer
        requireStartVerticalAxis.valueFormatter = startAxisItemFormatter
        requireBottomHorizontalAxis.valueFormatter = bottomAxisItemFormatter

        runInitialAnimation = true
        modelProducer = CartesianChartModelProducer.build {}

        val indicatorOuterComponent = ShapeComponent(Shapes.pillShape, Color.WHITE)
        val indicatorCenterComponent = ShapeComponent(Shapes.pillShape, Color.WHITE)
        val indicatorInnerComponent = ShapeComponent(
            shape = Shapes.pillShape,
            color = hostActivity.getAttrColor(GoogleAttr.colorOnPrimarySurface),
        )
        val indicator = OverlayingComponent(
            outer = indicatorOuterComponent,
            inner = OverlayingComponent(
                outer = indicatorCenterComponent,
                inner = indicatorInnerComponent,
                innerPaddingAllDp = 5f
            ),
            innerPaddingAllDp = 10f
        )

        val labelBackgroundShape = MarkerCorneredShape(Corner.FullyRounded)
        val labelBackgroundColor = hostActivity.getAttrColor(GoogleAttr.colorSurface)
        val labelBackground = ShapeComponent(labelBackgroundShape, labelBackgroundColor)
            .setShadow(
                radius = 2f,
                dy = 1f,
                applyElevationOverlay = true,
                color = hostActivity.getAttrColor(GoogleAttr.colorSurfaceVariant)
            )

        val label = textComponent {
            background = labelBackground
            lineCount = 1
            padding = dimensionsOf(12f, 12f)
            typeface = Typeface.MONOSPACE
            color = hostActivity.getAttrColor(GoogleAttr.colorOnSurface)
        }

        marker = object : MarkerComponent(
            label = label,
            guideline = LineComponent(
                color = hostActivity.getAttrColor(GoogleAttr.colorOnSurfaceVariant),
                thicknessDp = 2f,
                shape = DashedShape(Shapes.pillShape, 8f, 4f),
            ),
            indicator = indicator
        ) {
            init {
                indicatorSizeDp = 36f
                onApplyEntryColor = { entryColor ->
                    indicatorOuterComponent.color = entryColor.copyColor(0.2f)
                    with(indicatorCenterComponent) {
                        color = entryColor
                        setShadow(radius = 12f, color = entryColor)
                    }
                }
                labelFormatter = object : MarkerLabelFormatter {
                    override fun getLabel(
                        markedEntries: List<Marker.EntryModel>,
                        chartValues: ChartValues,
                    ): CharSequence =
                        markedEntries.transformToSpannable(
                            prefix = if (markedEntries.size > 1) "%.0f".format(markedEntries.sumOf { it.entry.y }) + " (" else "",
                            postfix = if (markedEntries.size > 1) ")" else "",
                            separator = "; ",
                        ) { model ->
                            appendCompat(
                                "%.0f".format(model.entry.y),
                                ForegroundColorSpan(model.color),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
                            )
                        }

                    private val CartesianLayerModel.Entry.y
                        get() = when (this) {
                            is ColumnCartesianLayerModel.Entry -> y
                            is LineCartesianLayerModel.Entry -> y
                            else -> throw IllegalArgumentException("Unexpected `CartesianLayerModel.Entry` implementation.")
                        }
                }
            }
        }
    }

    companion object {
        fun newInstance(mediaId: String): MediaChartFragment {
            return MediaChartFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}

