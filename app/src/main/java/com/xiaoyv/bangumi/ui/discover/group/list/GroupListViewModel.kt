package com.xiaoyv.bangumi.ui.discover.group.list

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserGroupList
import com.xiaoyv.common.config.bean.SampleImageEntity
import com.xiaoyv.common.helper.UserHelper

/**
 * Class: [GroupListViewModel]
 *
 * @author why
 * @since 12/12/23
 */
class GroupListViewModel : BaseListViewModel<SampleImageEntity>() {
    internal var isSortByNewest = false

    internal var userId: String = ""
    internal var requireLogin: Boolean = false

    internal val isMine: Boolean
        get() = userId.isNotBlank() && userId == UserHelper.currentUser.id

    internal val categoryMap by lazy {
        listOf(
            "所有小组" to listOf("all" to "所有小组"),
            "二次元" to listOf(
                "AC" to "二次元",
                "CV" to "声优",
                "Director" to "监督",
                "Illustartor" to "画师",
                "Bangumi" to "番组",
                "Doujin" to "同人",
                "14" to "其他"
            ),
            "游戏" to listOf(
                "Game" to "游戏",
                "Platform" to "主机",
                "Corporation" to "厂商",
                "Serial" to "系列",
                "23" to "其他"
            ),
            "技术" to listOf(
                "Tech" to "技术",
                "Internet" to "互联网",
                "Coding" to "编程与软件",
                "Dgital" to "数码产品",
                "Hardware" to "电脑硬件技术",
                "44" to "其他"
            ),
            "生活" to listOf(
                "Life" to "生活",
                "Chat" to "闲聊",
                "Place" to "地域",
                "32" to "其他"
            )
        )
    }

    private val monolayer by lazy {
        categoryMap.flatMap { it.second }
    }

    internal var category = "all"

    internal fun currentCategoryName(): String {
        return monolayer.find { it.first == category }?.second.orEmpty()
    }

    override suspend fun onRequestListImpl(): List<SampleImageEntity> {
        if (requireLogin) {
            require(UserHelper.isLogin) { "你还没有登录呢" }
        }

        return if (userId.isNotBlank()) {
            BgmApiManager.bgmWebApi.queryUserGroup(userId).parserGroupList()
        } else {
            BgmApiManager.bgmWebApi.queryGroupList(category, current).parserGroupList()
        }
    }
}