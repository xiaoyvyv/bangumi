package com.xiaoyv.bangumi.ui.discover.index.detail

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.IndexDetailEntity
import com.xiaoyv.common.api.parser.impl.parserIndexDetail
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [IndexDetailViewModel]
 *
 * @author why
 * @since 12/12/23
 */
class IndexDetailViewModel : BaseViewModel() {
    internal var indexId = ""

    internal val onIndexDetailLiveData = MutableLiveData<IndexDetailEntity?>()
    internal val onDeleteResult = MutableLiveData<Boolean>()

    private val requireIndexUserId: String
        get() = onIndexDetailLiveData.value?.userId.orEmpty()

    private val requireIndexDeleteForms: Map<String, String>
        get() = onIndexDetailLiveData.value?.deleteForms.orEmpty()

    /**
     * 是否为自己的目录
     */
    internal val isMine: Boolean
        get() = requireIndexUserId.isNotBlank() && requireIndexUserId == UserHelper.currentUser.id

    fun queryIndexDetail() {
        launchUI(
            stateView = loadingViewState,
            error = {
                it.printStackTrace()
            },
            block = {
                onIndexDetailLiveData.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryIndexDetail(indexId).parserIndexDetail(indexId)
                }
            }
        )
    }

    /**
     * 删除目录
     */
    fun deleteIndex() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()

                showToastCompat(it.errorMsg)
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.deleteIndex(
                        indexId = indexId,
                        referer = BgmApiManager.buildReferer(
                            type = BgmPathType.TYPE_INDEX,
                            id = requireIndexUserId
                        ),
                        map = requireIndexDeleteForms
                    )
                }
                onDeleteResult.value = true

                UserHelper.notifyDelete(BgmPathType.TYPE_INDEX)
            }
        )
    }
}