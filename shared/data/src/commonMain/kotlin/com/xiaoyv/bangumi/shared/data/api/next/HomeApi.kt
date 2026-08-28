package com.xiaoyv.bangumi.shared.data.api.next

import com.xiaoyv.bangumi.shared.core.types.AppJsonApiDsl
import com.xiaoyv.bangumi.shared.data.constant.WebConstant
import com.xiaoyv.bangumi.shared.data.model.response.base.ComposeSection
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay
import com.xiaoyv.bangumi.shared.data.model.response.bgm.home.ComposeHome
import de.jensklingenberg.ktorfit.http.GET

@AppJsonApiDsl
interface HomeApi {

    /**
     * 聚合首页所需的全部数据：进度管理、好友时间线、小组话题、热门小组、热门条目讨论与每日放送
     *
     * 根据登录状态分发：
     * - 未登录时个人区块（进度/时间线/小组话题）为空，仅返回公开区块；
     * - 已登录时返回全部区块。各个区块独立计算，单个区块失败时返回空数据，不影响其他区块。
     */
    @GET("p1/home")
    suspend fun getHome(): ComposeHome

    /**
     * 聚合首页人物首页
     */
    @GET("${WebConstant.URL_BGM_PROXY}p1/mono/home")
    suspend fun getMonoHome(): List<ComposeSection<ComposeMonoDisplay>>
}
