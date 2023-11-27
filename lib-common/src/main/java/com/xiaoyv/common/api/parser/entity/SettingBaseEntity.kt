package com.xiaoyv.common.api.parser.entity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.EditProfileOptionType

/**
 * Class: [SettingBaseEntity]
 *
 * @author why
 * @since 11/27/23
 */
@Keep
data class SettingBaseEntity(
    @SerializedName("title")
    var title: String = "",
    @SerializedName("field")
    var field: String = "",
    @SerializedName("value")
    var value: String = "",
    @EditProfileOptionType
    @SerializedName("type")
    var type: String = EditProfileOptionType.TYPE_INPUT,
    @SerializedName("options")
    var options: List<SelectItem> = emptyList(),
) {

    @Keep
    data class SelectItem(
        @SerializedName("title")
        var title: String = "",
        @SerializedName("value")
        var value: String = "",
        @SerializedName("isSelected")
        var isSelected: Boolean = false,
    )
}
