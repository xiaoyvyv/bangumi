package com.xiaoyv.bangumi.features.main.tab.home.business

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import com.xiaoyv.bangumi.shared.core.types.SubjectWebPath
import com.xiaoyv.bangumi.shared.core.types.list.ListBlogType
import com.xiaoyv.bangumi.shared.core.types.list.ListGroupType
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.emnu.GroupFilterMode
import com.xiaoyv.bangumi.shared.data.model.request.list.blog.ListBlogParam
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupBrowserParam
import com.xiaoyv.bangumi.shared.data.model.request.list.group.ListGroupParam
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomepageCard
import com.xiaoyv.bangumi.shared.data.model.response.bgm.index.ComposeIndexFocus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [HomeState]
 *
 * @author why
 * @since 2025/1/12
 */
@Serializable
@Immutable
data class HomeState(
    /**
     * 首页
     */
    @SerialName("hotSubjects") val hotSubjects: SerializeList<ComposeSubjectDisplay> = persistentListOf(),

    /**
     * 下发横向条目数据
     */
    @SerialName("sections") val sections: List<ComposeHomepageCard> = emptyList(),

    /**
     * 追番日程
     */
    @SerialName("todayTotal") val todayTotal: Int = 0,
    @SerialName("todayCalendar") val todayCalendar: SerializeList<ComposeHomeSection> = persistentListOf(),
    @SerialName("tomorrow") val tomorrow: Int = 0,
    @SerialName("tomorrowCalendar") val tomorrowCalendar: SerializeList<ComposeHomeSection> = persistentListOf(),

    /**
     * 目录TAB首页
     */
    @SerialName("indexFocus") val indexFocus: SerializeList<ComposeIndexFocus> = persistentListOf(),
) {

    @Composable
    fun rememberListBlogParam(@SubjectWebPath type: String): ListBlogParam {
        return remember(type) {
            ListBlogParam(
                type = ListBlogType.BROWSER,
                browser = type
            )
        }
    }

    @Composable
    fun rememberListIndexParam(order: String): ListIndexParam {
        return remember(order) {
            ListIndexParam(
                type = ListIndexType.BROWSER,
                browserOrder = order
            )
        }
    }

    @Composable
    fun rememberListGroupParam(sort: String): ListGroupParam {
        return remember {
            ListGroupParam(
                type = ListGroupType.BROWSER,
                browser = ListGroupBrowserParam(
                    sort = sort,
                    mode = GroupFilterMode.All
                )
            )
        }
    }
}