package com.xiaoyv.bangumi.features.mono.browser.business

import com.xiaoyv.bangumi.core_resource.resources.Res
import com.xiaoyv.bangumi.core_resource.resources.mono_cat_character
import com.xiaoyv.bangumi.core_resource.resources.mono_cat_mecha
import com.xiaoyv.bangumi.core_resource.resources.mono_cat_organization
import com.xiaoyv.bangumi.core_resource.resources.mono_cat_ship
import com.xiaoyv.bangumi.core_resource.resources.mono_character_list
import com.xiaoyv.bangumi.core_resource.resources.mono_gender_female
import com.xiaoyv.bangumi.core_resource.resources.mono_gender_male
import com.xiaoyv.bangumi.core_resource.resources.mono_job_actor
import com.xiaoyv.bangumi.core_resource.resources.mono_job_cv
import com.xiaoyv.bangumi.core_resource.resources.mono_job_illustrator
import com.xiaoyv.bangumi.core_resource.resources.mono_job_mangaka
import com.xiaoyv.bangumi.core_resource.resources.mono_job_musician
import com.xiaoyv.bangumi.core_resource.resources.mono_job_producer
import com.xiaoyv.bangumi.core_resource.resources.mono_job_writer
import com.xiaoyv.bangumi.core_resource.resources.mono_month_1
import com.xiaoyv.bangumi.core_resource.resources.mono_month_10
import com.xiaoyv.bangumi.core_resource.resources.mono_month_11
import com.xiaoyv.bangumi.core_resource.resources.mono_month_12
import com.xiaoyv.bangumi.core_resource.resources.mono_month_2
import com.xiaoyv.bangumi.core_resource.resources.mono_month_3
import com.xiaoyv.bangumi.core_resource.resources.mono_month_4
import com.xiaoyv.bangumi.core_resource.resources.mono_month_5
import com.xiaoyv.bangumi.core_resource.resources.mono_month_6
import com.xiaoyv.bangumi.core_resource.resources.mono_month_7
import com.xiaoyv.bangumi.core_resource.resources.mono_month_8
import com.xiaoyv.bangumi.core_resource.resources.mono_month_9
import com.xiaoyv.bangumi.core_resource.resources.mono_person_list
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_collect
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_comment
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_dateline
import com.xiaoyv.bangumi.core_resource.resources.type_mono_query_title
import com.xiaoyv.bangumi.shared.core.mvi.BaseViewModel
import com.xiaoyv.bangumi.shared.core.mvi.reduceData
import com.xiaoyv.bangumi.shared.core.types.MonoOrderByType
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.MonoBrowserBody
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
    private val args: Screen.MonoBrowser,
) : BaseViewModel<MonoBrowserState, MonoBrowserSideEffect, MonoBrowserEvent.Action>() {

    override fun createInitialState() = MonoBrowserState(
        title = if (args.type == MonoType.CHARACTER) Res.string.mono_character_list else Res.string.mono_person_list,
        param = initParam(),
        typeFilters = createTypeFilters(args.type),
        genderFilters = persistentListOf(
            ComposeTextTab(label = Res.string.mono_gender_male, type = "1"),
            ComposeTextTab(label = Res.string.mono_gender_female, type = "2")
        ),
        bloodFilters = persistentListOf(
            ComposeTextTab(labelText = "A", type = "1"),
            ComposeTextTab(labelText = "B", type = "2"),
            ComposeTextTab(labelText = "AB", type = "3"),
            ComposeTextTab(labelText = "O", type = "4")
        ),
        monthFilters = persistentListOf(
            ComposeTextTab(label = Res.string.mono_month_1, type = "1"),
            ComposeTextTab(label = Res.string.mono_month_2, type = "2"),
            ComposeTextTab(label = Res.string.mono_month_3, type = "3"),
            ComposeTextTab(label = Res.string.mono_month_4, type = "4"),
            ComposeTextTab(label = Res.string.mono_month_5, type = "5"),
            ComposeTextTab(label = Res.string.mono_month_6, type = "6"),
            ComposeTextTab(label = Res.string.mono_month_7, type = "7"),
            ComposeTextTab(label = Res.string.mono_month_8, type = "8"),
            ComposeTextTab(label = Res.string.mono_month_9, type = "9"),
            ComposeTextTab(label = Res.string.mono_month_10, type = "10"),
            ComposeTextTab(label = Res.string.mono_month_11, type = "11"),
            ComposeTextTab(label = Res.string.mono_month_12, type = "12"),
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
            is MonoBrowserEvent.Action.OnRefresh -> refresh(contentLoading = event.loading)
            is MonoBrowserEvent.Action.OnChangeFilterOrderBy -> onChangeFilterOrderBy(event.orderBy)
            is MonoBrowserEvent.Action.OnChangeFilterType -> onChangeFilterType(event.type)
            is MonoBrowserEvent.Action.OnChangeFilterGender -> onChangeFilterGender(event.gender)
            is MonoBrowserEvent.Action.OnChangeFilterBloodType -> onChangeFilterBloodType(event.bloodType)
            is MonoBrowserEvent.Action.OnChangeFilterMonth -> onChangeFilterMonth(event.month)
        }
    }

    private fun onChangeFilterOrderBy(orderBy: String) = intent {
        reduceData { state.copy(param = state.param.copy(orderBy = orderBy)) }
    }

    private fun onChangeFilterType(type: String) = intent {
        reduceData {
            if (state.param.mutexParam.type == type) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterType(type)))
            }
        }
    }

    private fun onChangeFilterGender(gender: String) = intent {
        reduceData {
            if (state.param.mutexParam.gender == gender) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterGender(gender)))
            }
        }
    }

    private fun onChangeFilterBloodType(bloodType: String) = intent {
        reduceData {
            if (state.param.mutexParam.bloodType == bloodType) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterBloodType(bloodType)))
            }
        }
    }

    private fun onChangeFilterMonth(month: String) = intent {
        reduceData {
            if (state.param.mutexParam.month == month) {
                state.copy(param = state.param.copy(mutexParam = MonoBrowserBody.BrowserMonoMutexParam.Empty))
            } else {
                state.copy(param = state.param.copy(mutexParam = state.param.mutexParam.toFilterMonth(month)))
            }
        }
    }

    private fun initParam(): MonoBrowserBody {
        return args.param.copy(
            orderBy = args.param.orderBy.ifBlank { MonoOrderByType.TYPE_DATELINE }
        )
    }

    private fun createTypeFilters(@MonoType type: Int): SerializeList<ComposeTextTab<String>> {
        return if (type == MonoType.CHARACTER) {
            persistentListOf(
                ComposeTextTab(label = Res.string.mono_cat_character, type = "1"),
                ComposeTextTab(label = Res.string.mono_cat_mecha, type = "2"),
                ComposeTextTab(label = Res.string.mono_cat_ship, type = "3"),
                ComposeTextTab(label = Res.string.mono_cat_organization, type = "4"),
            )
        } else {
            persistentListOf(
                ComposeTextTab(label = Res.string.mono_job_cv, type = "1"),
                ComposeTextTab(label = Res.string.mono_job_mangaka, type = "2"),
                ComposeTextTab(label = Res.string.mono_job_illustrator, type = "7"),
                ComposeTextTab(label = Res.string.mono_job_producer, type = "3"),
                ComposeTextTab(label = Res.string.mono_job_musician, type = "4"),
                ComposeTextTab(label = Res.string.mono_job_writer, type = "8"),
                ComposeTextTab(label = Res.string.mono_job_actor, type = "6"),
            )
        }
    }
}