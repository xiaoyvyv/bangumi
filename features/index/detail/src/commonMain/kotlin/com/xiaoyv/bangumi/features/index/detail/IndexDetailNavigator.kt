package com.xiaoyv.bangumi.features.index.detail

import com.xiaoyv.bangumi.features.index.detail.business.IndexDetailViewModel
import com.xiaoyv.bangumi.features.index.detail.page.IndexDetailPageViewModel
import com.xiaoyv.bangumi.shared.data.model.request.list.index.ListIndexRelatedParam
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation


val indexDetailModule = module {
    viewModelOf(::IndexDetailViewModel)
    viewModel { (param: ListIndexRelatedParam) ->
        IndexDetailPageViewModel(param = param, get())
    }

    navScope {
        navigation<Screen.IndexDetail> { key ->
            IndexDetailRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
