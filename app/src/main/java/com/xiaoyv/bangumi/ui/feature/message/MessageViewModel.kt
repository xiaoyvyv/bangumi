package com.xiaoyv.bangumi.ui.feature.message

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.MessageEntity
import com.xiaoyv.common.api.parser.impl.parserMessageList
import com.xiaoyv.common.config.annotation.MessageBoxType

/**
 * Class: [MessageViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class MessageViewModel : BaseListViewModel<MessageEntity>() {
    @MessageBoxType
    internal var boxType = MessageBoxType.TYPE_INBOX

    override suspend fun onRequestListImpl(): List<MessageEntity> {
        return BgmApiManager.bgmWebApi.queryMessageList(boxType, current).parserMessageList()
    }

    fun toggleBox() {
        boxType = when (boxType) {
            MessageBoxType.TYPE_INBOX -> MessageBoxType.TYPE_OUTBOX
            else -> MessageBoxType.TYPE_INBOX
        }
        refresh()
    }
}