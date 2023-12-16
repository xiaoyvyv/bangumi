package com.xiaoyv.bangumi.ui.feature.setting.block

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.BlockEntity
import com.xiaoyv.common.api.parser.impl.parserBlockUser
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [BlockViewModel]
 *
 * @author why
 * @since 12/17/23
 */
class BlockViewModel : BaseListViewModel<BlockEntity>() {
    override suspend fun onRequestListImpl(): List<BlockEntity> {
        require(UserHelper.isLogin) { "你还有没有登录呢" }
        return BgmApiManager.bgmWebApi.queryPrivacy().parserBlockUser()
    }

    /**
     * 取消绝交
     */
    fun release(numberId: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    require(UserHelper.isLogin) { "你还有没有登录呢" }

                    BgmApiManager.bgmWebApi.queryPrivacy(numberId, UserHelper.formHash)
                }

                // 刷新
                refresh()
            }
        )
    }
}