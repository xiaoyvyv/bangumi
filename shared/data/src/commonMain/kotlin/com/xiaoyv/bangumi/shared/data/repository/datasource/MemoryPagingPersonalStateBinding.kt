package com.xiaoyv.bangumi.shared.data.repository.datasource

import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.grouped
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.toImmutableList

fun MemoryPagingController<ComposeTimeline, Long>.bindTimelinePersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.state.collect { state ->
        state.timelines.forEach { (id, timeline) -> replaceById(id, timeline) }
        state.deletedTimelineIds.forEach { removeById(it) }
    }
}

fun MemoryPagingController<ComposeSubjectDisplay, Long>.bindSubjectDisplayPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.state.collect { state ->
        state.subjects.forEach { (id, subject) ->
            updateById(id) { display -> display.copy(subject = subject) }
        }
    }
}

fun MemoryPagingController<ComposeSubject, Long>.bindCollectionSubjectPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.state.collect { state ->
        state.subjects.forEach { (id, subject) ->
            updateById(id) { current ->
                subject.copy(
                    episodes = current.episodes.map { episode ->
                        if (episode.splitter != null) episode
                        else subject.episodes.find { it.id == episode.id } ?: episode
                    }.toImmutableList().grouped()
                )
            }
        }
    }
}

fun MemoryPagingController<ComposeMonoDisplay, String>.bindMonoDisplayPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.state.collect { state ->
        state.monos.forEach { (id, mono) ->
            updateWhere(predicate = { it.mono.id == id }) { display ->
                display.copy(info = display.info.copy(mono = mono))
            }
        }
    }
}

fun MemoryPagingController<ComposeMonoInfo, Long>.bindMonoInfoPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.state.collect { state ->
        state.monos.forEach { (id, mono) ->
            updateById(id) { info -> info.copy(mono = mono) }
        }
    }
}
