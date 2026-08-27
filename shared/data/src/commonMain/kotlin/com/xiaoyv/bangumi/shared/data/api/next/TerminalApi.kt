package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalMessage
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalPersonality
import com.xiaoyv.bangumi.shared.data.model.response.bgm.terminal.ComposeTerminalSpeech
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

@AppJsonApiDsl
interface TerminalApi {

    /**
     * 获取看板娘人格列表
     *
     * @param creator (optional) 过滤创建者
     */
    @GET("${WebConstant.URL_BGM_PROXY}p1/terminal/personality")
    suspend fun getTerminalPersonalities(
        @Query("creator") creator: String? = null,
    ): List<ComposeTerminalPersonality>

    /**
     * 获取指定人格语录列表
     *
     * @param curPsn 人格ID
     * @param all (optional) true: 返回全部, false: 返回当前人格
     */
    @GET("${WebConstant.URL_BGM_PROXY}p1/terminal/list")
    suspend fun getTerminalSpeeches(
        @Query("cur_psn") curPsn: Long,
        @Query("all") all: Boolean? = null,
    ): List<ComposeTerminalSpeech>

    /**
     * 创建看板娘人格
     *
     * @param name 人格名称
     */
    @FormUrlEncoded
    @POST("${WebConstant.URL_BGM_PROXY}p1/terminal/create")
    suspend fun createTerminalPersonality(
        @Field("name") name: String,
    ): ComposeTerminalMessage

    /**
     * 添加看板娘语录
     *
     * @param speech 语录内容
     * @param curPsn 人格ID
     */
    @FormUrlEncoded
    @POST("${WebConstant.URL_BGM_PROXY}p1/terminal/speech")
    suspend fun createTerminalSpeech(
        @Field("speech") speech: String,
        @Field("cur_psn") curPsn: Long,
    ): ComposeTerminalMessage
}
