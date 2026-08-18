package com.xiaoyv.bangumi.features.main.tab.tracking

import com.xiaoyv.bangumi.features.main.tab.tracking.business.TrackingViewModel
import com.xiaoyv.bangumi.features.main.tab.tracking.page.TrackingPageViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val trackingModule = module {
    viewModelOf(::TrackingViewModel)
    viewModel { (type: Int) ->
        TrackingPageViewModel(type = type, collectionRepository = get(), personalStateStore = get())
    }

    navScope {
        navigation<Screen.Tracking> { key ->
            TrackingRoute(
                viewModel = koinViewModel { parametersOf(key) },
                onNavScreen = { navigator.navigate(it) },
                onNavUp = { navigator.goBack() }
            )
        }
    }
}
