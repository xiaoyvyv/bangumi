package com.xiaoyv.bangumi.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xiaoyv.bangumi.features.settings.business.SplashState
import com.xiaoyv.bangumi.features.settings.business.SplashViewModel
import com.xiaoyv.bangumi.shared.ui.component.navigation.Screen
import org.orbitmvi.orbit.compose.collectAsState


@Composable
fun SplashRoute(
    viewModel: SplashViewModel,
    onNavScreen: (Screen) -> Unit,
) {
    val baseState by viewModel.collectAsState()

    SideEffect(Unit) {
        onNavScreen(Screen.DnsResolver)
    }

    SplashScreen(
        state = baseState.data,
    )
}

@Composable
fun SplashScreen(state: SplashState) {

}

@Composable
fun PureInlineDynamicHeightWorkingDemo() {
    val inlineId = "custom_badge"
    val badgeHeight = 80.sp // 明显高于普通行的 44sp 高度

    val annotatedText = buildAnnotatedString {
        append("这是第一行普通文字，行高保持默认紧凑。\n")
        append("这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联")

        // 1. 占位符必须附带相同高度的 SpanStyle
        appendInlineContent(id = inlineId, alternateText = "[组件]")

        append(" 第二行被真这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件这是第二行，包含大尺寸内联组件正撑高。\n")
        append("这是第三行普通文字，行高依然正常。")
    }

    val inlineContent = mapOf(
        inlineId to InlineTextContent(
            placeholder = Placeholder(
                width = 60.sp,
                height = badgeHeight,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFFECEF))
                    .border(1.5.dp, Color(0xFFFF4D6D), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF4D6D))
                    )
                    Text(
                        text = "BGM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF4D6D)
                    )
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = annotatedText,
            inlineContent = inlineContent,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = TextUnit.Unspecified,
                color = Color(0xFF24292F)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFFD0D7DE), RoundedCornerShape(8.dp))
                .padding(14.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun PureInlineDynamicHeightWorkingDemoPreview() {
    PureInlineDynamicHeightWorkingDemo()
}