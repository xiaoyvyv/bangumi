@file:OptIn(ExperimentalSerializationApi::class)

package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_empty
import com.xiaoyv.bangumi.core_resource.resources.global_loading
import com.xiaoyv.bangumi.core_resource.resources.media_save_tip
import com.xiaoyv.bangumi.core_resource.resources.subject_collection_this
import com.xiaoyv.bangumi.core_resource.resources.type_score_1
import com.xiaoyv.bangumi.core_resource.resources.type_score_10
import com.xiaoyv.bangumi.core_resource.resources.type_score_2
import com.xiaoyv.bangumi.core_resource.resources.type_score_3
import com.xiaoyv.bangumi.core_resource.resources.type_score_4
import com.xiaoyv.bangumi.core_resource.resources.type_score_5
import com.xiaoyv.bangumi.core_resource.resources.type_score_6
import com.xiaoyv.bangumi.core_resource.resources.type_score_7
import com.xiaoyv.bangumi.core_resource.resources.type_score_8
import com.xiaoyv.bangumi.core_resource.resources.type_score_9
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.ScoreStarType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.generateRatingStars
import com.xiaoyv.bangumi.shared.core.utils.groupValue
import com.xiaoyv.bangumi.shared.core.utils.infoMonthDayRegex
import com.xiaoyv.bangumi.shared.core.utils.infoYearMonthRegex
import com.xiaoyv.bangumi.shared.core.utils.parseAsHtml
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.toFixed
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

@Serializable
data class Airtime(
    @SerialName("date") val date: String = "",
    @SerialName("month") val month: Int = 0,
    @SerialName("weekday") val weekday: Int = 0,
    @SerialName("year") val year: Int = 0,
) {
    companion object {
        fun parse(date: String): Airtime {
            return Airtime(date = date)
        }

        fun fromInfo(info: String): Airtime {
            val yearMonth = infoYearMonthRegex.groupValue(info, 0)
            val monthDay = infoMonthDayRegex.groupValue(info, 0)
            val airtime = Airtime(date = yearMonth.ifBlank { monthDay })
            return airtime
        }

        val Empty = Airtime()
    }
}


/**
 * [ComposeSubject]
 *
 * @author why
 * @since 2025/1/25
 */
