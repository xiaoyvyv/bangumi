package com.xiaoyv.bangumi.features.search.result.business

import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.data.model.request.list.index.IndexSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.TagSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.TopicSearchBody
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen

/**
 * [SearchResultEvent]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class SearchResultEvent {
    sealed class UI : SearchResultEvent() {
        data object OnNavUp : UI()
        data class OnNavScreen(val screen: Screen) : UI()
    }

    sealed class Action : SearchResultEvent() {
        data class OnRefresh(val loading: Boolean) : Action()
        data class OnUpdateLayout(val ui: PageUI) : Action()
        data class OnUpdateSearchSubjectParam(val body: SubjectSearchBody) : Action()
        data class OnUpdateSearchMonoParam(val body: MonoSearchBody, @field:MonoType val type: Int) : Action()
        data class OnUpdateSearchTopicParam(val body: TopicSearchBody) : Action()
        data class OnUpdateSearchIndexParam(val body: IndexSearchBody) : Action()
        data class OnUpdateSearchTagParam(val body: TagSearchBody) : Action()
    }
}