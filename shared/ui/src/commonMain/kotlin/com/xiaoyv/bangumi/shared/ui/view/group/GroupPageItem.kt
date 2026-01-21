package com.xiaoyv.bangumi.shared.ui.view.group

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeGroup
import com.xiaoyv.bangumi.shared.ui.component.image.StateImage

@Composable
fun GroupPageItem(
    item: ComposeGroup,
    onClick: (ComposeGroup) -> Unit = {},
) {
    ListItem(
        modifier = Modifier
            .clickable { onClick(item) }
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        leadingContent = {
            StateImage(
                modifier = Modifier.size(44.dp),
                model = item.images.displayLargeImage,
                shape = MaterialTheme.shapes.small,
            )
        },
        headlineContent = {
            Text(
                text = item.title,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(text = "${item.members} 位成员")
        }
    )
}