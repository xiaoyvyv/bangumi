package com.xiaoyv.bangumi.features.subject.detail.business

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_blog
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_character
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_chart
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_episode
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_index
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_overview
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_person
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_preview
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_related
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_topic
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_tracks
import com.xiaoyv.bangumi.shared.core.types.LoadingState
import com.xiaoyv.bangumi.shared.core.types.SubjectDetailTab
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.core.utils.substringBeforeAnyPunctuation
import com.xiaoyv.bangumi.shared.core.utils.toTrimString
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeParade
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.data.model.response.bgm.chineseNames
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanPhoto
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * [SubjectDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class SubjectDetailState(
    @SerialName("id") val id: Long,
    @SerialName("subject") val subject: ComposeSubject = ComposeSubject.Empty,
    @SerialName("photo") val photo: ComposeDoubanPhoto = ComposeDoubanPhoto.Empty,
    @SerialName("parade") val parade: ComposeParade = ComposeParade.Empty,
    @SerialName("characters") val characters: SerializeList<ComposeMonoDisplay> = persistentListOf(),
    @SerialName("persons") val persons: SerializeList<ComposeMonoDisplay> = persistentListOf(),
    @SerialName("related") val related: SerializeList<ComposeSubjectDisplay> = persistentListOf(),
    @SerialName("myTags") val myTags: SerializeList<ComposeTag> = persistentListOf(),

    @Transient
    val loading: LoadingState = LoadingState.NotLoading,
) {

    fun magnetQuery(episode: ComposeEpisode = ComposeEpisode.Empty): String {
        val ep = if (episode == ComposeEpisode.Empty) "" else listOf(episode.sortOrder, episode.episodeNumber)
            .asSequence()
            .filter { it != 0.0 }
            .map { it.toTrimString().padStart(2, '0') }
            .distinct()
            .joinToString("|")

        return subject.infobox
            .chineseNames(subject.nameCn)
            .let { it + listOf(subject.name) }
            .asSequence()
            .map { it.substringBefore(" ").substringBeforeAnyPunctuation() }
            .distinct()
            .joinToString("|")
            .let { "$it CHT|CHS|GB|简|繁|中 $ep".trim() }
    }

    @Composable
    fun rememberTabs() = remember(photo, subject.type) {
        val items = mutableListOf<ComposeTextTab<Int>>()
        items.add(ComposeTextTab(SubjectDetailTab.OVERVIEW, Res.string.subject_tab_overview))
        // 动画和三次元和音乐 才显示章节 TAB
        if (subject.type == SubjectType.MUSIC || subject.type == SubjectType.REAL || subject.type == SubjectType.ANIME) {
            items.add(
                ComposeTextTab(
                    SubjectDetailTab.EPISODE,
                    if (subject.type == SubjectType.MUSIC) Res.string.subject_tab_tracks
                    else Res.string.subject_tab_episode
                )
            )
        }
        // 动画和三次元 才显示预览 TAB
        if (photo.doubanMediaId.isNotBlank() && (subject.type == SubjectType.REAL || subject.type == SubjectType.ANIME)) {
            items.add(ComposeTextTab(SubjectDetailTab.PREVIEW, Res.string.subject_tab_preview))
        }
        items.add(ComposeTextTab(SubjectDetailTab.CHARACTER, Res.string.subject_tab_character))
        items.add(ComposeTextTab(SubjectDetailTab.PERSON, Res.string.subject_tab_person))
        items.add(ComposeTextTab(SubjectDetailTab.RELATED, Res.string.subject_tab_related))
        items.add(ComposeTextTab(SubjectDetailTab.INDEX, Res.string.subject_tab_index))
        items.add(ComposeTextTab(SubjectDetailTab.BLOG, Res.string.subject_tab_blog))
        items.add(ComposeTextTab(SubjectDetailTab.TOPIC, Res.string.subject_tab_topic))
        items.add(ComposeTextTab(SubjectDetailTab.CHART, Res.string.subject_tab_chart))
        items.toImmutableList()
    }
}
