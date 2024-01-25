package com.xiaoyv.common.config.bean

import com.xiaoyv.common.helper.callback.IdEntity

/**
 * Class: [LanguageModel]
 *
 * @author why
 * @since 1/24/24
 */
data class LanguageModel(
    override var id: String = "",
    var name: String = "",
    var download: Boolean = false,
) : IdEntity
