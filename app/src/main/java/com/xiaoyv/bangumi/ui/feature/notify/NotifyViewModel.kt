package com.xiaoyv.bangumi.ui.feature.notify

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.api.parser.impl.parserNotify
import com.xiaoyv.common.helper.NotifyHelper

/**
 * Class: [NotifyViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class NotifyViewModel : BaseListViewModel<NotifyEntity>() {

    override fun onViewCreated() {
        markAllRead()
    }

    override suspend fun onRequestListImpl(): List<NotifyEntity> {
        return BgmApiManager.bgmWebApi.queryNotifyAll().parserNotify()
    }

    private fun markAllRead() {
        NotifyHelper.markAllRead()
    }
}