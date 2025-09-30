package com.xiaoyv.bangumi.features.user.business

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_collection
import com.xiaoyv.bangumi.core_resource.resources.global_friend
import com.xiaoyv.bangumi.core_resource.resources.global_timeline
import com.xiaoyv.bangumi.core_resource.resources.profile_bio
import com.xiaoyv.bangumi.core_resource.resources.profile_state
import com.xiaoyv.bangumi.core_resource.resources.profile_time_machine
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CollectionWebSortType
import com.xiaoyv.bangumi.shared.core.types.ProfileMenu
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectCollectionBody
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUser
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [UserState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
@Serializable
data class UserState(
    @SerialName("username") val username: String = "",
    @SerialName("user") val user: ComposeUser = ComposeUser.Empty,
    @SerialName("time_machine") val timeMachine: SerializeList<ComposeSection<ComposeSubject>> = persistentListOf(),

    /**
     * 收藏 TAB 过滤项目
     */
    @field:CollectionType
    @SerialName("selectedCollectType") val selectedCollectType: Int = CollectionType.DONE,
    @field:SubjectType
    @SerialName("selectedSubjectType") val selectedSubjectType: Int = SubjectType.ANIME,
    @field:CollectionWebSortType
    @SerialName("selectedCollectSort") val selectedCollectSort: String = CollectionWebSortType.COLLECT_TIME,
) {

    @Composable
    fun rememberListSubjectParam(username: String): ListSubjectParam {
        return remember(selectedCollectType, selectedSubjectType, selectedCollectSort, username) {
            ListSubjectParam(
                type = ListSubjectType.USER_COLLECTION,
                collection = SubjectCollectionBody(
                    username = username,
                    subjectType = selectedSubjectType,
                    collectionType = selectedCollectType,
                    collectionSort = selectedCollectSort
                )
            )
        }
    }

    @Composable
    fun rememberTabs() = remember {
        persistentListOf(
            ComposeTextTab(ProfileMenu.TIME_MACHINE, Res.string.profile_time_machine),
            ComposeTextTab(ProfileMenu.BIO, Res.string.profile_bio),
            ComposeTextTab(ProfileMenu.COLLECTION, Res.string.global_collection),
            ComposeTextTab(ProfileMenu.TIMELINE, Res.string.global_timeline),
            ComposeTextTab(ProfileMenu.FRIEND, Res.string.global_friend),
            ComposeTextTab(ProfileMenu.STATE, Res.string.profile_state),
        )
    }
}
