package com.xiaoyv.bangumi.shared.data.repository

import androidx.paging.Pager
import com.xiaoyv.bangumi.shared.core.types.EpisodeType
import com.xiaoyv.bangumi.shared.core.types.SubjectType
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.ListSubjectParam
import com.xiaoyv.bangumi.shared.data.model.request.list.tag.ListTagParam
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeComment
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeEpisode
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeHomeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeParade
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeReply
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectStats
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubjectWebInfo
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeTag
import com.xiaoyv.bangumi.shared.data.model.response.bgm.topic.ComposeTopic
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanPhoto
import com.xiaoyv.bangumi.shared.data.model.response.db.ComposeDoubanSuggest
import kotlinx.coroutines.flow.Flow

/**
 * [SubjectRepository]
 *
 * @author why
 * @since 2025/1/25
 */
interface SubjectRepository {

    fun fetchSubjectPager(param: ListSubjectParam): Pager<Int, ComposeSubjectDisplay>

    fun fetchSubjectCommentPager(subjectId: Long): Pager<Int, ComposeComment>

    fun fetchSearchSuggestion(query: String): Flow<Result<ComposeDoubanSuggest>>

    fun fetchSubjectTagPager(param: ListTagParam): Pager<Int, ComposeTag>

    suspend fun fetchSubjectPreview(subject: ComposeSubject): Result<ComposeDoubanPhoto>

    suspend fun fetchCalendar(): Result<Map<String, List<ComposeHomeSection>>>

    suspend fun fetchTrends(@SubjectType type: Int, limit: Int): Result<List<ComposeHomeSection>>

    suspend fun fetchSubjectDetail(subjectId: Long): Result<ComposeSubject>

    suspend fun fetchSubjectDetailByWeb(subjectId: Long): Result<ComposeSubjectWebInfo>

    suspend fun fetchSubjectParade(subjectId: Long): Result<ComposeParade>

    suspend fun fetchSubjectEpisode(episodeId: Long): Result<ComposeEpisode>

    suspend fun fetchSubjectEpisodes(
        subjectId: Long,
        @EpisodeType type: Int? = null,
        offset: Int = 0,
        limit: Int
    ): Result<List<ComposeEpisode>>

    suspend fun fetchSubjectAllEpisodes(
        subjectId: Long,
        @EpisodeType type: Int? = null,
    ): Result<List<ComposeEpisode>>


    suspend fun fetchSubjectStats(id: Long): Result<ComposeSubjectStats>

    suspend fun fetchSubjectCharacter(
        subjectId: Long,
        type: Int? = null,
        offset: Int = 0,
        limit: Int = 20,
    ): Result<List<ComposeMonoDisplay>>


    suspend fun fetchSubjectPerson(
        subjectId: Long,
        position: Long? = null,
        offset: Int = 0,
        limit: Int = 20,
    ): Result<List<ComposeMonoDisplay>>

    suspend fun fetchSubjectTopic(subjectId: Long): Result<List<ComposeTopic>>

    suspend fun fetchSubjectRelated(subjectId: Long): Result<List<ComposeSubjectDisplay>>

    suspend fun fetchSubjectList(param: ListSubjectParam, offset: Int, pageSize: Int): Result<List<ComposeSubjectDisplay>>

    suspend fun fetchMySubjectTags(subjectId: Long): Result<List<ComposeTag>>

    suspend fun fetchSearchSubjectTags(query: String, @SubjectType type: Int): Result<List<ComposeTag>>

    suspend fun fetchBrowserSubjectTags(@SubjectType type: Int, page: Int): Result<List<ComposeTag>>

    suspend fun submitEpisodeReaction(commentId: Long, value: String?): Result<Unit>
}