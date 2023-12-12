package com.xiaoyv.common.helper

import com.blankj.utilcode.util.AppUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [UpdateHelper]
 *
 * @author why
 * @since 12/12/23
 */
object UpdateHelper {

    /**
     * 更新通过对比 Tag 实现，github 发布 Release 必须严格按照 `vX.X.X` 格式创建 Tag
     */
    fun checkUpdate(activity: BaseViewModelActivity<*, *>, showLoading: Boolean = false) {
        activity.launchUI(
            state = if (showLoading) activity.viewModel.loadingDialogState(cancelable = false) else null,
            error = {
                it.printStackTrace()

                if (showLoading) {
                    showToastCompat(it.errorMsg)
                }
            },
            block = {
                val entity = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmJsonApi.queryGithubLatest()
                }

                val tagName = entity.tagName.orEmpty()
                require(tagName.isNotBlank()) { "暂无更新包发布" }

                val versionName = AppUtils.getAppVersionName()
                val tagVersionName = tagName.removePrefix("v").trim()
                require(versionName != tagVersionName) { "当前已经是最新版" }

                val assets = entity.assets.orEmpty().firstOrNull {
                    it.contentType == "application/vnd.android.package-archive"
                            || it.browserDownloadUrl.orEmpty().endsWith(".apk", true)
                }
                requireNotNull(assets) { "暂无更新包发布" }

                activity.showConfirmDialog(
                    title = "检测到更新",
                    message = "检测到有新版本（${entity.tagName}），是否下载更新？",
                    confirmText = "下载",
                    neutralText = "查看详情",
                    cancelable = false,
                    onNeutralClick = {
                        openInBrowser(entity.htmlUrl.orEmpty())
                    },
                    onConfirmClick = {
                        openInBrowser(assets.browserDownloadUrl.orEmpty())
                    }
                )
            }
        )
    }
}