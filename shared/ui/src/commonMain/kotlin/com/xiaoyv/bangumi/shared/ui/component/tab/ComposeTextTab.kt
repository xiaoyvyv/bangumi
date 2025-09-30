package com.xiaoyv.bangumi.shared.ui.component.tab

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.xiaoyv.bangumi.shared.core.types.ButtonType
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * [ComposeTextTab]
 *
 * @author why
 * @since 2025/1/13
 */
@Immutable
data class ComposeTextTab<T>(
    val type: T,
    val label: StringResource? = null,
    val labelText: String = "",
    val contentColor: Color = Color.Unspecified,
) {
    @Composable
    fun displayText(): String {
        return if (label != null) stringResource(label) else labelText
    }
}

fun ButtonType.toTextTab(colorScheme: ColorScheme): ComposeTextTab<ButtonType> {
    return ComposeTextTab(
        type = this,
        label = label,
        labelText = "",
        contentColor = contentColor(colorScheme)
    )
}

@Composable
fun rememberButtonTypeMenu(key: Any? = null, builderAction: MutableList<ButtonType>.() -> Unit): SerializeList<ComposeTextTab<ButtonType>> {
    val colorScheme = MaterialTheme.colorScheme
    return remember(colorScheme, builderAction, key) {
        buildList(builderAction)
            .map { it.toTextTab(colorScheme) }
            .toPersistentList()
    }
}