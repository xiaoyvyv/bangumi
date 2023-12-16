package com.xiaoyv.bangumi.ui.feature.setting

import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [SettingViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class SettingViewModel : BaseViewModel() {
    internal val onRefreshItem = MutableLiveData<Boolean>()

    fun cleanCache() {
        launchUI {
            withContext(Dispatchers.IO) {
                FileUtils.deleteAllInDir(PathUtils.getExternalAppCachePath())
                FileUtils.deleteAllInDir(PathUtils.getInternalAppCachePath())
            }
            onRefreshItem.value = true

            showToastCompat("缓存已清理")
        }
    }
}