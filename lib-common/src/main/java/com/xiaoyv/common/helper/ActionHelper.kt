package com.xiaoyv.common.helper

import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.entity.LoadingState
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.UserDetailEntity
import com.xiaoyv.common.api.parser.impl.parserUserInfo
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.ReportType
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.widget.dialog.AnimeReportDialog
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun FragmentActivity.showActionMenu(
    view: View,
    userNumberId: String,
    @ReportType reportType: String,
    loadingState: MutableLiveData<LoadingState>? = null,
) {
    PopupMenu(this, view)
        .apply {
            menu.add(i18n(CommonString.action_block)).setOnMenuItemClickListener {
                ignoreUser(userNumberId, loadingState)
                true
            }
            menu.add(i18n(CommonString.action_report)).setOnMenuItemClickListener {
                AnimeReportDialog.show(userNumberId, reportType)
                true
            }
        }
        .show()
}

/**
 * 绝交用户
 *
 * @author why
 * @since 12/9/23
 */
fun FragmentActivity.ignoreUser(
    userId: String,
    loadingState: MutableLiveData<LoadingState>? = null,
) {
    showConfirmDialog(
        message = i18n(CommonString.action_block_message),
        onConfirmClick = {
            launchUI(
                state = loadingState,
                error = {
                    it.printStackTrace()

                    showToastCompat(i18n(CommonString.action_block_error))
                },
                block = {
                    withContext(Dispatchers.IO) {
                        val userInfo: UserDetailEntity =
                            BgmApiManager.bgmWebApi.queryUserInfo(userId).parserUserInfo(userId)
                                .apply {
                                    require(gh.isNotBlank()) {
                                        i18n(CommonString.action_block_error)
                                    }
                                }
                        val referer = BgmApiManager.buildReferer(BgmPathType.TYPE_FRIEND, userId)

                        BgmApiManager.bgmWebApi.postIgnoreUser(referer, userId, userInfo.gh)
                            .apply {
                                require(status.equals("ok", true)) {
                                    i18n(CommonString.action_block_error)
                                }
                            }
                        // 刷新屏蔽用户缓存
                        UserHelper.refreshBlockUser()
                    }
                    showToastCompat(i18n(CommonString.action_block_success))
                }
            )
        })
}