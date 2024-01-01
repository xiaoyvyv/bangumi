package com.xiaoyv.bangumi.special.magnet

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.widget.setting.SearchOptionView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * Class: [MagnetViewModel]
 *
 * @author why
 * @since 1/1/24
 */
class MagnetViewModel : BaseListViewModel<AnimeMagnetEntity.Resource>() {
    internal val onOptionLiveData = MutableLiveData<List<SearchOptionView.Option>>()

    private val magnetUrl: String
        get() = ConfigHelper.magnetSearchApi

    internal var keyword: String = ""
    internal var subGroup: String? = null
    internal var type: String? = null

    override fun onViewCreated() {
        queryTypes()
    }

    /**
     * 查询 Types
     */
    internal fun queryTypes() {
        launchUI {
            require(magnetUrl.isNotBlank())

            val entities = awaitAll(
                async(Dispatchers.IO) { BgmApiManager.bgmJsonApi.queryAnimeMagnetType(magnetApi = magnetUrl) },
                async(Dispatchers.IO) { BgmApiManager.bgmJsonApi.queryAnimeMagnetSubGroup(magnetApi = magnetUrl) }
            )

            val type = entities.first().let {
                SearchOptionView.Option(name = "类型", fieldName = "type",
                    options = it.types.orEmpty().map { type ->
                        SearchOptionView.OptionItem(
                            name = type.name.orEmpty(),
                            value = type.id.toString()
                        )
                    }
                )
            }

            val subGroup = entities.last().let {
                SearchOptionView.Option(name = "字幕组", fieldName = "subgroups",
                    options = it.subgroups.orEmpty().map { type ->
                        SearchOptionView.OptionItem(
                            name = type.name.orEmpty(),
                            value = type.id.toString()
                        )
                    }
                )
            }

            onOptionLiveData.value = listOf(type, subGroup)
        }
    }

    override suspend fun onRequestListImpl(): List<AnimeMagnetEntity.Resource> {
        require(magnetUrl.isNotBlank() && URLUtil.isNetworkUrl(magnetUrl)) { "请先配置资源搜索节点" }
        require(keyword.isNotBlank()) { "请输入关键词搜索资源" }

        return BgmApiManager.bgmJsonApi.queryAnimeMagnetList(
            magnetApi = magnetUrl,
            keyword = keyword,
            subgroup = subGroup,
            type = type,
        ).resources.orEmpty()
    }

    /**
     * 搜索参数
     */
    fun fillParam(selectedOptions: List<SearchOptionView.Option>) {
        type = selectedOptions.find { it.fieldName == "type" }?.selected?.value
        subGroup = selectedOptions.find { it.fieldName == "subgroups" }?.selected?.value
    }
}