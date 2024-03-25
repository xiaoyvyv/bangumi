package com.xiaoyv.bangumi.special.thunder.page

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.config.annotation.ThunderTabType
import com.xiaoyv.common.config.bean.TaskInfoEntity
import com.xiaoyv.thunder.Thunder

/**
 * Class: [ThunderTaskViewModel]
 *
 * @author why
 * @since 3/23/24
 */
class ThunderTaskViewModel : BaseListViewModel<TaskInfoEntity>() {
    @ThunderTabType
    internal var tabType: String = ThunderTabType.TYPE_DOWNLOADING

    override suspend fun onRequestListImpl(): List<TaskInfoEntity> {
        return queryTaskList()
    }

    private fun queryTaskList(): List<TaskInfoEntity> {
Thunder.instance

        return listOf()
    }
}