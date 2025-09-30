package com.xiaoyv.bangumi.features.garden.business

/**
 * [GardenSideEffect]
 *
 * @author why
 * @since 2025/1/12
 */
sealed class GardenSideEffect {
    object OnRefresh : GardenSideEffect()
}