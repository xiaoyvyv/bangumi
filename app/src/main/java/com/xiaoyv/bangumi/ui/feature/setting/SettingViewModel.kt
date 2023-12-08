package com.xiaoyv.bangumi.ui.feature.setting

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.NotifyEntity
import com.xiaoyv.common.api.parser.impl.parserNotify

/**
 * Class: [SettingViewModel]
 *
 * @author why
 * @since 12/8/23
 */
class SettingViewModel : BaseListViewModel<NotifyEntity>() {

    override suspend fun onRequestListImpl(): List<NotifyEntity> {
        return BgmApiManager.bgmWebApi.queryNotifyAll().parserNotify()
    }
}