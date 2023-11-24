package com.xiaoyv.common.api.parser.entity

/**
 * Class: [BgmMediaEntity]
 *
 * @author why
 * @since 11/24/23
 */
data class HomeIndexCardEntity(
    var title: String = "",
    var titleType: String = "",
    var images: List<BgmMediaEntity> = emptyList(),
)