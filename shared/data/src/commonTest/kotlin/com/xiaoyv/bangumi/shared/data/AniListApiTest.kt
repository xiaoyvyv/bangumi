package com.xiaoyv.bangumi.shared.data

import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.data.api.anilist.AniListApi
import com.xiaoyv.bangumi.shared.data.api.anilist.createAniListApi
import com.xiaoyv.bangumi.shared.data.api.client.createApiKtorfit
import com.xiaoyv.bangumi.shared.data.model.request.anilist.ComposeAniListGraphQLRequest
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListMedia
import com.xiaoyv.bangumi.shared.data.model.response.anilist.ComposeAniListPage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.subject.ComposeSubject
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.decodeFromJsonElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * [AniListApiTest]
 *
 * AniList API 与 Repository 的单元测试
 */
class AniListApiTest {

    private val aniListApi: AniListApi by lazy {
        val httpClient = HttpClient {
            install(ContentNegotiation) {
                json(defaultJson)
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
        val ktorfit = createApiKtorfit(httpClient, "https://graphql.anilist.co/")
        ktorfit.createAniListApi()
    }

    @Test
    fun testFetchAniListMediaBySubjectsBatchSingleQuery() {
        runBlocking {
            val subjects = listOf(
                ComposeSubject(id = 1001, name = "二十世紀電氣目録-ユーレカ・エヴリカ-", nameCn = "二十世纪电气目录"),
                ComposeSubject(id = 1002, name = "鬼滅の刃", nameCn = "鬼灭之刃"),
                ComposeSubject(id = 1003, name = "故意搜不到的假番剧名_XYZ_999", nameCn = "假番剧"),
            )

            // 1. 自动组装单次 GraphQL 批量别名 Query
            val request = ComposeAniListGraphQLRequest.buildBatchSubjectsQuery(subjects)
            println("=== Generated Batch GraphQL Query ===")
            println("Query: ${request.query}")
            println("Variables: ${request.variables}")

            // 2. 发起单次 HTTP POST 请求
            val response = aniListApi.queryGraphQL(request)
            val dataObject = response.data
            assertNotNull(dataObject)

            // 3. 解析映射数据
            val resultMap = subjects.associate { subject ->
                val subjectKey = "s_${subject.id}"
                val pageElement = dataObject[subjectKey]
                val media = runCatching {
                    val pageData = pageElement?.let { defaultJson.decodeFromJsonElement<ComposeAniListPage>(it) }
                    pageData?.media?.firstOrNull()
                }.getOrNull() ?: ComposeAniListMedia.Empty

                subject.id to media
            }

            println("=== Parsed Batch Result Map ===")
            resultMap.forEach { (subjectId, media) ->
                println("Subject ID: $subjectId -> AniList ID: ${media.id}, Title: ${media.title.displayName}, Episode: ${media.nextAiringEpisode?.episode}")
                println(media.toString())
            }

            assertEquals(3, resultMap.size)
            assertTrue(resultMap.containsKey(1001L))
            assertTrue(resultMap.containsKey(1002L))
            assertTrue(resultMap.containsKey(1003L))

            // 1001 & 1002 有匹配数据
            assertTrue(resultMap[1001L]?.id != 0L)
            assertTrue(resultMap[1002L]?.id != 0L)

            // 1003 搜不到填充 Empty
            assertEquals(0L, resultMap[1003L]?.id)
        }
    }
}
