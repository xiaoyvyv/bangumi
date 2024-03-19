package com.xiaoyv.bangumi.special.yuc

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.bean.YucSectionEntity
import com.xiaoyv.common.kts.currentMonth
import com.xiaoyv.common.kts.currentYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [YucViewModel]
 *
 * @author why
 * @since 3/19/24
 */
class YucViewModel : BaseViewModel() {
    internal val onYucSectionLiveData = MutableLiveData<List<YucSectionEntity>?>()

    override fun onViewCreated() {
        loadYucSection()
    }

    private fun loadYucSection() {
        launchUI {
            onYucSectionLiveData.value = withContext(Dispatchers.IO) {
                val currentYear = currentYear
                val currentMonth = currentMonth

                val listOf = mutableListOf<YucSectionEntity>()
                for (i in currentYear downTo 2020) {
                    listOf.add(YucSectionEntity(i.toString()))
                }

                // 当前位于10月份后，再加一年
                if (currentMonth > 10) {
                    listOf.add(0, YucSectionEntity((currentYear + 1).toString()))
                }

                listOf
            }
        }
    }
}