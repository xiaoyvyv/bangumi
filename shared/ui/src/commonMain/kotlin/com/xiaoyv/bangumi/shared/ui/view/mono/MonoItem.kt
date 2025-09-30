package com.xiaoyv.bangumi.shared.ui.view.mono

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xiaoyv.bangumi.shared.core.types.MonoType
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeMonoDisplay

/**
 * [MonoLineItem]
 */
@Composable
fun MonoLineItem(
    item: ComposeMonoDisplay,
    modifier: Modifier = Modifier,
    onClick: (Long, Int) -> Unit = { _, _ -> },
) {
    when (item.type) {
        MonoType.CHARACTER -> MonoLineItemCharacter(item, modifier, onClick)
        MonoType.PERSON -> MonoLineItemPerson(item, modifier, onClick)
    }
}

/**
 * [MonoCardItem]
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