@Immutable
@Serializable
data class ComposeSubject(
    @SerialName("airtime") val airtime: Airtime = Airtime(),
    @SerialName("collection") val collection: ComposeCollectionInfo = ComposeCollectionInfo.Empty,
    @SerialName("eps") val eps: Int = 0,
    @SerialName("volumes") val volumes: Int = 0,
    @SerialName("id") val id: Long = 0,
    @SerialName("images") val images: ComposeImages = ComposeImages.Empty,
    @SerialName("infobox") val infobox: SerializeList<ComposeInfobox> = persistentListOf(),
    @SerialName("locked") val locked: Boolean = false,
    @SerialName("meta_tags") val metaTags: SerializeList<String> = persistentListOf(),
    @SerialName("name") val name: String = "",
    @SerialName("name_cn") @JsonNames("nameCN", "name_cn") val nameCn: String = "",
    @SerialName("nsfw") val nsfw: Boolean = false,
    @SerialName("platform") val platform: ComposePlatform = ComposePlatform.Empty,
    @SerialName("rating") val rating: ComposeRating = ComposeRating.Empty,
    @SerialName("series") val series: Boolean = false,
    @SerialName("seriesEntry") val seriesEntry: Int = 0,
    @SerialName("summary") val summary: String = "",
    @SerialName("info") val info: String = "",
    @SerialName("tags") val tags: SerializeList<ComposeTag> = persistentListOf(),
    @SerialName("type") @field:SubjectType val type: Int = 0,
    @SerialName("interest") val interest: ComposeSubjectInterest = ComposeSubjectInterest.Empty,
    @SerialName("redirect") val redirect: Int = 0,

    /**
     * Index 目录内复用填充的数据
     */
    @SerialName("added_at") val addedAt: String = "",
    @SerialName("comment") val comment: String = "",

    /**
     * 条目相关条目的扩展字段
     */
    @SerialName("relation")
    val relation: String = "",
    @SerialName("webInfo")
    val webInfo: ComposeSubjectWebInfo = ComposeSubjectWebInfo.Empty,

    /**
     * 本地填充，针对条目格子列表
     */
    @SerialName("episodes") val episodes: SerializeList<ComposeEpisode> = persistentListOf(),
) {
    val displayName: String get() = nameCn.ifBlank { name }
    val displayRateTotalCount: Int get() = rating.total
    val displayRateCountMap: Map<Int, Int> get() = rating.count.mapIndexed { index, value -> (index + 1) to value }.toMap()
    val displayType: StringResource get() = SubjectType.string(type)

    val shareUrl: String get() = WebConstant.URL_BASE_WEB + "subject/" + id

    val paradeUrl get() = "https://anitabi.cn/map?bangumiId=$id"

    @Composable
    fun rememberDisplayDateAndType(): String {
        val type = stringResource(displayType)
        return remember(type, platform, airtime) {
            buildString {
                append(type)
                if (platform != ComposePlatform.Empty) {
                    append(" - ")
                    append(platform.typeCN)
                }
                if (airtime != Airtime.Empty) {
                    append(" - ")
                    append(airtime.date)
                }
            }
        }
    }


    /**
     * 平均评分的展示文案
     */
    @Composable
    fun rememberDisplayScoreText(
        startColor: Color,
        fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    ): AnnotatedString {
        val displayScoreType = when (round(rating.score).toInt()) {
            1 -> Res.string.type_score_1
            2 -> Res.string.type_score_2
            3 -> Res.string.type_score_3
            4 -> Res.string.type_score_4
            5 -> Res.string.type_score_5
            6 -> Res.string.type_score_6
            7 -> Res.string.type_score_7
            8 -> Res.string.type_score_8
            9 -> Res.string.type_score_9
            10 -> Res.string.type_score_10
            else -> Res.string.global_empty
        }
        val displayScoreTypeText = stringResource(displayScoreType)
        return remember(rating.score, startColor, fontSize, displayScoreTypeText) {
            buildAnnotatedString {
                append(rating.score.toString())
                if (displayScoreType != Res.string.global_empty) {
                    append(" ")
                    withStyle(SpanStyle(color = startColor, fontSize = fontSize)) {
                        append(displayScoreTypeText)
                    }
                }
            }
        }
    }

    /**
     * 评分标准差的展示文案
     */
    @Composable
    fun rememberDisplayStandardDeviation(): Double {
        return remember(rating) {
            val scoreMap = (1..10).associateWith { displayRateCountMap[it] ?: 0 }
            val totalPeople = scoreMap.values.sum().toDouble()
            if (totalPeople == 0.0) 0.0 else {
                val mean = scoreMap.entries
                    .sumOf { (score, people) -> score * people } / totalPeople

                val variance = scoreMap.entries
                    .sumOf { (score, people) -> people * (score - mean).pow(2) } / totalPeople

                sqrt(variance).toFixed(2)
            }
        }
    }

    /**
     * 全站收藏信息展示文案
     */
    @Composable
    fun rememberDisplayCollectionInfo(): String {
        val loading = stringResource(Res.string.global_loading)
        val wish = CollectionType.string(type, CollectionType.WISH)
        val done = CollectionType.string(type, CollectionType.DONE)
        val doing = CollectionType.string(type, CollectionType.DOING)
        val aside = CollectionType.string(type, CollectionType.ASIDE)
        val drop = CollectionType.string(type, CollectionType.DROP)
        return remember(collection) {
            if (collection == ComposeCollectionInfo.Empty) loading else {
                buildString {
                    append(collection.wish).append(wish)
                    append(" / ")
                    append(collection.collect).append(done)
                    append(" / ")
                    append(collection.doing).append(doing)
                    append(" / ")
                    append(collection.onHold).append(aside)
                    append(" / ")
                    append(collection.dropped).append(drop)
                }
            }
        }
    }

    /**
     * 收藏按钮文案
     */
    @Composable
    fun rememberDisplayMyCollectionText(): String {
        return if (interest.type == CollectionType.UNKNOWN) {
            stringResource(
                Res.string.subject_collection_this,
                stringResource(SubjectType.string(type))
            )
        } else {
            buildString {
                append(
                    stringResource(
                        Res.string.media_save_tip,
                        CollectionType.string(type, interest.type),
                        stringResource(SubjectType.string(type))
                    )
                )
                val rate = interest.rate
                if (rate != 0) {
                    append("\u3000\u3000")
                    append(ScoreStarType.string(rate))
                    append("\u3000")
                    append(remember(rate) { rate.generateRatingStars() })
                }
            }
        }
    }

    /**
     * 恢复 Html 渲染
     */
    fun restoreHtml(): ComposeSubject {
        return copy(
            webInfo = webInfo.let { webInfo ->
                webInfo.copy(shortInfoHtml = webInfo.shortInfo.parseAsHtml())
            }
        )
    }

    companion object {
        val Empty = ComposeSubject()
    }
}

