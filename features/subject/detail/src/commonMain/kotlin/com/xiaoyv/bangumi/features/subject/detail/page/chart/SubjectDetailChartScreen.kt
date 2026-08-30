package com.xiaoyv.bangumi.features.subject.detail.page.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.CartesianLayerPadding
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.multiplatform.common.Fill
import com.patrykandpatrick.vico.multiplatform.common.component.rememberLineComponent
import com.patrykandpatrick.vico.multiplatform.common.data.ExtraStore
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.parseHtmlHexColor
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectStats
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectStats.GridState
import com.xiaoyv.bangumi.shared.ui.component.layout.adaptive.AdaptiveGrid
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpLazyListState
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
import com.xiaoyv.bangumi.shared.ui.theme.ContentMarginHalf
import kotlinx.collections.immutable.persistentListOf
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.math.roundToInt

private const val CONTENT_CHART_ITEM = "CONTENT_CHART_ITEM"

private val LegendLabelKey = ExtraStore.Key<Set<String>>()
private val columnColors = listOf(
    Color(0xFF67B7DC),
    Color(0xFF6794DC),
    Color(0xFF6771DC),
    Color(0xFF8067DC),
    Color(0xFFA267DC),
    Color(0xFFC767DC),
    Color(0xFFDC67CE),
    Color(0xFFDC67AB),
)

@Composable
fun SubjectDetailChartRoute(subjectId: Long) {
    val viewModel = koinSubjectDetailChartViewModel(subjectId)
    val baseState by viewModel.collectAsState()

    SubjectDetailChartScreen(
        uiState = baseState,
        onActionEvent = viewModel::onEvent
    )
}

@Composable
fun SubjectDetailChartScreen(
    uiState: UiState<SubjectDetailChartState>,
    onActionEvent: (SubjectDetailChartEvent.Action) -> Unit,
) {
    StateLayout(
        modifier = Modifier.fillMaxSize(),
        uiState = uiState,
        onRefresh = { onActionEvent(SubjectDetailChartEvent.Action.OnRefresh(it)) },
        enablePullRefresh = true
    ) {
        SubjectDetailChartContent(state = it)
    }
}


@Composable
private fun SubjectDetailChartContent(state: SubjectDetailChartState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberScrollUpLazyListState(),
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
        contentPadding = PaddingValues(vertical = ContentMargin)
    ) {
        item(key = "1", contentType = CONTENT_CHART_ITEM) {
            SubjectDetailChartContentItem(
                modifier = Modifier.fillMaxWidth(),
                stats = state.stats.interestType,
                gridState = state.stats.interestGridState
            )
        }
        item(key = "2", contentType = CONTENT_CHART_ITEM) {
            SubjectDetailChartContentItem(
                modifier = Modifier.fillMaxWidth(),
                stats = state.stats.airDate,
            )
        }
        item(key = "3", contentType = CONTENT_CHART_ITEM) {
            SubjectDetailChartContentItem(
                modifier = Modifier.fillMaxWidth(),
                stats = state.stats.totalCollects,
            )
        }
        item(key = "4", contentType = CONTENT_CHART_ITEM) {
            SubjectDetailChartContentItem(
                modifier = Modifier.fillMaxWidth(),
                stats = state.stats.regDate,
            )
        }
        item(key = "5", contentType = CONTENT_CHART_ITEM) {
            SubjectDetailChartContentItem(
                modifier = Modifier.fillMaxWidth(),
                stats = state.stats.relativeRegdate,
            )
        }
        item(key = "6", contentType = CONTENT_CHART_ITEM) {
            SubjectDetailChartContentItem(
                modifier = Modifier.fillMaxWidth(),
                stats = state.stats.vib,
                gridState = state.stats.vibGridState
            )
        }
    }
}

@Composable
private fun SubjectDetailChartContentItem(
    modifier: Modifier = Modifier,
    stats: ComposeSubjectStats.TypeData,
    gridState: SerializeList<GridState> = persistentListOf(),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ContentMargin)
    ) {
        SectionTitle(
            text = stats.title,
            modifier = Modifier.padding(horizontal = ContentMargin),
            subtitle = stats.desc,
            showMore = false
        )

        if (gridState.isNotEmpty()) {
            SubjectDetailGridInfo(
                modifier = Modifier.padding(horizontal = ContentMargin),
                gridState = gridState
            )
        }

        ComposeMultiplatformDailyDigitalMediaUse(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = ContentMargin),
            stats = stats,
        )
    }
}


