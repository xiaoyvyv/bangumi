package com.xiaoyv.bangumi.features.mono.detail.business

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_album
import com.xiaoyv.bangumi.core_resource.resources.global_anime_pictures
import com.xiaoyv.bangumi.core_resource.resources.global_character
import com.xiaoyv.bangumi.core_resource.resources.global_collabs
import com.xiaoyv.bangumi.core_resource.resources.global_collection
import com.xiaoyv.bangumi.core_resource.resources.global_index
import com.xiaoyv.bangumi.core_resource.resources.global_pixiv
import com.xiaoyv.bangumi.core_resource.resources.global_works
import com.xiaoyv.bangumi.core_resource.resources.subject_tab_overview
import com.xiaoyv.bangumi.shared.core.types.MonoDetailTab
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectPersonWorkBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePersonPosition
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [MonoDetailState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class MonoDetailState(
    @SerialName("id") val id: Long,
    @SerialName("type") @field:MonoType val type: Int = MonoType.UNKNOWN,
    @SerialName("mono") val mono: ComposeMono = ComposeMono.Empty,
    @SerialName("casts") val casts: SerializeList<ComposeMonoInfo> = persistentListOf(),
    @SerialName("works") val works: SerializeList<ComposeSubjectDisplay> = persistentListOf(),
    @SerialName("positions") val positions: SerializeList<ComposePersonPosition> = persistentListOf(),
) {

    @Composable
    fun rememberTabs() = remember(type) {
        if (type == MonoType.PERSON) {
            persistentListOf(
                ComposeTextTab(MonoDetailTab.OVERVIEW, Res.string.subject_tab_overview),
                ComposeTextTab(MonoDetailTab.CASTS, Res.string.global_character),
                ComposeTextTab(MonoDetailTab.WORKS, Res.string.global_works),
                ComposeTextTab(MonoDetailTab.COLLABS, Res.string.global_collabs),
                ComposeTextTab(MonoDetailTab.INDEX, Res.string.global_index),
                ComposeTextTab(MonoDetailTab.COLLECTIONS, Res.string.global_collection),
            )
        } else {
            persistentListOf(
                ComposeTextTab(MonoDetailTab.OVERVIEW, Res.string.subject_tab_overview),
                ComposeTextTab(MonoDetailTab.COLLECTIONS, Res.string.global_collection),
                ComposeTextTab(MonoDetailTab.ALBUM, Res.string.global_album),
                ComposeTextTab(MonoDetailTab.PIXIV, Res.string.global_pixiv),
                ComposeTextTab(MonoDetailTab.ANIME_PICTURES, Res.string.global_anime_pictures),
                ComposeTextTab(MonoDetailTab.INDEX, Res.string.global_index),
            )
        }
    }


    @Composable
    fun rememberPersonWorkParam(@SubjectType subjectType: Int, position: Long): ListSubjectParam {
        return remember(subjectType, position) {
            ListSubjectParam(
                type = ListSubjectType.PERSON_WORK,
                personWork = SubjectPersonWorkBody(
                    personId = id,
                    subjectType = subjectType,
                    position = position,
                )
            )
        }
    }
}
