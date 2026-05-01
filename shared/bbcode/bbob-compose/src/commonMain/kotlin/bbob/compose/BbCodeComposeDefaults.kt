package bbob.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

/**
 * `bbcode-compose` 默认样式与默认渲染器集合。
 */
object BbCodeComposeDefaults {
    const val UrlAnnotationTag: String = "bbcode:url"
    const val SpoilerAnnotationTag: String = "bbcode:spoiler"
    const val MaskAnnotationTag: String = "bbcode:mask"

    val LinkColor: Color = Color(0xFF0645AD)
    val QuoteBorderColor: Color = Color(0xFFDDDDDD)
    val CodeBackgroundColor: Color = Color(0xFFF5F5F5)
    val DividerColor: Color = Color(0xFFE0E0E0)

    /**
     * 默认图片渲染器。
     *
     * 当前实现使用占位文本和背景块表示图片位置，便于在未接入真实图片加载器时预览结构。
     *
     * @param element 图片元素。
     * @param modifier 外层修饰符。
     * @param textStyle 占位文本样式。
     */
    @Composable
    fun DefaultImageRenderer(
        element: BbCodeImageElement,
        modifier: Modifier = Modifier,
        textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    ) {
        Box(modifier) {
            Box(
                Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .matchParentSize()
            )
            BasicText(
                text = "[image: ${element.url}]",
                style = textStyle,
            )
        }
    }

    /**
     * 默认引用容器。
     *
     * @param modifier 外层修饰符。
     * @param content 容器内容。
     */
    @Composable
    fun DefaultQuoteContainer(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        Box(
            modifier = modifier
                .border(1.dp, QuoteBorderColor)
                .padding(12.dp),
        ) {
            content()
        }
    }

    /**
     * 默认代码块容器。
     *
     * @param text 代码文本。
     * @param modifier 外层修饰符。
     * @param textStyle 代码文本样式。
     */
    @Composable
    fun DefaultCodeContainer(
        text: String,
        modifier: Modifier = Modifier,
        textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    ) {
        Box(
            modifier = modifier
                .border(1.dp, QuoteBorderColor)
                .background(CodeBackgroundColor)
                .padding(12.dp),
        ) {
            BasicText(text = text, style = textStyle)
        }
    }

    /**
     * 默认剧透/遮罩容器。
     *
     * @param modifier 外层修饰符。
     * @param content 容器内容。
     */
    @Composable
    fun DefaultSpoilerContainer(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        Box(modifier = modifier) {
            content()
            var showMask by remember { mutableStateOf(true) }

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(if (showMask) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable(indication = null, interactionSource = null, onClick = { showMask = !showMask }),
            )
        }
    }

    /**
     * 默认水平分隔线。
     *
     * @param modifier 外层修饰符。
     */
    @Composable
    fun DefaultDivider(
        modifier: Modifier = Modifier,
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(DividerColor)
                .height(1.dp),
        )
    }

    /**
     * 基于给定文本样式生成等宽预格式化样式。
     *
     * @param base 基础文本样式。
     * @return 替换为等宽字体后的文本样式。
     */
    fun preformattedTextStyle(base: TextStyle): TextStyle =
        base.copy(fontFamily = FontFamily.Monospace)
}
