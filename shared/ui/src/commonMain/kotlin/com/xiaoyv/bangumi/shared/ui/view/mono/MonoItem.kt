package com.xiaoyv.bangumi.shared.ui.view.mono

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay

/**
 * Displays a character or person in the standard full-width list layout.
 *
 * @param item The character or person display data.
 * @param modifier The modifier applied to the item.
 * @param showDivider Whether to draw the standard separator below the item.
 * @param onClick Handles navigation to the selected character or person.
 */
@Composable
fun MonoLineItem(
    item: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    when (item.type) {
        MonoType.CHARACTER -> MonoLineItemCharacter(item, modifier, showDivider, onClick)
        MonoType.PERSON -> MonoLineItemPerson(item, modifier, showDivider, onClick)
    }
}

/**
 * Displays a character or person in the compact card layout.
 *
 * @param item The character or person display data.
 * @param modifier The modifier applied to the card.
 * @param onClick Handles navigation to the selected character or person.
 */
@Composable
fun MonoCardItem(
    item: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    when (item.type) {
        MonoType.CHARACTER -> MonoCardItemCharacter(item, modifier, onClick)
        MonoType.PERSON -> MonoCardItemPerson(item, modifier, onClick)
    }
}
