package com.xiaoyv.bangumi.ui.discover.mono.list

import androidx.lifecycle.MutableLiveData
import com.xiaoyv.bangumi.base.BaseListViewModel
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.impl.parserMonoList
import com.xiaoyv.common.api.parser.impl.parserUserMonoList
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.config.annotation.MonoOrderByType
import com.xiaoyv.common.config.bean.FilterEntity
import com.xiaoyv.common.config.bean.SampleImageEntity

/**
 * Class: [MonoListViewModel]
 *
 * @author why
 * @since 12/21/23
 */
class MonoListViewModel : BaseListViewModel<SampleImageEntity>() {
    internal var isCharacter: Boolean = false
    internal var userId = ""

    @MonoOrderByType
    internal var orderByType = MutableLiveData(MonoOrderByType.TYPE_ALL)

    /**
     * 选中的过滤条件
     */
    internal var selectedOptions: List<FilterEntity.OptionItem> = emptyList()

    internal val toolbarMenus by lazy {
        listOf(
            MonoOrderByType.TYPE_ALL,
            MonoOrderByType.TYPE_COLLECT,
            MonoOrderByType.TYPE_COMMENT,
            MonoOrderByType.TYPE_DATELINE,
            MonoOrderByType.TYPE_TITLE
        )
    }

    /**
     * 分类区别
     */
    private val filterType by lazy {
        if (isCharacter) {
            listOf(
                FilterEntity.OptionItem(title = "角色", field = "type", id = "1"),
                FilterEntity.OptionItem(title = "机体", field = "type", id = "2"),
                FilterEntity.OptionItem(title = "船舰", field = "type", id = "3"),
                FilterEntity.OptionItem(title = "组织机构", field = "type", id = "4"),
            )
        } else {
            listOf(
                FilterEntity.OptionItem(title = "声优", field = "type", id = "1"),
                FilterEntity.OptionItem(title = "漫画家", field = "type", id = "2"),
                FilterEntity.OptionItem(title = "绘师", field = "type", id = "7"),
                FilterEntity.OptionItem(title = "制作人", field = "type", id = "3"),
                FilterEntity.OptionItem(title = "音乐人", field = "type", id = "4"),
                FilterEntity.OptionItem(title = "作家", field = "type", id = "8"),
                FilterEntity.OptionItem(title = "演员", field = "type", id = "6"),
            )
        }
    }

    internal val filterMenu by lazy {
        listOf(
            FilterEntity(options = filterType, id = "类型"),
            FilterEntity(
                options = listOf(
                    FilterEntity.OptionItem(title = "A", field = "bloodtype", id = "1"),
                    FilterEntity.OptionItem(title = "B", field = "bloodtype", id = "2"),
                    FilterEntity.OptionItem(title = "AB", field = "bloodtype", id = "3"),
                    FilterEntity.OptionItem(title = "O", field = "bloodtype", id = "4")
                ),
                id = "血型"
            ),
            FilterEntity(
                options = listOf(
                    FilterEntity.OptionItem(title = "男性", field = "gender", id = "1"),
                    FilterEntity.OptionItem(title = "女性", field = "gender", id = "2")
                ),
                id = "性别"
            ),
            FilterEntity(
                options = listOf(
                    FilterEntity.OptionItem(title = "一月", field = "month", id = "1"),
                    FilterEntity.OptionItem(title = "二月", field = "month", id = "2"),
                    FilterEntity.OptionItem(title = "三月", field = "month", id = "3"),
                    FilterEntity.OptionItem(title = "四月", field = "month", id = "4"),
                    FilterEntity.OptionItem(title = "五月", field = "month", id = "5"),
                    FilterEntity.OptionItem(title = "六月", field = "month", id = "6"),
                    FilterEntity.OptionItem(title = "七月", field = "month", id = "7"),
                    FilterEntity.OptionItem(title = "八月", field = "month", id = "8"),
                    FilterEntity.OptionItem(title = "九月", field = "month", id = "9"),
                    FilterEntity.OptionItem(title = "十月", field = "month", id = "10"),
                    FilterEntity.OptionItem(title = "十一月", field = "month", id = "11"),
                    FilterEntity.OptionItem(title = "十二月", field = "month", id = "12"),
                ),
                id = "出生月份"
            ),
        )
    }

    override suspend fun onRequestListImpl(): List<SampleImageEntity> {
        return if (userId.isNotBlank()) {
            BgmApiManager.bgmWebApi.queryUserMonoList(
                userId = userId,
                monoType = if (isCharacter) BgmPathType.TYPE_CHARACTER else BgmPathType.TYPE_PERSON,
                page = current
            ).parserUserMonoList()
        } else {
            BgmApiManager.bgmWebApi.queryMonoList(
                monoType = if (isCharacter) BgmPathType.TYPE_CHARACTER else BgmPathType.TYPE_PERSON,
                orderByType = orderByType.value,
                page = current,
                param = buildQueryMap()
            ).parserMonoList()
        }
    }

    /**
     * 构建参数
     */
    private fun buildQueryMap(): Map<String, String> {
        return selectedOptions.associate { it.field to it.id }
    }
}