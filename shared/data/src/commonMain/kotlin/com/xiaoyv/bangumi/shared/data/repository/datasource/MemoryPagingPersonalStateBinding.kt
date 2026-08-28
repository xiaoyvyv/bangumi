package com.xiaoyv.bangumi.shared.data.repository.datasource

import com.xiaoyv.bangumi.shared.data.manager.app.PersonalStateStore
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.grouped
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectRelation
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun MemoryPagingController<ComposeTimeline, Long>.bindTimelinePersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    launch {
        personalStateStore.onTimelineUpdated.collect { event ->
            replaceById(event.id, event.data)
        }
    }
    launch {
        personalStateStore.onTimelineDeleted.collect { event ->
            removeById(event.id)
        }
    }
}

fun MemoryPagingController<ComposeSubjectRelation, Long>.bindSubjectDisplayPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.onSubjectUpdated.collect { event ->
        updateById(event.id) { display -> display.copy(subject = event.data) }
    }
}

fun MemoryPagingController<ComposeSubject, Long>.bindCollectionSubjectPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.onSubjectUpdated.collect { event ->
        val subject = event.data
        updateById(event.id) { current ->
            subject.copy(
                episodes = current.episodes.map { episode ->
                    if (episode.splitter != null) episode
                    else subject.episodes.find { it.id == episode.id } ?: episode
                }.toImmutableList().grouped()
            )
        }
    }
}

fun MemoryPagingController<ComposeMonoDisplay, String>.bindMonoDisplayPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.onMonoUpdated.collect { event ->
        updateWhere(predicate = { it.mono.id == event.id }) { display ->
            display.copy(info = display.info.copy(mono = event.data))
        }
    }
}

fun MemoryPagingController<ComposeMonoInfo, Long>.bindMonoInfoPersonalState(
    scope: CoroutineScope,
    personalStateStore: PersonalStateStore,
): Job = scope.launch {
    personalStateStore.onMonoUpdated.collect { event ->
        updateById(event.id) { info -> info.copy(mono = event.data) }
    }
}
