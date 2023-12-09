package com.xiaoyv.bangumi.ui.feature.message.detail

import com.blankj.utilcode.util.TimeUtils
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchIO
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.api.parser.impl.parserMessageBox
import com.xiaoyv.common.api.parser.impl.parserMessageReplyForm
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.widget.kts.copyAdd
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.mutableCopyOf
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

/**
 * Class: [MessageDetailViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class MessageDetailViewModel : BaseListViewModel<MessageEntity>() {
    internal var replyForm: MutableMap<String, String> = mutableMapOf()
    internal var messageId: String = ""
    internal var fromName: String = ""
    internal var imeHeight: Int = 0

    /**
     * 轮询的数据不自动滑到底部
     */
    internal var isPollMessages = false
    private var pollMessagesJob: Job? = null

    override suspend fun onRequestListImpl(): List<MessageEntity> {
        require(messageId.isNotBlank()) { "消息不存在" }
        val document = BgmApiManager.bgmWebApi.queryMessageBox(messageId)
        replyForm = document.parserMessageReplyForm()
        return document.parserMessageBox(messageId)
    }

    override fun onViewCreated() {
        pollMessages()
    }

    private fun pollMessages() {
        pollMessagesJob = launchIO {
            while (isActive) {
                delay(15000)
                isPollMessages = true
                refresh()
            }
        }
    }

    /**
     * 发送消息
     */
    fun sendMessage(input: String) {
        val sendMessage = MessageEntity(
            id = System.currentTimeMillis().toString(),
            mineAvatar = UserHelper.currentUser.avatar?.large.orEmpty(),
            summary = input,
            time = TimeUtils.getNowString(),
            isSending = true
        )
        onListLiveData.value = onListLiveData.value.copyAdd(sendMessage)

        launchUI(
            error = {
                showToastCompat(it.errorMsg)

                refreshStateToSent()
            },
            block = {
                withContext(Dispatchers.IO) {
                    require(replyForm.isNotEmpty()) { "对方回复你后才可以继续发消息" }

                    // 默认开启交谈模式
                    replyForm["chat"] = "on"
                    replyForm["msg_body"] = input
                    BgmApiManager.bgmWebApi.postMessage(replyForm)
                }

                refreshStateToSent()
            }
        )
    }

    /**
     * 刷新发送完成
     */
    private fun refreshStateToSent() {
        onListLiveData.value = onListLiveData.value.mutableCopyOf().apply {
            for (i in size - 1 downTo 0) {
                val entity: MessageEntity = get(i)
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