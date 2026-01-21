package com.xiaoyv.bangumi.features.mono.browser.business

import androidx.lifecycle.SavedStateHandle
import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.mono_character_list
import com.xiaoyv.bangumi.core_resource.resources.mono_person_list
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_collect
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_comment
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_dateline
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_title
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.types.MonoOrderByType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoBrowserBody
import com.xiaoyv.bangumi.shared.data.model.ui.PageUI
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.tab.ComposeTextTab
import kotlinx.collections.immutable.persistentListOf

/**
 * [MonoBrowserViewModel]
 *
 * @author why
 * @since 2025/1/12
 */
class MonoBrowserViewModel(
    savedStateHandle: SavedStateHandle,
    private val args: Screen.MonoBrowser,
) : BaseViewModel<MonoBrowserState, MonoBrowserSideEffect, MonoBrowserEvent.Action>(savedStateHandle) {

    override fun initSate(onCreate: Boolean) = MonoBrowserState(
        title = if (args.type == MonoType.CHARACTER) Res.string.mono_character_list else Res.string.mono_person_list,
        param = initParam(),
        typeFilters = createTypeFilters(args.type),
        genderFilters = persistentListOf(
            ComposeTextTab(labelText = "男性", type = "1"),
            ComposeTextTab(labelText = "女性", type = "2")
        ),
        bloodFilters = persistentListOf(
            ComposeTextTab(labelText = "A", type = "1"),
            ComposeTextTab(labelText = "B", type = "2"),
            ComposeTextTab(labelText = "AB", type = "3"),
            ComposeTextTab(labelText = "O", type = "4")
        ),
        monthFilters = persistentListOf(
            ComposeTextTab(labelText = "一月", type = "1"),
            ComposeTextTab(labelText = "二月", type = "2"),
            ComposeTextTab(labelText = "三月", type = "3"),
            ComposeTextTab(labelText = "四月", type = "4"),
            ComposeTextTab(labelText = "五月", type = "5"),
            ComposeTextTab(labelText = "六月", type = "6"),
            ComposeTextTab(labelText = "七月", type = "7"),
            ComposeTextTab(labelText = "八月", type = "8"),
            ComposeTextTab(labelText = "九月", type = "9"),
            ComposeTextTab(labelText = "十月", type = "10"),
            ComposeTextTab(labelText = "十一月", type = "11"),
            ComposeTextTab(labelText = "十二月", type = "12"),
        ),
        sortFilters = persistentListOf(
            ComposeTextTab(type = MonoOrderByType.TYPE_DATELINE, label = Res.string.type_mono_query_dateline),
            ComposeTextTab(type = MonoOrderByType.TYPE_COLLECT, label = Res.string.type_mono_query_collect),
            ComposeTextTab(type = MonoOrderByType.TYPE_COMMENT, label = Res.string.type_mono_query_comment),
            ComposeTextTab(type = MonoOrderByType.TYPE_TITLE, label = Res.string.type_mono_query_title),
        )
    )


    override fun onEvent(event: MonoBrowserEvent.Action) {
        when (event) {
            is MonoBrowserEvent.Action.OnRefresh -> refresh(loading = event.loading)
            is MonoBrowserEvent.Action.OnChangeFilterOrderBy -> onChangeFilterOrderBy(event.orderBy)
            is MonoBrowserEvent.Action.OnChangeFilterType -> onChangeFilterType(event.type)
            is MonoBrowserEvent.Action.OnChangeFilterGender -> onChangeFilterGender(event.gender)
            is MonoBrowserEvent.Action.OnChangeFilterBloodType -> onChangeFilterBloodType(event.bloodType)
            is MonoBrowserEvent.Action.OnChangeFilterMonth -> onChangeFilterMonth(event.month)
        }
    }

    private fun onChangeFilterOrderBy(orderBy: String) = action {
        reduceContent { state.copy(param = state.param.copy(orderBy = orderBy)) }
    }

    private fun onChangeFilterType(type: String) = action {
        reduceContent {
            if (state.param.mutexParam.type == type) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterType(type)))
            }
        }
    }

    private fun onChangeFilterGender(gender: String) = action {
        reduceContent {
            if (state.param.mutexParam.gender == gender) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterGender(gender)))
            }
        }
    }

    private fun onChangeFilterBloodType(bloodType: String) = action {
        reduceContent {
            if (state.param.mutexParam.bloodType == bloodType) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterBloodType(bloodType)))
            }
        }
    }

    private fun onChangeFilterMonth(month: String) = action {
        reduceContent {
            if (state.param.mutexParam.month == month) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterMonth(month)))
            }
        }
    }

    private fun initParam(): MonoBrowserBody {
        return args.param.copy(
            ui = PageUI(gridLayout = false),
            orderBy = args.param.orderBy.ifBlank { MonoOrderByType.TYPE_DATELINE }
        )
    }

    private fun createTypeFilters(@MonoType type: Int): SerializeList<ComposeTextTab<String>> {
        return if (type == MonoType.CHARACTER) {
            persistentListOf(
                ComposeTextTab(labelText = "角色", type = "1"),
                ComposeTextTab(labelText = "机体", type = "2"),
                ComposeTextTab(labelText = "船舰", type = "3"),
                ComposeTextTab(labelText = "组织机构", type = "4"),
            )
        } else {
            persistentListOf(
                ComposeTextTab(labelText = "声优", type = "1"),
                ComposeTextTab(labelText = "漫画家", type = "2"),
                ComposeTextTab(labelText = "绘师", type = "7"),
                ComposeTextTab(labelText = "制作人", type = "3"),
                ComposeTextTab(labelText = "音乐人", type = "4"),
                ComposeTextTab(labelText = "作家", type = "8"),
                ComposeTextTab(labelText = "演员", type = "6"),
            )
        }
    }
}