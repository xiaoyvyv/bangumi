package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.multiplatform.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.Axis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.multiplatform.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.multiplatform.cartesian.data.columnSeries
import com.patrykandpatrick.vico.multiplatform.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.multiplatform.cartesian.rememberCartesianChart
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPadding
import com.xiaoyv.bangumi.shared.ui.composition.TabTokens
import com.xiaoyv.bangumi.shared.ui.theme.colorChartStatisticsAverage
import com.xiaoyv.bangumi.shared.ui.theme.colorChartStatisticsCollect
import com.xiaoyv.bangumi.shared.ui.theme.colorChartStatisticsComments
import com.xiaoyv.bangumi.shared.ui.theme.colorChartStatisticsFinish
import com.xiaoyv.bangumi.shared.ui.theme.colorChartStatisticsFinishRate
import com.xiaoyv.bangumi.shared.ui.theme.colorChartStatisticsStander
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun UserStateScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(LayoutPadding),
        verticalArrangement = Arrangement.spacedBy(LayoutPadding)
    ) {
        var current by remember { mutableStateOf(SubjectType.UNKNOWN) }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            TabTokens.subjectTypeFilters.forEachIndexed { index, (t, _) ->
                SegmentedButton(
                    selected = current == t,
                    onClick = { current = t },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = TabTokens.subjectTypeFilters.size,
                        baseShape = MaterialTheme.shapes.small
                    ),
                    label = {
                        Text(
                            modifier = Modifier.basicMarquee(Int.MAX_VALUE, spacing = MarqueeSpacing(4.dp)),
                            maxLines = 1,
                            text = stringResource(SubjectType.string(t))
                        )
                    }
                )
            }
        }

        val (collection, rating) = when (current) {
            SubjectType.ANIME -> state.user.stats.subject.anime to state.user.stats.rating.anime
            SubjectType.REAL -> state.user.stats.subject.real to state.user.stats.rating.real
            SubjectType.MUSIC -> state.user.stats.subject.music to state.user.stats.rating.music
            SubjectType.GAME -> state.user.stats.subject.game to state.user.stats.rating.game
            SubjectType.BOOK -> state.user.stats.subject.book to state.user.stats.rating.book
            else -> state.user.stats.subject.all to state.user.stats.rating.all
        }

        VerticalGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = SimpleGridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(LayoutPadding),
            verticalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = "收藏",
                value = collection.total.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsCollect)
            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = "完成",
                value = collection.collect.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsFinish)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = "完成率",
                value = collection
                    .let { if (it.total > 0) it.collect / it.total.toFloat() * 100f else 0f }
                    .toFixed(2).toString() + "%",
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsFinishRate)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = "平均分",
                value = rating.averageScore.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsAverage)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = "标准差",
                value = rating.standardDeviation.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsStander)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = "评分数",
                value = rating.ratingCount.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsComments)
            )
        }

        val modelProducer = remember { CartesianChartModelProducer() }
        LaunchedEffect(rating) {
            if (rating.infos.isNotEmpty()) {
                modelProducer.runTransaction {
                    columnSeries { series(rating.infos.map { it.count }) }
                }
            }
        }

        CartesianChartHost(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = remember(rating.infos) {
                        object : CartesianValueFormatter {
                            override fun format(
                                context: CartesianMeasuringContext,
                                value: Double,
                                verticalAxisPosition: Axis.Position.Vertical?,
                            ): CharSequence {
                                return (10 - value).roundToInt().toString()
                            }
                        }
                    }
                ),
            ),
            modelProducer = modelProducer,
        )
    }
}

@Composable
private fun CardInfo(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    colors: CardColors = CardDefaults.cardColors(),
) {
    Card(modifier = modifier, colors = colors) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(LayoutPadding)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),
                maxLines = 1,
                overflow = Ellipsis
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}