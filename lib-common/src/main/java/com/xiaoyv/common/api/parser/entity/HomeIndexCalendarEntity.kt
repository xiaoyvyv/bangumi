package com.xiaoyv.common.api.parser.entity

/**
 * Class: [HomeIndexCalendarEntity]
 *
 * @author why
 * @since 11/25/23
 */
data class HomeIndexCalendarEntity(
    var tip: String = "",
    var today: List<BgmMediaEntity> = emptyList(),
    var tomorrow: List<BgmMediaEntity> = emptyList(),
)
