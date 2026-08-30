package com.xiaoyv.bangumi.features.user.page

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import com.xiaoyv.bangumi.shared.ui.component.scroll.rememberScrollUpScrollState as rememberScrollState
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
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.user_stat_average_score
import com.xiaoyv.bangumi.core_resource.resources.user_stat_collect
import com.xiaoyv.bangumi.core_resource.resources.user_stat_completion_rate
import com.xiaoyv.bangumi.core_resource.resources.user_stat_done
import com.xiaoyv.bangumi.core_resource.resources.user_stat_score_count
import com.xiaoyv.bangumi.core_resource.resources.user_stat_standard_deviation
import com.xiaoyv.bangumi.features.user.business.UserEvent
import com.xiaoyv.bangumi.features.user.business.UserState
import org.jetbrains.compose.resources.stringResource
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.ui.theme.ContentMargin
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
@OptIn(ExperimentalGridApi::class)
fun UserStateScreen(
    state: UserState,
    onUiEvent: (UserEvent.UI) -> Unit,
    onActionEvent: (UserEvent.Action) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(ContentMargin),
        verticalArrangement = Arrangement.spacedBy(ContentMargin)
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

        val gridSpacing = ContentMargin
        Grid(
            config = {
                repeat(3) { column(minmax(0.dp, 1.fr)) }
                gap(gridSpacing)
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.user_stat_collect),
                value = collection.total.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsCollect)
            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.user_stat_done),
                value = collection.collect.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsFinish)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.user_stat_completion_rate),
                value = collection
                    .let { if (it.total > 0) it.collect / it.total.toFloat() * 100f else 0f }
                    .toFixed(2).toString() + "%",
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsFinishRate)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.user_stat_average_score),
                value = rating.averageScore.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsAverage)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.user_stat_standard_deviation),
                value = rating.standardDeviation.toString(),
                colors = CardDefaults.cardColors(containerColor = colorChartStatisticsStander)

            )
            CardInfo(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.user_stat_score_count),
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
            verticalArrangement = Arrangement.spacedBy(ContentMargin)
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
