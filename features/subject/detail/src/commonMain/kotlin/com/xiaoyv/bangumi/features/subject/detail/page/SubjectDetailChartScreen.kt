package com.xiaoyv.bangumi.features.subject.detail.page


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
import androidx.compose.runtime.Immutable
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
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
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailEvent
import com.xiaoyv.bangumi.features.subject.detail.business.SubjectDetailState
import com.xiaoyv.bangumi.shared.core.mvi.BaseState
import com.xiaoyv.bangumi.shared.core.mvi.BaseSyntax
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.utils.clickWithoutRipped
import com.xiaoyv.bangumi.shared.core.utils.parseHtmlHexColor
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.core.utils.withSpanStyle
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectStats
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectStats.GridState
import com.xiaoyv.bangumi.shared.data.repository.SubjectRepository
import com.xiaoyv.bangumi.shared.ui.component.layout.state.StateLayout
import com.xiaoyv.bangumi.shared.ui.component.layout.state.rememberCacheWindowLazyListState
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.SectionTitle
import com.xiaoyv.bangumi.shared.ui.kts.isExtraSmallScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.math.roundToInt

private const val CONTENT_CHART_ITEM = "CONTENT_CHART_ITEM"

@Immutable
data class SubjectDetailChartState(
    @SerialName("stats") val stats: ComposeSubjectStats = ComposeSubjectStats.Empty,
)

@Composable
fun koinSubjectDetailChartViewModel(subjectId: Long): SubjectDetailChartViewModel {
    return koinViewModel(
        key = subjectId.toString(),
        parameters = { parametersOf(subjectId) }
    )
}


class SubjectDetailChartViewModel(
    savedStateHandle: SavedStateHandle,
    private val subjectRepository: SubjectRepository,
    private val subjectId: Long,
) : BaseViewModel<SubjectDetailChartState, Any, Any>(savedStateHandle) {
    override fun initBaseState(): BaseState<SubjectDetailChartState> = BaseState.Loading()

    override fun initSate(onCreate: Boolean) = SubjectDetailChartState()

    override fun onEvent(event: Any) = Unit

    override suspend fun BaseSyntax<SubjectDetailChartState, Any>.refreshSync() {
        subjectRepository.fetchSubjectStats(subjectId)
            .onFailure { reduceError { it } }
            .onSuccess {
                reduceContent(forceRefresh = true) { state.copy(stats = it) }
            }
    }
}

/**
 * [SubjectDetailTopicScreen]
 *
 * @since 2025/5/11
 */
@Composable
fun SubjectDetailChartScreen(
    state: SubjectDetailState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    if (LocalInspectionMode.current) return
    val viewModel = koinSubjectDetailChartViewModel(state.id)
    val baseState by viewModel.collectAsState()

    StateLayout(
        modifier = Modifier.fillMaxSize(),
        baseState = baseState,
        onRefresh = { viewModel.refresh(it) },
        enablePullRefresh = true
    ) {
        SubjectDetailChartContent(
            state = it,
            onUiEvent = onUiEvent,
            onActionEvent = onActionEvent
        )
    }
}


@Composable
private fun SubjectDetailChartContent(
    state: SubjectDetailChartState,
    onUiEvent: (SubjectDetailEvent.UI) -> Unit,
    onActionEvent: (SubjectDetailEvent.Action) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = rememberCacheWindowLazyListState(),
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
        contentPadding = PaddingValues(vertical = LayoutPadding)
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
        verticalArrangement = Arrangement.spacedBy(LayoutPadding)
    ) {
        SectionTitle(
            text = stats.title,
            modifier = Modifier.padding(horizontal = LayoutPadding),
            subtitle = stats.desc,
            showMore = false
        )

        if (gridState.isNotEmpty()) {
            SubjectDetailGridInfo(
                modifier = Modifier.padding(horizontal = LayoutPadding),
                gridState = gridState
            )
        }

        ComposeMultiplatformDailyDigitalMediaUse(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = LayoutPadding),
            stats = stats,
        )
    }
}

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
private fun SubjectDetailGridInfo(
    modifier: Modifier = Modifier,
    gridState: SerializeList<GridState>,
) {
    val gridCells = if (isExtraSmallScreen) SimpleGridCells.Fixed(3) else SimpleGridCells.Adaptive(100.dp)

    VerticalGrid(
        columns = gridCells,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
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
                        .fillMaxSize()
                        .padding(LayoutPaddingHalf),
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf, Alignment.CenterVertically)
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
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
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
                    columnCollectionSpacing = LayoutPaddingHalf,
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
                        .padding(LayoutPaddingHalf)
                        .clickWithoutRipped {
                            if (showSeriesKeys.contains(entry.value)) {
                                showSeriesKeys.remove(entry.value)
                            } else {
                                showSeriesKeys.add(entry.value)
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf),
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