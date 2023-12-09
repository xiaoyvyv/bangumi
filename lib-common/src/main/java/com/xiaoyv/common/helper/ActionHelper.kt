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
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun FragmentActivity.showActionMenu(
    view: View,
    userId: String,
    loadingState: MutableLiveData<LoadingState>? = null
) {
    PopupMenu(this, view)
        .apply {
            menu.add("绝交").setOnMenuItemClickListener {
                ignoreUser(userId, loadingState)
                true
            }
            menu.add("报告疑虑").setOnMenuItemClickListener {
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
    loadingState: MutableLiveData<LoadingState>? = null
) {
    showConfirmDialog(
        message = "确认与该用户绝交？\n绝交后将不再看到用户的所有话题、评论、日志、私信、提醒",
        onConfirmClick = {
            launchUI(
                state = loadingState,
                error = {
                    it.printStackTrace()

                    showToastCompat("绝交失败")
                },
                block = {
                    withContext(Dispatchers.IO) {
                        val userInfo: UserDetailEntity =
                            BgmApiManager.bgmWebApi.queryUserInfo(userId).parserUserInfo(userId)
                                .apply {
                                    require(ignoreHash.isNotBlank()) { "绝交失败" }
                                }
                        BgmApiManager.bgmWebApi.postIgnoreUser(userId, userInfo.ignoreHash)
                            .apply {
                                require(status.equals("ok", true)) { "绝交失败" }
                            }
                    }
                    showToastCompat("绝交成功")
                }
            )
        })
}