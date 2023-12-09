package com.xiaoyv.bangumi.ui.feature.message

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.api.parser.impl.parserMessageList
import com.xiaoyv.common.config.annotation.MessageBoxType
import com.xiaoyv.widget.kts.showToastCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [MessageViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class MessageViewModel : BaseListViewModel<MessageEntity>() {
    @MessageBoxType
    internal var boxType = MessageBoxType.TYPE_INBOX

    private var formGh = ""

    override suspend fun onRequestListImpl(): List<MessageEntity> {
        val (gh, list) =
            BgmApiManager.bgmWebApi.queryMessageList(boxType, current).parserMessageList(boxType)
        if (gh.isNotBlank()) formGh = gh
        return list
    }

    fun toggleBox() {
        boxType = when (boxType) {
            MessageBoxType.TYPE_INBOX -> MessageBoxType.TYPE_OUTBOX
            else -> MessageBoxType.TYPE_INBOX
        }
        refresh()
    }

    fun clearBox() {
        launchUI(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
                showToastCompat("清空失败")
            },
            block = {
                val strings = onListLiveData.value.orEmpty().map { it.id }

                withContext(Dispatchers.IO) {
                    require(formGh.isNotBlank()) { "数据丢失，请重新打开" }

                    BgmApiManager.bgmWebApi.postClearMessageBox(
                        folder = boxType,
                        erasePm = strings,
                        chkall = "on",
                        gh = formGh
                    ).parserMessageList(boxType)
                }

                refresh()

                showToastCompat("当页已清空")
            }
        )
    }
}