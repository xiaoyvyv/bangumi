package com.xiaoyv.bangumi.shared.data.repository.impl

import androidx.paging.PagingConfig
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.login_first_tip
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.core.types.RakuenFlagType
import com.xiaoyv.bangumi.shared.core.types.RakuenType
import com.xiaoyv.bangumi.shared.data.api.client.ApiClient
import com.xiaoyv.bangumi.shared.data.manager.app.UserManager
import com.xiaoyv.bangumi.shared.data.manager.bbcodeToHtml
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeDollarItem
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroupHomepage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeStatus
import com.xiaoyv.bangumi.shared.data.model.response.bgm.rakuen.ComposeRakuenTopic
import com.xiaoyv.bangumi.shared.data.parser.bgm.BlogParser
import com.xiaoyv.bangumi.shared.data.parser.bgm.GroupParser
import com.xiaoyv.bangumi.shared.data.repository.UgcRepository
import com.xiaoyv.bangumi.shared.data.repository.datasource.MemoryPagingController
import com.xiaoyv.bangumi.shared.data.repository.datasource.createMemoryPageLimitPagingController
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.getString

class UgcRepositoryImpl(
    private val client: ApiClient,
    private val pagingConfig: PagingConfig,
    private val blogParser: BlogParser,
    private val groupParser: GroupParser,
    private val userManager: UserManager,
) : UgcRepository {

    override fun fetchRaKuenPager(@RakuenType type: String, filter: String?): MemoryPagingController<ComposeRakuenTopic, String> {
        return createMemoryPageLimitPagingController(
            pagingConfig = pagingConfig,
            onlyOnePage = true,
            idSelector = { it.key },
            onLoadData = {
                // 登录检测
                if (type == RakuenType.MY_GROUP && !userManager.isLogin) {
                    throw ApiHttpException(401, getString(Res.string.login_first_tip))
                }

                client.requestNextTopicApi {
                    val rakuenTopics = getRakuenTopicList(type, 200).result

                    val groupTopicIds = rakuenTopics
                        .filter { item -> item.type == RakuenType.GROUP || item.type == RakuenType.MY_GROUP }
                        .map { it.id }

                    val maxGroupTopicId = groupTopicIds.maxOrNull()
                    val newGroupTopicIds = groupTopicIds.sortedDescending().take(5)

                    val subjectTopicIds = rakuenTopics
                        .filter { item -> item.type == RakuenType.SUBJECT }
                        .map { it.id }

                    val maxSubjectTopicId = subjectTopicIds.maxOrNull()
                    val newSubjectTopicIds = subjectTopicIds.sortedDescending().take(5)

                    rakuenTopics.map { item ->
                        val flags = mutableListOf<String>()

                        val id = item.id

                        // 小组话题：坟贴、新帖、火标记
                        if ((item.type == RakuenType.GROUP || item.type == RakuenType.MY_GROUP) && maxGroupTopicId != null) {
                            if (id < maxGroupTopicId - 10000) {
                                flags.add(RakuenFlagType.TYPE_OLDEST)
                            } else if (id < maxGroupTopicId - 4000) {
                                flags.add(RakuenFlagType.TYPE_OLD)
                            }

                            if (newGroupTopicIds.contains(id)) {
                                flags.add(RakuenFlagType.TYPE_NEW)
                            }
                            if (item.replyCount >= 100) {
                                flags.add(RakuenFlagType.TYPE_HOT)
                            }
                        }

                        // 条目话题：坟贴、新帖、火标记
                        if (item.type == RakuenType.SUBJECT && maxSubjectTopicId != null) {
                            if (id < maxSubjectTopicId - 5000) {
                                flags.add(RakuenFlagType.TYPE_OLDEST)
                            } else if (id < maxSubjectTopicId - 1000) {
                                flags.add(RakuenFlagType.TYPE_OLD)
                            }

                            if (newSubjectTopicIds.contains(id)) {
                                flags.add(RakuenFlagType.TYPE_NEW)
                            }
                            if (item.replyCount >= 50) {
                                flags.add(RakuenFlagType.TYPE_HOT)
                            }
                        }

                        item.copy(flags = flags.toPersistentList())
                    }
                }.getOrThrow()
            }
        )
    }

    override suspend fun fetchDollarsChat(): Result<List<ComposeDollarItem>> = client.requestWebApi {
        fetchDollarChat().map {
            it.copy(
                content = it.msg.bbcodeToHtml(),
                avatar = "https://bgm.tv/pic/user/l/" + it.avatar
            )
        }
    }

    override suspend fun fetchGroupHomepage(): Result<ComposeGroupHomepage> = client.requestWebApi {
        with(groupParser) {
            fetchGroupHomepage()
                .fetchGroupHomepageConverted()
        }
    }

    override suspend fun summitDollarsChat(message: String): Result<ComposeStatus> = client.requestWebApi {
        summitDollarsChat(message = message)
    }
}
