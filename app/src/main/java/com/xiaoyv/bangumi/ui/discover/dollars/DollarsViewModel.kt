package com.xiaoyv.bangumi.ui.discover.dollars

import com.blankj.utilcode.util.ColorUtils
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchIO
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.DollarsEntity
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.widget.kts.copyAdd
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.getAttrColor
import com.xiaoyv.widget.kts.mutableCopyOf
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * Class: [DollarsViewModel]
 *
 * @author why
 * @since 1/4/24
 */
class DollarsViewModel : BaseListViewModel<DollarsEntity>() {
    /**
     * 轮询的数据不自动滑到底部
     */
    internal var needScrollToBottom = true
    private var pollMessagesJob: Job? = null

    internal var needSmoothScrollToBottom = false

    override suspend fun onRequestListImpl(): List<DollarsEntity> {
        return BgmApiManager.bgmWebApi.queryDollars(sinceId = "")
    }

    override fun onViewCreated() {
        pollMessages()
    }

    private fun pollMessages() {
        pollMessagesJob = launchIO {
            while (isActive) {
                delay(5000)
                needScrollToBottom = false
                refresh()
            }
        }
    }

    fun sendMessage(input: String) {
        val sendMessage = DollarsEntity(
            id = UserHelper.currentUser.id,
            nickname = UserHelper.currentUser.nickname,
            avatar = UserHelper.currentUser.avatar,
            color = ColorUtils.int2RgbString(context.getAttrColor(GoogleAttr.colorPrimary)),
            msg = input,
            isSending = true
        )

        needSmoothScrollToBottom = true
        onListLiveData.value = onListLiveData.value.copyAdd(sendMessage)

        launchUI(
            error = {
                showToastCompat(it.errorMsg)

                refreshStateToSent()
            },
            block = {
                withContext(Dispatchers.IO) {
                    BgmApiManager.bgmWebApi.postDollars(message = input)
                }

                refreshStateToSent()
            }
        )
    }

    /**
     * 刷新发送完成
     */
    private fun refreshStateToSent() {
        needSmoothScrollToBottom = true
        onListLiveData.value = onListLiveData.value.mutableCopyOf().apply {
            for (i in size - 1 downTo 0) {
                val entity: DollarsEntity = get(i)
                if (entity.isSending) {
                    this[i] = entity.copy(isSending = false)
                }
            }
        }
    }

    override fun onCleared() {
        pollMessagesJob?.cancel()
        pollMessagesJob = null
    }
}