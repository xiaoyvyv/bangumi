package com.xiaoyv.common.api.parser.entity

import android.os.Parcelable
import androidx.annotation.Keep
import com.xiaoyv.common.helper.callback.IdEntity
import kotlinx.parcelize.Parcelize

/**
 * Class: [MagiQuestionEntity]
 *
 * @author why
 * @since 12/11/23
 */
@Keep
@Parcelize
data class MagiQuestionEntity(
    override var id: String = "",
    var title: String = "",
    var userId: String = "",
    var userName: String = "",
    var userAvatar: String = "",
    var forms: Map<String, String> = emptyMap(),
    var options: List<Option> = emptyList(),
    var lastQuestionId: String = "",
    var lastQuestionTitle: String = "",
    var lastQuestionRightRate: String = "",
    var lastQuestionRight: Boolean = false,
    var lastQuestionCount: String = "",
    var lastQuestionOptions: List<Option> = emptyList(),
    var syncRate: String = "",
    var syncCount: String = "",
) : IdEntity, Parcelable {


    @Keep
    @Parcelize
    data class Option(
        override var id: String = "",
        var field: String = "",
        var label: String = "",
        var error: Boolean = false,
        var right: Boolean = false
    ) : IdEntity, Parcelable
}
