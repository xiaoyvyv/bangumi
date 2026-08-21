package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.shared.core.types.TimelineCat
import com.xiaoyv.bangumi.shared.core.types.TimelineTarget
import com.xiaoyv.bangumi.shared.data.api.client.BgmApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.PreferenceStore
import com.xiaoyv.bangumi.shared.data.model.request.CreateCommentParam
import com.xiaoyv.bangumi.shared.data.model.request.LikeCommentParam
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeId
import com.xiaoyv.bangumi.shared.data.model.response.bgm.timeline.ComposeTimeline
import com.xiaoyv.bangumi.shared.data.model.response.bgm.user.ComposeUser
import com.xiaoyv.bangumi.shared.data.repository.TimelineRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.createNetworkPageLimitPagingPager
import com.xiaoyv.bangumi.shared.data.repository.datasource.createStepUniquePagingPager

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
    ): Pager<Int, ComposeTimeline> {
        // 官方 API 暂不支持分类，这里用 Web 代替
        val timelineCat = type.takeIf { cat -> cat != TimelineCat.UNKNOWN }
        if (timelineCat != null && target != TimelineTarget.USER) {
            return createNetworkPageLimitPagingPager(
                pagingConfig = pagingConfig,
                keySelector = { it.id },
                onLoadData = {
                    client.requestNextTimelineApi {
                        getTimelineWebApi(
                            mode = target,
                            username = username.takeIf { it.isNotBlank() },
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
                            }
                        )
                    }.getOrThrow()
                }
            )
        }
        return createStepUniquePagingPager(
            pagingConfig = pagingConfig,
            keySelector = { it.id },
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