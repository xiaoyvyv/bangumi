package com.xiaoyv.bangumi.special.syncer

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.kts.groupValueOne
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl

/**
 * Class: [SyncerViewModel]
 *
 * @author why
 * @since 1/23/24
 */
class SyncerViewModel : BaseViewModel() {
    internal val isBilibili = MutableLiveData(true)
    internal val idLiveData = MutableLiveData<String?>()

    fun handleId(bId: String) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                idLiveData.value = withContext(Dispatchers.IO) {
                    matchIdFromUrl(bId)
                }
            }
        )
    }

    private suspend fun matchIdFromUrl(bId: String): String? {
        if (bId.isBlank()) return null
        if (bId.toLongOrNull() != null) return bId
        if (!URLUtil.isNetworkUrl(bId)) return null
        val id = "(\\d{3,})".toRegex().groupValueOne(bId.toHttpUrl().encodedPath)
        if (id.isNotBlank()) return id
        val url = BgmApiManager.bgmWebNoRedirectApi.qeuryUrl(bId).headers()["Location"].orEmpty()
        return "(\\d{3,})".toRegex().groupValueOne(url)
    }
}