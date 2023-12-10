package com.xiaoyv.bangumi.ui.feature.almanac

import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.config.bean.AlmanacEntity
import com.xiaoyv.common.kts.currentYear

/**
 * Class: [AlmanacViewModel]
 *
 * @author why
 * @since 12/10/23
 */
class AlmanacViewModel : BaseListViewModel<AlmanacEntity>() {
    override suspend fun onRequestListImpl(): List<AlmanacEntity> {
        val listOf = arrayListOf<AlmanacEntity>()
        for (i in (currentYear - 1) downTo 2010) {
            listOf.add(AlmanacEntity(id = i.toString(), image = ""))
        }
        return listOf
    }
}