package com.xiaoyv.bangumi.features.search.result.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.global_character
import com.xiaoyv.bangumi.core_resource.resources.global_index
import com.xiaoyv.bangumi.core_resource.resources.global_person
import com.xiaoyv.bangumi.core_resource.resources.global_sort_bookmark
import com.xiaoyv.bangumi.core_resource.resources.global_sort_match
import com.xiaoyv.bangumi.core_resource.resources.global_sort_rank
import com.xiaoyv.bangumi.core_resource.resources.global_sort_rating
import com.xiaoyv.bangumi.core_resource.resources.global_subject
import com.xiaoyv.bangumi.core_resource.resources.global_tag
import com.xiaoyv.bangumi.core_resource.resources.global_topic
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.types.SearchType
import com.xiaoyv.bangumi.shared.core.types.SubjectSortSearchType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListIndexType
import com.xiaoyv.bangumi.shared.core.types.list.ListMonoType
import com.xiaoyv.bangumi.shared.core.types.list.ListSubjectType
import com.xiaoyv.bangumi.shared.core.types.list.ListTagType
import com.xiaoyv.bangumi.shared.core.types.list.ListTopicType
import com.xiaoyv.bangumi.shared.data.model.request.list.index.IndexSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListMonoParam
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoSearchFilter
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.TagSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.ListTopicParam
import com.xiaoyv.bangumi.shared.data.model.request.list.topic.TopicSearchBody
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [SearchResultViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class SearchResultViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.SearchResult,
) : BaseViewModel<SearchResultState, SearchResultSideEffect, SearchResultEvent.Action>(savedStateHandle) {
    override fun initSate(onCreate: Boolean) = SearchResultState(
        query = args.query,
        tabs = persistentListOf(
            ComposeTextTab(SearchType.SUBJECT, Res.string.global_subject),
            ComposeTextTab(SearchType.CHARACTER, Res.string.global_character),
            ComposeTextTab(SearchType.PERSON, Res.string.global_person),
            ComposeTextTab(SearchType.TOPIC, Res.string.global_topic),
            ComposeTextTab(SearchType.INDEX, Res.string.global_index),
            ComposeTextTab(SearchType.TAG, Res.string.global_tag),
        ),
        filterSubjectSort = persistentListOf(
            ComposeTextTab(SubjectSortSearchType.MATCH, Res.string.global_sort_match),
            ComposeTextTab(SubjectSortSearchType.RANK, Res.string.global_sort_rank),
            ComposeTextTab(SubjectSortSearchType.SCORE, Res.string.global_sort_rating),
            ComposeTextTab(SubjectSortSearchType.COLLECTS, Res.string.global_sort_bookmark),
        ),
        subjectParam = ListSubjectParam(
            type = ListSubjectType.SEARCH,
            search = SubjectSearchBody(
                keyword = args.query,
                filter = SubjectSearchBody.SubjectSearchFilter(
                    type = persistentListOf(SubjectType.ANIME),
                    nsfw = true
                )
            )
        ),
        characterParam = ListMonoParam(
            type = ListMonoType.SEARCH_CHARACTER,
            search = MonoSearchBody(
                keyword = args.query,
                filter = MonoSearchFilter(nsfw = true)
            )
        ),
        personParam = ListMonoParam(
            type = ListMonoType.SEARCH_PERSON,
            search = MonoSearchBody(
                keyword = args.query,
                filter = MonoSearchFilter(career = persistentListOf())
            )
        ),
        topicParam = ListTopicParam(
            type = ListTopicType.SEARCH,
            search = TopicSearchBody(
                keyword = args.query,
                exact = false,
                order = "time_date"
            )
        ),
        indexParam = ListIndexParam(
            type = ListIndexType.SEARCH,
            search = IndexSearchBody(
                keyword = args.query,
                exact = false,
                order = "updated_at"
            )
        ),
        tagParam = ListTagParam(
            type = ListTagType.SEARCH,
            search = TagSearchBody(
                keyword = args.query,
                subjectType = SubjectType.ANIME
            )
        )
    )

    override fun onEvent(event: SearchResultEvent.Action) {
        when (event) {
            is SearchResultEvent.Action.OnRefresh -> refresh(event.loading)
            is SearchResultEvent.Action.OnUpdateLayout -> onUpdateLayout(event.ui)
            is SearchResultEvent.Action.OnUpdateSearchSubjectParam -> onUpdateSearchSubjectParam(event.body)
            is SearchResultEvent.Action.OnUpdateSearchMonoParam -> onUpdateSearchCharacterParam(event.body, event.type)
            is SearchResultEvent.Action.OnUpdateSearchTopicParam -> onUpdateSearchTopicParam(event.body)
            is SearchResultEvent.Action.OnUpdateSearchIndexParam -> onUpdateSearchIndexParam(event.body)
            is SearchResultEvent.Action.OnUpdateSearchTagParam -> onUpdateSearchTagParam(event.body)
        }
    }

    private fun onUpdateLayout(ui: PageUI) = action {
        reduceContent {
            state.copy(
                subjectParam = state.subjectParam.copy(ui = ui),
                characterParam = state.characterParam.copy(ui = ui),
                personParam = state.personParam.copy(ui = ui),
                topicParam = state.topicParam.copy(ui = ui)
            )
        }
    }

    private fun onUpdateSearchCharacterParam(body: MonoSearchBody, @MonoType type: Int) = action {
        if (type == MonoType.CHARACTER) {
            reduceContent { state.copy(characterParam = state.characterParam.copy(search = body)) }
        } else {
            reduceContent { state.copy(personParam = state.personParam.copy(search = body)) }
        }
    }

    private fun onUpdateSearchTagParam(body: TagSearchBody) = action {
        reduceContent { state.copy(tagParam = state.tagParam.copy(search = body)) }
    }


    private fun onUpdateSearchIndexParam(body: IndexSearchBody) = action {
        reduceContent { state.copy(indexParam = state.indexParam.copy(search = body)) }
    }

    private fun onUpdateSearchTopicParam(body: TopicSearchBody) = action {
        reduceContent { state.copy(topicParam = state.topicParam.copy(search = body)) }
    }

    private fun onUpdateSearchSubjectParam(body: SubjectSearchBody) = action {
        reduceContent { state.copy(subjectParam = state.subjectParam.copy(search = body)) }
    }
}