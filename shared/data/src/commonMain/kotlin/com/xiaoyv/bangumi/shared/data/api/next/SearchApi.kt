package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoSearchBody
import com.xiaoyv.bangumi.shared.data.model.request.list.subject.SubjectSearchBody
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMono
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposePage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeSubject
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface SearchApi {
    /**
     * 搜索角色
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     * @param searchCharacter  (optional)
     */
    @POST("p1/search/characters")
    suspend fun searchCharacters(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
        @Body searchCharacter: MonoSearchBody? = null,
    ): ComposePage<ComposeMono>

    /**
     * 搜索人物
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     * @param searchPerson  (optional)
     */
    @POST("p1/search/persons")
    suspend fun searchPersons(
        @Query("limit") limit: Int? = 20,
        @Query("offset") offset: Int? = 0,
        @Body searchPerson: MonoSearchBody? = null,
    ): ComposePage<ComposeMono>

    /**
     * 搜索条目
     *
     * @param limit max 100 (optional, default to 20)
     * @param offset min 0 (optional, default to 0)
     * @param body  (optional)
     */
    @POST("p1/search/subjects")
    suspend fun searchSubjects(
        @Body body: SubjectSearchBody,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): ComposePage<ComposeSubject>
}
