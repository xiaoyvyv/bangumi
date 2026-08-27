package com.xiaoyv.bangumi.shared.data.manager.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import com.xiaoyv.bangumi.shared.System
import com.xiaoyv.bangumi.shared.core.types.CollectionEpisodeType
import com.xiaoyv.bangumi.shared.core.types.PublishPostType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeMap
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.bgm.CollectionSubjectProgressParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update

val LocalPersonalState = staticCompositionLocalOf { PersonalState() }

@ReadOnlyComposable
@Composable
fun currentPersonalState() = LocalPersonalState.current

/**
 * [AppEvent]
 *
 * 全局应用事件
 */
sealed interface AppEvent {
    /**
     * 发布成功事件
     *
     * @param type 发布类型 [PublishPostType]
     * @param publishAttachId 目标 ID（如小组 ID、条目 ID 等）
     * @param publishSuccessId 发布成功后的内容 ID（如帖子 ID、评论 ID 等）
     */
    data class PublishSuccess(
        @PublishPostType val type: Int,
        val publishAttachId: String,
        val publishSuccessId: String
    ) : AppEvent

    /**
     * 通用刷新事件
     */
    data object Refresh : AppEvent

    /**
     * 条目数据更新事件
     */
    data class SubjectUpdated(val id: Long, val data: ComposeSubject) : AppEvent

    /**
     * 角色/人物数据更新事件
     */
    data class MonoUpdated(val id: Long, val data: ComposeMono) : AppEvent

    /**
     * 动态/时间线数据更新事件
     */
    data class TimelineUpdated(val id: Long, val data: ComposeTimeline) : AppEvent

    /**
     * 动态/时间线数据删除事件
     */
    data class TimelineDeleted(val id: Long) : AppEvent
}

/**
 * [PersonalState]
 *
 * 个人数据状态，用于应用内多页面间状态同步
 *
 * @property subjects 修改或更新过的条目数据 Map (ID -> ComposeSubject)
 * @property monos 修改或更新过的角色/人物数据 Map (ID -> ComposeMono)
 * @property timelines 修改或更新过的动态/时间线数据 Map (ID -> ComposeTimeline)
 * @property deletedTimelineIds 已删除的动态/时间线 ID 集合
 */
@Stable
data class PersonalState(
    val subjects: SerializeMap<Long, ComposeSubject> = persistentMapOf(),
    val monos: SerializeMap<Long, ComposeMono> = persistentMapOf(),
    val timelines: SerializeMap<Long, ComposeTimeline> = persistentMapOf(),
    val deletedTimelineIds: Set<Long> = persistentSetOf(),
)

/**
 * [PersonalStateStore]
 *
 * 个人数据状态存储管理器，用于全局广播与同步各种数据的修改与删除
 */
@Stable
class PersonalStateStore {
    private val _state = MutableStateFlow(PersonalState())
    private val _events = MutableSharedFlow<AppEvent>(extraBufferCapacity = 64)

    /**
     * 全局个人状态 Flow
     */
    val state: StateFlow<PersonalState> = _state.asStateFlow()

    /**
     * 全局事件 Flow
     */
    val events = _events.asSharedFlow()

    /**
     * 发布成功事件 Flow
     */
    val publishSuccess = events.filterIsInstance<AppEvent.PublishSuccess>()

    /**
     * 数据更新事件 Flow
     */
    val onSubjectUpdated = events.filterIsInstance<AppEvent.SubjectUpdated>()
    val onMonoUpdated = events.filterIsInstance<AppEvent.MonoUpdated>()
    val onTimelineUpdated = events.filterIsInstance<AppEvent.TimelineUpdated>()
    val onTimelineDeleted = events.filterIsInstance<AppEvent.TimelineDeleted>()

    /**
     * 发送事件
     */
    suspend fun emitAppEvent(event: AppEvent) {
        _events.emit(event)
    }

    /**
     * 发送发布成功事件
     */
    fun emitPublishSuccess(
        @PublishPostType type: Int,
        publishAttachId: String,
        publishSuccessId: String
    ) {
        _events.tryEmit(AppEvent.PublishSuccess(type, publishAttachId, publishSuccessId))
    }

    /**
     * 更新条目数据状态
     *
     * @param id 条目 ID
     * @param data 新的条目数据对象
     */
    fun emitSubjectUpdated(id: Long, data: ComposeSubject) {
        _state.update { it.copy(subjects = it.subjects.plus(id to data).toPersistentMap()) }
        _events.tryEmit(AppEvent.SubjectUpdated(id, data))
    }

    /**
     * 更新角色/人物数据状态
     *
     * @param id 角色/人物 ID
     * @param data 新的角色/人物数据对象
     */
    fun emitMonoUpdated(id: Long, data: ComposeMono) {
        _state.update { it.copy(monos = it.monos.plus(id to data).toPersistentMap()) }
        _events.tryEmit(AppEvent.MonoUpdated(id, data))
    }

    /**
     * 更新动态/时间线数据状态（如贴贴回应变动）
     *
     * @param id 动态 ID
     * @param data 新的动态数据对象
     */
    fun emitTimelineUpdated(id: Long, data: ComposeTimeline) {
        _state.update { it.copy(timelines = it.timelines.plus(id to data).toPersistentMap()) }
        _events.tryEmit(AppEvent.TimelineUpdated(id, data))
    }

    /**
     * 标记指定的动态/时间线数据为已删除
     *
     * @param id 动态 ID
     */
    fun emitTimelineDeleted(id: Long) {
        _state.update {
            it.copy(
                timelines = it.timelines.minus(id).toPersistentMap(),
                deletedTimelineIds = (it.deletedTimelineIds + id).toPersistentSet()
            )
        }
        _events.tryEmit(AppEvent.TimelineDeleted(id))
    }

    /**
     * 修改了章节收藏状态，刷新条目
     *
     * @param subject 条目数据对象
     * @param episodeIds 变化的章节 ID 列表
     * @param type 新的章节收藏状态 [CollectionEpisodeType]
     */
    fun emitSubjectEpisodeCollection(subject: ComposeSubject, episodeIds: List<Long>, @CollectionEpisodeType type: Int) {
        val episodes = subject.episodes.map {
            if (!episodeIds.contains(it.id)) it else it.copy(
                collection = it.collection.copy(
                    status = type,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
        emitSubjectUpdated(
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
     *
     * @param subject 条目数据对象
     * @param update 条目收藏更新请求参数 [CollectionSubjectParam]
     */
    fun emitSubjectCollection(subject: ComposeSubject, update: CollectionSubjectParam) {
        emitSubjectUpdated(
            id = subject.id,
            data = subject.copy(
                interest = subject.interest.copy(
                    type = update.type ?: subject.interest.type,
                    rate = update.rate ?: subject.interest.rate,
                    comment = update.comment ?: subject.interest.comment,
                    private = update.`private` ?: subject.interest.private,
                    tags = update.tags ?: subject.interest.tags
                )
            )
        )
    }

    /**
     * 更新条目收藏进度数据
     *
     * @param subject 条目数据对象
     * @param update 进度 [CollectionSubjectProgressParam]
     */
    fun emitSubjectCollection(subject: ComposeSubject, update: CollectionSubjectProgressParam) {
        emitSubjectUpdated(
            id = subject.id,
            data = subject.copy(
                interest = subject.interest.copy(
                    epStatus = update.epStatus ?: subject.interest.epStatus,
                    volStatus = update.volStatus ?: subject.interest.volStatus,
                )
            )
        )
    }
}
