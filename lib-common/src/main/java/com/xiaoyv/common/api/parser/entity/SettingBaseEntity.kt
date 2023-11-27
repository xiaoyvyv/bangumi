package com.xiaoyv.common.api.parser.entity

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.xiaoyv.common.config.annotation.FormInputType
import java.io.File

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
    @FormInputType
    @SerializedName("type")
    var type: String = FormInputType.TYPE_INPUT,
    @SerializedName("options")
    var options: List<SelectItem> = emptyList()
) {

    /**
     * 设置新值
     */
    fun setNewValue(newItem: SettingBaseEntity) {
        title = newItem.title
        field = newItem.field
        value = newItem.value
        options = newItem.options
    }

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
