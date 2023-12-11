package com.xiaoyv.bangumi.ui.feature.magi.history

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MagiQuestionEntity
import com.xiaoyv.common.api.parser.impl.parserMagiHistory
import com.xiaoyv.common.api.parser.impl.parserMagiQuestion
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MagiHistoryViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class MagiHistoryViewModel : BaseListViewModel<MagiQuestionEntity>() {

    internal var onQueryMagiDetail = UnPeekLiveData<MagiQuestionEntity?>()

    override suspend fun onRequestListImpl(): List<MagiQuestionEntity> {
        return BgmApiManager.bgmWebApi.queryMagiHistory().parserMagiHistory()
    }

    fun queryDetail(entity: MagiQuestionEntity) {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat(it.errorMsg)
            },
            block = {
                onQueryMagiDetail.value = withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.queryMagiDetail(entity.lastQuestionId)
                        .parserMagiQuestion()
                        .apply {
                            lastQuestionId = id
                            lastQuestionTitle = title
                            lastQuestionOptions = options
                            lastQuestionRightRate = entity.lastQuestionRightRate
                            lastQuestionRight = entity.lastQuestionRight
                            lastQuestionCount = entity.lastQuestionCount
                            debugLog { this.toJson(true) }
                        }
                }
            }
        )
    }
}