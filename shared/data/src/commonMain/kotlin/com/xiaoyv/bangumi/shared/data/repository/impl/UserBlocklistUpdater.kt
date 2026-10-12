package com.xiaoyv.bangumi.shared.data.repository.impl

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.action_block_error
import com.xiaoyv.bangumi.core_resource.resources.action_unblock_error
import com.xiaoyv.bangumi.shared.core.exception.ApiHttpException
import com.xiaoyv.bangumi.shared.data.api.BgmWebApi
import com.xiaoyv.bangumi.shared.data.api.next.RelationshipApi
import com.xiaoyv.bangumi.shared.data.api.next.UserApi
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeBlocklist
import com.xiaoyv.bangumi.shared.data.parser.bgm.UserParser
import org.jetbrains.compose.resources.getString

/**
 * 绝交 API 返回服务器错误时，使用网站的独立表单流程恢复操作。
 *
 * @param relationshipApi 读取和修改绝交名单的 API。
 * @param userApi 将用户标识解析为服务端用户信息的 API。
 * @param webApi 使用当前登录 Cookie 的网站接口。
 * @param userParser 解析当前网站表单和操作提示。
 */
internal class UserBlocklistUpdater(
    private val relationshipApi: RelationshipApi,
    private val userApi: UserApi,
    private val webApi: BgmWebApi,
    private val userParser: UserParser,
) {
    /**
     * 更新绝交关系，返回服务端确认的名单。
     *
     * API 出错后先读回状态，避免服务端已写入但响应失败时重复提交。
     * 登录失效、权限不足、用户不存在和限流不会切换到网站重复操作。
     *
     * @param username 用户名或用户 ID。
     * @param blocked 是否与该用户绝交。
     */
    suspend fun update(username: String, blocked: Boolean): ComposeBlocklist {
        try {
            return if (blocked) relationshipApi.addUserToBlocklist(username)
            else relationshipApi.removeUserFromBlocklist(username)
        } catch (error: ApiHttpException) {
            if (error.code !in 500..599) throw error
        }

        val user = userApi.getUser(username)
        require(user.id > 0)
        val before = relationshipApi.getBlocklist()
        if ((user.id in before.blocklist) == blocked) return before

        with(userParser) {
            val form = webApi.fetchUserPrivacyPage().fetchUserBlockFormConverted(user)
            val response = if (blocked) {
                webApi.submitUserBlock(user.username.ifBlank { user.id.toString() }, form.formHash)
            } else {
                val url = requireNotNull(form.unblockUrl) { getString(Res.string.action_unblock_error) }
                webApi.submitUserUnblock(url)
            }
            response.requireLogin()
            response.requireNoError()
        }

        val updated = relationshipApi.getBlocklist()
        check((user.id in updated.blocklist) == blocked) {
            getString(if (blocked) Res.string.action_block_error else Res.string.action_unblock_error)
        }
        return updated
    }
}
