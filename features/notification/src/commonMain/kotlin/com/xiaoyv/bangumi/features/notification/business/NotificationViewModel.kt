package com.xiaoyv.bangumi.features.notification.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.notification_no_data
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.UiSideEffect
import com.xiaoyv.bangumi.shared.core.mvi.UiState
import com.xiaoyv.bangumi.shared.core.mvi.postEffect
import com.xiaoyv.bangumi.shared.core.mvi.postToast
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.mvi.reduceError
import com.xiaoyv.bangumi.shared.core.mvi.withActionLoading
import com.xiaoyv.bangumi.shared.core.types.NoticeType
import com.xiaoyv.bangumi.shared.core.types.TopicType
import com.xiaoyv.bangumi.shared.core.utils.errMsg
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeNotice
import com.xiaoyv.bangumi.shared.data.repository.UserRepository
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.getString
import org.orbitmvi.orbit.syntax.Syntax

/**
 * [NotificationViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class NotificationViewModel(
    private val userRepository: UserRepository,
) : BaseViewModel<NotificationState, NotificationSideEffect, NotificationEvent.Action>() {
    override fun initBaseState(): UiState<NotificationState> = initBaseLoadingState()

    override fun createInitialState() = NotificationState(
        pageUrl = WebConstant.URL_BASE_WEB + "notify/all"
    )

    override fun onEvent(event: NotificationEvent.Action) {
        when (event) {
            is NotificationEvent.Action.OnRefresh -> refresh(event.loading)
            is NotificationEvent.Action.OnMarkRead -> onMarkRead(event.item, event.showLoading)
            is NotificationEvent.Action.OnAgreeFriendRequest -> onAgreeFriendRequest(event.item)
            is NotificationEvent.Action.OnClickItem -> onClickItem(event.item)
        }
    }

    override suspend fun Syntax<UiState<NotificationState>, UiSideEffect<NotificationSideEffect>>.refreshSync() {
        userRepository.fetchUserNotify(null)
            .mapCatching { it.ifEmpty { throw Exception(getString(Res.string.notification_no_data)) } }
            .onFailure { reduceError { it } }
            .onSuccess { reduceData { state.copy(notifications = it.toPersistentList()) } }
    }

    private fun onClickItem(item: ComposeNotice) = intent {
        val screen = when (item.type) {
            // 小组
            NoticeType.GROUP_TOPIC_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_GROUP)
            NoticeType.GROUP_TOPIC_AT -> Screen.TopicDetail(item.mainID, TopicType.TYPE_GROUP)
            NoticeType.GROUP_POST_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_GROUP)

            // 日志
            NoticeType.BLOG_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_BLOG)
            NoticeType.BLOG_POST_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_BLOG)

            // 角色
            NoticeType.CHARACTER_TOPIC_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_CRT)
            NoticeType.CHARACTER_POST_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_CRT)
            NoticeType.CHARACTER_POST_AT -> Screen.TopicDetail(item.mainID, TopicType.TYPE_CRT)

            // 人物
            NoticeType.PERSON_POST_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_PERSON)
            NoticeType.PERSON_POST_AT -> Screen.TopicDetail(item.mainID, TopicType.TYPE_PERSON)

            // 条目
            NoticeType.SUBJECT_TOPIC_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_SUBJECT)
            NoticeType.SUBJECT_TOPIC_AT -> Screen.TopicDetail(item.mainID, TopicType.TYPE_SUBJECT)
            NoticeType.SUBJECT_POST_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_SUBJECT)

            // 目录
            NoticeType.INDEX_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_INDEX)
            NoticeType.INDEX_POST_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_INDEX)

            // 章节
            NoticeType.EP_POST_REPLY -> Screen.TopicDetail(item.mainID, TopicType.TYPE_EP)
            NoticeType.EP_POST_AT -> Screen.TopicDetail(item.mainID, TopicType.TYPE_EP)

            // 时间线的吐槽
            NoticeType.TIMELINE_SAY_REPLY -> Screen.Empty
            NoticeType.TIMELINE_SAY_AT -> Screen.Empty

            // 好友
            NoticeType.REQUEST_FRIEND -> Screen.Empty
            NoticeType.ACCEPT_FRIEND -> Screen.Empty

            // 条目修订
            NoticeType.SUBJECT_PATCH_ACCEPTED -> Screen.Empty
            NoticeType.SUBJECT_PATCH_REJECTED -> Screen.Empty
            NoticeType.SUBJECT_PATCH_EXPIRED -> Screen.Empty
            NoticeType.SUBJECT_PATCH_REPLY -> Screen.Empty

            // 章节修订
            NoticeType.EPISODE_PATCH_ACCEPTED -> Screen.Empty
            NoticeType.EPISODE_PATCH_REJECTED -> Screen.Empty
            NoticeType.EPISODE_PATCH_EXPIRED -> Screen.Empty
            NoticeType.EPISODE_PATCH_REPLY -> Screen.Empty

            // 角色修订
            NoticeType.CHARACTER_PATCH_ACCEPTED -> Screen.Empty
            NoticeType.CHARACTER_PATCH_REJECTED -> Screen.Empty
            NoticeType.CHARACTER_PATCH_EXPIRED -> Screen.Empty
            NoticeType.CHARACTER_PATCH_REPLY -> Screen.Empty

            // 人物修订
            NoticeType.PERSON_PATCH_ACCEPTED -> Screen.Empty
            NoticeType.PERSON_PATCH_REJECTED -> Screen.Empty
            NoticeType.PERSON_PATCH_EXPIRED -> Screen.Empty
            NoticeType.PERSON_PATCH_REPLY -> Screen.Empty

            else -> Screen.Empty
        }

        if (screen != Screen.Empty) {
            postEffect { NotificationSideEffect.OnNavScreen(screen) }
        }
    }

    private fun onAgreeFriendRequest(item: ComposeNotice) = intent {
        withActionLoading {
            userRepository.submitMarkNotificationRead(item.id)
            userRepository.fetchUserNotify(false)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceData { state.copy(notifications = it.toPersistentList()) }
            postEffect { NotificationSideEffect.OnRefreshNotificationCount }
        }
    }

    private fun onMarkRead(item: ComposeNotice, showLoading: Boolean) = intent {
        withActionLoading(enable = showLoading) {
            userRepository.submitMarkNotificationRead(item.id)
            userRepository.fetchUserNotify(false)
        }.onFailure {
            postToast { it.errMsg }
        }.onSuccess {
            reduceData { state.copy(notifications = it.toPersistentList()) }
            postEffect { NotificationSideEffect.OnRefreshNotificationCount }
        }
    }
}