package com.xiaoyv.bangumi.features.main.tab.profile.business

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.types.CollectionWebSortType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.core.types.list.ListGroupType
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.core.types.list.ListMonoType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoCollectionBody
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectCollectionBody
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [ProfileState]
 *
 * @author why
 * @since 2025/1/12
 */
@Immutable
data class ProfileState(
    val tabs: SerializeList<ComposeTextTab<String>> = persistentListOf(),

    val topBarMenu: SerializeList<ComposeTextTab<Int>> = persistentListOf(),

    /**
     * 收藏 TAB 过滤项目
     */
    @field:CollectionType
    val selectedCollectType: Int = CollectionType.DONE,
    @field:SubjectType
    val selectedSubjectType: Int = SubjectType.ANIME,
    @field:CollectionWebSortType
    val selectedCollectSort: String = CollectionWebSortType.COLLECT_TIME,

    /**
     * 人物 TAB 过滤项目
     */
    val monoTypeFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
    /**
     * 目录TAB 过滤项目
     */
    val indexFilters: SerializeList<ComposeTextTab<Int>> = persistentListOf(),
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
    fun rememberListMonoParam(@MonoType type: Int, username: String): ListMonoParam {
        return remember(type, username) {
            ListMonoParam(
                type = ListMonoType.USER_COLLECTION,
                ui = PageUI(gridLayout = true),
                collection = MonoCollectionBody(
                    username = username,
                    monoType = type,
                )
            )
        }
    }

    @Composable
    fun rememberListBlogParam(username: String): ListBlogParam {
        return remember(username) {
            ListBlogParam(
                type = ListBlogType.USER_CREATE,
                username = username
            )
        }
    }

    @Composable
    fun rememberListIndexParam(@ListIndexType type: Int, username: String): ListIndexParam {
        return remember(username) {
            ListIndexParam(
                type = type,
                username = username
            )
        }
    }

    @Composable
    fun rememberListGroupParam(username: String): ListGroupParam {
        return remember(username) {
            ListGroupParam(
                type = ListGroupType.USER,
                username = username
            )
        }
    }
}
