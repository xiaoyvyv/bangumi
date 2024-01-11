package com.xiaoyv.bangumi.ui.media.detail.chart

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.axis.vertical.VerticalAxis
import com.patrykandpatrick.vico.core.chart.CartesianChart
import com.patrykandpatrick.vico.core.chart.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.views.chart.CartesianChartView
import com.xiaoyv.bangumi.databinding.FragmentMediaChartBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelFragment
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.MediaStatsEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.useNotNull

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

        // 全站用户评价 / 按收藏类型
        binding.ctInterest.onTipChangeListener = {
            binding.chartInterest.requireColumnLayer.columns = viewModel.createLineStyle(it)

            useNotNull(viewModel.fetchChartDataLayer(MediaStatsEntity::interestType, it)) {
                binding.chartInterest.modelProducer?.tryRunTransaction { add(this@useNotNull) }
            }
        }

        // 用户收藏量分布 / 按用户「看过」总量
        binding.ctCount.onTipChangeListener = {
            binding.chartCount.requireColumnLayer.columns = viewModel.createLineStyle(it)

            useNotNull(viewModel.fetchChartDataLayer(MediaStatsEntity::totalCollects, it)) {
                binding.chartCount.modelProducer?.tryRunTransaction { add(this@useNotNull) }
            }
        }

        // 用户注册时间
        binding.ctReg.onTipChangeListener = {
            binding.chartReg.requireColumnLayer.columns = viewModel.createLineStyle(it)

            useNotNull(viewModel.fetchChartDataLayer(MediaStatsEntity::regDate, it)) {
                binding.chartReg.modelProducer?.tryRunTransaction { add(this@useNotNull) }
            }
        }

        // VIB 评价 / Beta Release
        binding.ctVib.onTipChangeListener = {
            binding.chartVib.requireColumnLayer.columns = viewModel.createLineStyle(it)

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
        )

        viewModel.mediaStatsEntity.observe(this) {
            fillChartData(it ?: return@observe)
        }
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

        }

        if (totalCollects != null) {
            binding.ctCount.fillData(totalCollects.seriesSet, viewModel.styleColors)

            binding.sectionCount.title = totalCollects.title.orEmpty()
            binding.sectionCount.more = totalCollects.desc.orEmpty().ifBlank { "按用户「看过」总量" }
        }

        if (regDate != null) {
            binding.ctReg.fillData(regDate.seriesSet, viewModel.styleColors)

            binding.sectionReg.title = regDate.title.orEmpty()
            binding.sectionReg.more = regDate.desc

        }

        if (vib != null) {
            binding.ctVib.fillData(vib.seriesSet, viewModel.styleColors)

            binding.sectionVib.title = vib.title.orEmpty()
            binding.sectionVib.more = vib.desc.orEmpty().ifBlank { "Very Important Bgmer" }
        }
    }

    /**
     * 图表公共配置
     */
    private fun CartesianChartView.configCommonChart() {
        runInitialAnimation = true
        modelProducer = CartesianChartModelProducer.build {}
        requireStartVerticalAxis.itemPlacer = startAxisItemPlacer
        requireStartVerticalAxis.valueFormatter = startAxisItemFormatter
        requireBottomHorizontalAxis.valueFormatter = bottomAxisItemFormatter
    }

    companion object {
        fun newInstance(mediaId: String): MediaChartFragment {
            return MediaChartFragment().apply {
                arguments = bundleOf(NavKey.KEY_STRING to mediaId)
            }
        }
    }
}

