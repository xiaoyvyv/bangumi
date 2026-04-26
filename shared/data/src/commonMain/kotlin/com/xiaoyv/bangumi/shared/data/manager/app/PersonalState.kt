package com.xiaoyv.bangumi.shared.data.manager.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.request.CollectionSubjectUpdate
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

val LocalPersonalState = staticCompositionLocalOf { PersonalState() }

@ReadOnlyComposable
@Composable
fun currentPersonalState() = LocalPersonalState.current

@Stable
data class PersonalState(
    val subjects: SerializeMap<Long, ComposeSubject> = persistentMapOf(),
    val monos: SerializeMap<Long, ComposeMono> = persistentMapOf(),
)

@Stable
class PersonalStateStore {
    private val _state = MutableStateFlow(PersonalState())
    val state: StateFlow<PersonalState> = _state.asStateFlow()

    fun updateSubject(id: Long, data: ComposeSubject) {
        _state.update { it.copy(subjects = it.subjects.plus(id to data).toPersistentMap()) }
    }

    fun updateMono(id: Long, data: ComposeMono) {
        _state.update { it.copy(monos = it.monos.plus(id to data).toPersistentMap()) }
    }

    /**
     * 修改了章节收藏状态，刷新条目
     */
    fun updateCollectionEpisode(subject: ComposeSubject, episodeIds: List<Long>, @CollectionEpisodeType type: Int) {
        val episodes = subject.episodes.map {
            if (!episodeIds.contains(it.id)) it else it.copy(
                collection = it.collection.copy(
                    status = type,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        updateSubject(
            id = subject.id,
            data = subject.copy(
                episodes = episodes.toPersistentList(),
                interest = subject.interest.copy(
                    epStatus = episodes.count { it.collection.status != CollectionEpisodeType.UNKNOWN }
                )
            )
        )
    }

    /**
     * 更新条目收藏数据
     */
    fun updateCollectionSubject(subject: ComposeSubject, update: CollectionSubjectUpdate) {
        updateSubject(
            id = subject.id,
            data = subject.copy(
                interest = subject.interest.copy(
                    type = update.type ?: subject.interest.type,
                    rate = update.rate ?: subject.interest.rate,
                    epStatus = update.epStatus ?: subject.interest.epStatus,
                    volStatus = update.volStatus ?: subject.interest.volStatus,
                    comment = update.comment ?: subject.interest.comment,
                    private = update.`private` ?: subject.interest.private,
                    tags = update.tags ?: subject.interest.tags
                )
            )
        )
    }
}