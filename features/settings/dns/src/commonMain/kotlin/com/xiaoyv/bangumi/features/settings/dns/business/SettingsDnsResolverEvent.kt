package com.xiaoyv.bangumi.features.settings.dns.business

sealed interface SettingsDnsResolverEvent {
    sealed interface Action : SettingsDnsResolverEvent {
        data object OnLaunch : Action
        data object OnRefresh : Action
    }
}
