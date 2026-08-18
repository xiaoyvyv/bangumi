package com.xiaoyv.bangumi.features.settings.dns.business

sealed interface SettingsDnsResolverSideEffect {
    data object NavigateMain : SettingsDnsResolverSideEffect
}
