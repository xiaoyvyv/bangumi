package com.xiaoyv.bangumi.shared.data.api

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeParade
import com.xiaoyv.bangumi.shared.data.model.response.chore.ComposeBangumiStatus
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path

@AppJsonApiDsl
interface ChoreApi {
    @GET(WebConstant.URL_BGM_STATUS_API)
    suspend fun fetchBangumiStatus(): ComposeBangumiStatus

    /**
     * 获取条目巡礼
     */
    @GET("https://api.anitabi.cn/bangumi/{subjectId}/lite")
    suspend fun fetchSubjectParade(@Path("subjectId") subjectId: Long): ComposeParade
}
