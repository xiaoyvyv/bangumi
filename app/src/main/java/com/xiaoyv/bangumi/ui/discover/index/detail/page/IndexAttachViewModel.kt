package com.xiaoyv.bangumi.ui.discover.index.detail.page

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.bangumi.ui.discover.index.detail.IndexDetailViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.IndexAttachEntity
import com.xiaoyv.common.api.parser.impl.parserIndexAttach
import com.xiaoyv.common.config.annotation.IndexTabCatType

/**
 * Class: [IndexAttachViewModel]
 *
 * @author why
 * @since 12/17/23
 */
class IndexAttachViewModel : BaseListViewModel<IndexAttachEntity>() {
    internal var indexId = ""

    @IndexTabCatType
    internal var indexTabCatType: String = IndexTabCatType.TYPE_ALL

    internal lateinit var activityViewModel: IndexDetailViewModel

    override suspend fun onRequestListImpl(): List<IndexAttachEntity> {
        // 第一个页面默认全部条目，不需要再次查询网络，直接读取 IndexDetailActivity 的 VM 数据
        if (indexTabCatType.isBlank()) {
            return activityViewModel.onIndexDetailLiveData.value?.totalAttach.orEmpty()
        }
        return BgmApiManager.bgmWebApi.queryIndexDetail(indexId, indexTabCatType)
            .parserIndexAttach(indexTabCatType)
    }
}