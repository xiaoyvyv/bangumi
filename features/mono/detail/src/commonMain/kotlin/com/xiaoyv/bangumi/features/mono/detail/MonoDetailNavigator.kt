package com.xiaoyv.bangumi.features.mono.detail

import com.xiaoyv.bangumi.features.mono.detail.business.MonoDetailViewModel
import com.xiaoyv.bangumi.features.mono.detail.page.MonoDetailCastsViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.mono.ListPersonCastParam
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val monoDetailModule = module {
    viewModelOf(::MonoDetailViewModel)
    viewModel { (param: ListPersonCastParam) ->
        MonoDetailCastsViewModel(param = param, monoRepository = get())
    }

    navScope {
        navigation<Screen.MonoDetail> { key ->
            MonoDetailRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
