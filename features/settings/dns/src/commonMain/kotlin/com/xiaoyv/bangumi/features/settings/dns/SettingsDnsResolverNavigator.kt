package com.xiaoyv.bangumi.features.settings.dns

import com.xiaoyv.bangumi.features.settings.dns.business.SettingsDnsResolverViewModel
import org.koin.core.module.dsl.viewModelOf
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import com.xiaoyv.bangumi.shared.ui.component.navigation.navScope
import com.xiaoyv.bangumi.shared.ui.component.navigation.navigator
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val settingsDnsResolverModule = module {
    viewModelOf(::SettingsDnsResolverViewModel)

    navScope {
        navigation<Screen.DnsResolver> { key ->
            SettingsDnsResolverRoute(
                viewModel = koinViewModel(),
                onNavScreen = { navigator.navigate(it) },
            )
        }
    }
}
