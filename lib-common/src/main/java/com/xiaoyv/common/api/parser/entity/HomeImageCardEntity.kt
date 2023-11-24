package com.xiaoyv.common.api.parser.entity

/**
 * Class: [HomeImageEntity]
 *
 * @author why
 * @since 11/24/23
 */
data class HomeImageCardEntity(
    var title: String = "",
    var titleType: String = "",
    var images: List<HomeImageEntity> = emptyList(),
) {

    data class HomeImageEntity(
        var title: String = "",
        var attention: String = "",
        var image: String = "",
        var id: String = "",
    )
}