@Composable
private fun SubjectDetailGridInfo(
    modifier: Modifier = Modifier,
    gridState: SerializeList<GridState>,
) {
    val useThreeColumns = isExtraSmallScreen
    val spacing = 12.dp

    AdaptiveGrid(
        minColumnWidth = 100.dp,
        horizontalSpacing = spacing,
        fixedColumnCount = if (useThreeColumns) 3 else null,
        modifier = modifier,
    ) {
        gridState.forEach {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = remember(it.color) { parseHtmlHexColor(it.color) ?: Color.LightGray }
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ContentMarginHalf),
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
                ) {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                        )
                    )
                    Text(
                        text = it.desc,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposeMultiplatformDailyDigitalMediaUse(
    modifier: Modifier = Modifier,
    stats: ComposeSubjectStats.TypeData,
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    var averageInfo by remember { mutableStateOf(0 to 0.0) }
    val showSeriesKeys = remember(stats) {
        mutableStateListOf(*stats.seriesSet.values.toTypedArray())
    }

    val showSeriesSet = remember(stats.seriesSet, showSeriesKeys.size) {
        stats.seriesSet.filter {
            showSeriesKeys.contains(it.value)
        }
    }

    LaunchedEffect(showSeriesSet) {
        averageInfo = computeAverage(stats.dataMap, showSeriesSet)

        modelProducer.runTransaction {
            columnSeries {
                if (showSeriesSet.values.isNotEmpty()) {
                    showSeriesSet.values.forEach { key ->
                        series(stats.dataMap.map { it[key] ?: 0 })
                    }
                } else {
                    series(buildList { repeat(10) { add(0) } })
                }
            }
            extras { it[LegendLabelKey] = showSeriesSet.keys }
        }
    }


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ContentMarginHalf)
    ) {
        Text(
            buildAnnotatedString {
                withSpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp
                ) {
                    append(averageInfo.second.toFixed(2).toString())
                }
                append(" ${averageInfo.first} votes")
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        CartesianChartHost(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        columnColors.map { color ->
                            rememberLineComponent(fill = Fill(color), thickness = 16.dp)
                        }
                    ),
                    columnCollectionSpacing = ContentMarginHalf,
                    mergeMode = { ColumnCartesianLayer.MergeMode.Stacked },
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
                    valueFormatter = { _, value, _ ->
                        (10 - value.roundToInt()).toString()
                    }
                ),
                layerPadding = { CartesianLayerPadding(scalableStart = 0.dp, scalableEnd = 0.dp) },
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(zoomEnabled = false),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            stats.seriesSet.onEachIndexed { index, entry ->
                Column(
                    modifier = Modifier
                        .padding(ContentMarginHalf)
                        .clickWithoutRipped {
                            if (showSeriesKeys.contains(entry.value)) {
                                showSeriesKeys.remove(entry.value)
                            } else {
                                showSeriesKeys.add(entry.value)
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(ContentMarginHalf),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(if (showSeriesKeys.contains(entry.value)) columnColors[index] else MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text = entry.key,
                        color = if (showSeriesKeys.contains(entry.value)) columnColors[index] else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

/**
 * 计算平均值
 *
 * @return 总人数/平均分
 */
private fun computeAverage(
    valueMapList: SerializeList<SerializeMap<String, Int>>,
    seriesSet: Map<String, String>,
): Pair<Int, Double> {
    val empty = (0 to 0.0)

    // 分数-人数 对应的 Map
    val scoreMap = mutableMapOf<Int, MutableList<Int>>()

    seriesSet.values.forEach {
        valueMapList.forEachIndexed { index, map ->
            val itemCountY = map[it] ?: 0
            val score = 10 - index
            scoreMap.getOrPut(score) { arrayListOf() }.add(itemCountY)
        }
    }

    val averageMap = scoreMap.entries.associate { it.key to it.value.sum() }
    val totalCount = averageMap.values.sum()
    val totalScore = averageMap.entries.sumOf { it.key * it.value }
    return if (totalCount == 0) empty else totalCount to totalScore / totalCount.toDouble()
}