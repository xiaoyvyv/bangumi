package com.xiaoyv.common.api.parser.entity

/**
 * Class: [HomeIndexEntity]
 *
 * @author why
 * @since 11/24/23
 */
data class HomeIndexEntity(
    var banner: HomeIndexBannerEntity = HomeIndexBannerEntity(),
    var images: List<HomeIndexCardEntity> = emptyList(),
    var calendar: HomeIndexCalendarEntity = HomeIndexCalendarEntity()
)