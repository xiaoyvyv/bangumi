package com.xiaoyv.bangumi.shared.data.model.response.bgm

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [ComposeMonoCollab]
 *
 * 人物/角色的合作者信息，从网页 HTML 的 ul.coversSmall 解析
 *
 * @since 2025/7/27
 */
@Immutable
@Serializable
data class ComposeMonoCollab(
    @SerialName("id") val id: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("images") val images: ComposeImages = ComposeImages.Empty,
    @SerialName("count") val count: String = "",
) {
    companion object {
        val Empty = ComposeMonoCollab()
    }
}
