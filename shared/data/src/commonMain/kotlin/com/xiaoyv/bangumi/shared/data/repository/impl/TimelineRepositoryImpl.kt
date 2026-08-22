package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.LikeCommentParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.normalizedReplies
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryPageLimitPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryStepUniquePagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createPagingConfig

class TimelineRepositoryImpl(
    private val client: BgmApiClient,
    private val pagingConfig: PagingConfig,
    private val preferenceStore: PreferenceStore,
) : TimelineRepository {

    private val userCache = HashMap<String, ComposeUser>()

    override fun fetchTimelineDisplayPager(
        @TimelineTarget target: String,
        @TimelineCat type: Int,
        username: String
    ): MemoryPagingController<ComposeTimeline, Long> {
        // 官方 API 暂不支持分类，这里用 Web 代替
        val timelineCat = type.takeIf { cat -> cat != TimelineCat.UNKNOWN }
        if (timelineCat != null) {
            return createMemoryPageLimitPagingController(
                pagingConfig = createPagingConfig(20),
                idSelector = { it.id },
                onLoadData = {
                    client.requestNextTimelineApi {
                        getTimelineWebApi(
                            mode = target,
                            username = username.takeIf { text -> text.isNotBlank() },
                            type = when (timelineCat) {
                                TimelineCat.DAILY -> "say"
                                TimelineCat.WIKI -> "wiki"
                                TimelineCat.SUBJECT -> "subject"
                                TimelineCat.PROGRESS -> "progress"
                                TimelineCat.STATUS -> "say"
                                TimelineCat.BLOG -> "blog"
                                TimelineCat.INDEX -> "index"
                                TimelineCat.MONO -> "mono"
                                TimelineCat.WINDOW -> "doujin"
                                else -> null
                            },
                            page = it
                        ).map { timeline -> timeline.normalized() }
                    }.getOrThrow()
                }
            )
        }
        return createMemoryStepUniquePagingController(
            pagingConfig = pagingConfig,
            idSelector = { it.id },
            onLoadData = {
                val displays = if (target == TimelineTarget.USER) {
                    client.requestNextUserApi {
                        val user = userCache.getOrPut(username) { getUser(username) }

                        getUserTimeline(
                            username = username,
                            cat = null,
                            limit = pagingConfig.pageSize,
                            until = it?.toLong()
                        ).map { timeline -> timeline.copy(user = user).normalized() }
                    }.getOrThrow()
                } else {
                    client.requestNextTimelineApi {
                        getTimeline(
                            mode = target,
                            cat = null,
                            limit = pagingConfig.pageSize,
                            until = it?.toLong()
                        ).map { timeline -> timeline.normalized() }
                    }.getOrThrow()
                }
                displays to displays.lastOrNull()?.id?.toInt()
            }
        )
    }

    override suspend fun fetchTimelineReplies(timelineId: Long): Result<List<ComposeReply>> = client.requestNextTimelineApi {
        getTimelineReplies(timelineId.toInt()).normalizedReplies()
    }

    override suspend fun submitCreateTimeline(
        content: String,
        turnstileToken: String
    ): Result<ComposeId> = client.requestNextTimelineApi {
        createTimelineSay(
            CreateCommentParam(
                content = content,
                turnstileToken = turnstileToken
            )
        )
    }

    override suspend fun submitTimelineReply(
        timelineId: Long,
        content: String,
        turnstile: String,
        replyTo: Long?,
    ): Result<ComposeId> = client.requestNextTimelineApi {
        createTimelineReply(
            timelineID = timelineId.toInt(),
            param = CreateCommentParam(
                content = content,
                turnstileToken = turnstile,
                replyTo = replyTo ?: 0,
            ),
        )
    }

    override suspend fun submitTimelineReaction(timelineId: Long, value: String?): Result<Unit> = client.requestNextTimelineApi {
        if (value.isNullOrBlank()) {
            unlikeTimeline(timelineID = timelineId)
        } else {
            likeTimeline(timelineId, LikeCommentParam(value.toInt()))
        }
    }

    override suspend fun submitDeleteTimeline(timelineId: Long): Result<Unit> {
        return client.requestWebApi {
            submitDeleteTimeline(timelineId, gh = preferenceStore.userInfo.formHash)
        }

//        Json Api 会报错500，暂时用 WebApi 代替
//        return client.requestNextTimelineApi {
//            deleteTimeline(timelineId)
//        }
    }
}
