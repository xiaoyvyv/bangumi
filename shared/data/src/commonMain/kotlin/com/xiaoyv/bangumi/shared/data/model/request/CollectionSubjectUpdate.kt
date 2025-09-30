package com.xiaoyv.bangumi.shared.data.model.request

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import com.xiaoyv.bangumi.shared.core.types.CollectionType
import com.xiaoyv.bangumi.shared.core.utils.defaultJson
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 所有的字段均可选
 *
 * @param type 修改条目收藏类型
 * @param rate 评分，`0` 表示删除评分
 * @param epStatus 只能用于修改书籍条目进度
 * @param volStatus 只能用于修改书籍条目进度
 * @param comment 评价
 * @param private 仅自己可见
 * @param tags 不传或者 `null` 都会被忽略，传 `[]` 则会删除所有 tag。
 */
@Serializable
data class CollectionSubjectUpdate(

    /* 修改条目收藏类型 */
    @SerialName(value = "type") @field:CollectionType val type: Int? = null,

    /* 评分，`0` 表示删除评分 */
    @SerialName(value = "rate") val rate: Int? = null,

    /* 只能用于修改书籍条目进度 */
    @SerialName(value = "ep_status") val epStatus: Int? = null,

    /* 只能用于修改书籍条目进度 */
    @SerialName(value = "vol_status") val volStatus: Int? = null,

    /* 评价 */
    @SerialName(value = "comment") val comment: String? = null,

    /* 仅自己可见 */
    @SerialName(value = "private") val `private`: Boolean? = null,

    /* 不传或者 `null` 都会被忽略，传 `[]` 则会删除所有 tag。 */
    @SerialName(value = "tags") val tags: SerializeList<String>? = null,
)


val CollectionSubjectUpdateSaver = Saver<CollectionSubjectUpdate, String>(
    save = { defaultJson.encodeToString(it) },
    restore = { defaultJson.decodeFromString(it) }
)

@Composable
fun rememberCollectionSubjectUpdate(
    initial: CollectionSubjectUpdate = CollectionSubjectUpdate(),
): MutableState<CollectionSubjectUpdate> {
    return rememberSaveable(stateSaver = CollectionSubjectUpdateSaver) {
        mutableStateOf(initial)
    }
}