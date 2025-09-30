package com.xiaoyv.bangumi.shared.ui.view.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue
import com.xiaoyv.bangumi.shared.core.utils.serialization.SerializeList
import com.xiaoyv.bangumi.shared.ui.component.space.LayoutPaddingHalf
import com.xiaoyv.bangumi.shared.ui.component.text.HighlightedText
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun SubjectAdvanceFilterTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    title: String,
    hint: String,
    description: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceBright,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceBright
    )
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LayoutPaddingHalf)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFocusChanged(it.isFocused) },
            value = value,
            onValueChange = onValueChange,
            colors = textFieldColors,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            label = { Text(text = title) },
            placeholder = { Text(text = hint) }
        )

        HighlightedText(
            modifier = Modifier.padding(bottom = LayoutPaddingHalf),
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            highlights = remember { persistentListOf("且", "YYYY-MM-DD", "示例") }
        )
    }
}

fun String.splitValue(): SerializeList<String>? {
    return split("\\s+".toRegex())
        .filter { it.isNotBlank() }
        .toPersistentList()
        .takeIf { it.isNotEmpty() }
}

