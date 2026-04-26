package com.xiaoyv.bangumi

import com.xiaoyv.bangumi.shared.data.parser.bbcode.BBCodeHandler
import com.xiaoyv.bangumi.shared.data.parser.bbcode.BBCodeParser
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        val t =
            "想回一回血玩一下早濑在SMEE的最后一部作品。\r\n" +
                    "早濑至少不会翻车吧。之前的那部こいとれ把我折磨死了。\r\n" +
                    "[url]https://bangumi.tv/subject/353905[/url]\r\n" +
                    "[url=https://bangumi.tv/subject/353905]text[/url]\r\n" +
                    "[url='https://bangumi.tv/subject/353905']text[/url]\r\n" +
                    "[img]https://lain.bgm.tv/r/400/pic/cover/l/a0/7f/353905_hEh4r.jpg[/img]\r\n" +
                    "[img=https://lain.bgm.tv/r/400/pic/cover/l/a0/7f/353905_hEh4r.jpg][/img]\r\n" +
                    "[img='https://lain.bgm.tv/r/400/pic/cover/l/a0/7f/353905_hEh4r.jpg'][/img]\r\n" +
                    "[url]https://bangumi.tv/subject/408922[/url]\r\n" +
                    "[img]https://lain.bgm.tv/r/400/pic/cover/l/68/7f/408922_H181U.jpg[/img]\n"

        val input = t +
                "[b][color=#ff0000 size=18]Hello[/color][/b] \\[escaped\\]\n" +
                "文字内容 \t[b][color=#ff0000 size=18][b]BBBBBBBB[/b][/color][/b]\n" +
                "正常文字 \t\t\n" +
                "正常文字\n" +
                "我是 [b]粗体字[/b] \tCtrl+B \t\n" +
                "我是粗体字\n" +
                "我是[i]斜体字[/i] \tCtrl+I \t\n" +
                "我是斜体字\n" +
                "我是[u]下划线文字[/u] \tCtrl+U \t\n" +
                "我是下划线文字\n" +
                "我是[s]删除线文字[/s] \tCtrl+D \t\n" +
                "我是删除线文字\n" +
                "我是[mask]马赛克文字[/mask] \tCtrl+M \t\n" +
                "我是马赛克文字\n" +
                "我是\n" +
                "[color=red]彩[/color][color=green]色[/color][color=blue]的[/color][color=orange]哟[/color]。 \t\t\n" +
                "我是\n" +
                "彩色的哟。\n" +
                "[size=10]不同[/size][size=14]大小的[/size][size=18]文字[/size]效果也可实现。 \t\t\n" +
                "不同大小的文字效果也可实现。\n" +
                "Bangumi 番组计划: [url]http://chii.in/[/url] \tCtrl+L \t\n" +
                "Bangumi 番组计划: http://chii.in/\n" +
                "带文字说明的网站链接：\n" +
                "[url=http://chii.in]Bangumi 番组计划[/url] \tCtrl+L \t\n" +
                "带文字说明的网站链接：\n" +
                "Bangumi 番组计划\n" +
                "存放于其他网络服务器的图片：\n" +
                "[img]http://chii.in/img/ico/bgm88-31.gif[/img] \tCtrl+P \t\n" +
                "存放于其他网络服务器的图片：\n" +
                "[h1]BBCode 解析器综合测试[/h1]\n" +
                "\n" +
                "[h2]1. 基础格式与嵌套[/h2]\n" +
                "这是一段普通文本。\n" +
                "这里有[b]粗体文字[/b]，这里是[i]斜体文字[/i]，还有[u]下划线[/u]和[s]删除线[/s]。\n" +
                "[b]嵌套测试：[color=red]粗体中包含红色，[i]以及红色的粗斜体[/i][/color]回到粗体[/b]。\n" +
                "\n" +
                "[h2]2. 属性解析 (复杂场景)[/h2]\n" +
                "[url=https://www.kotlinlang.org]简单的 Kotlin 官网链接[/url]\n" +
                "[size=24]大号字体测试[/size]\n" +
                "[color=#FF5722 size=20]多属性测试：橙色且 20px 大小[/color]\n" +
                "[quote name=\"张三\"]这是一个简单的引用。[/quote]\n" +
                "[quote name=\"李四 (Li Si)\" time=\"2025-01-01\"]这是带引号且属性值包含空格的引用 (测试正则是否健壮)。[/quote]\n" +
                "\n" +
                "[h2]3. 列表与对齐[/h2]\n" +
                "[center]这段文字应该居中显示[/center]\n" +
                "[list]\n" +
                "[*]列表项 1：普通文本\n" +
                "[*]列表项 2：[b]带格式的列表项[/b]\n" +
                "[*]列表项 3\n" +
                "[/list]\n" +
                "\n" +
                "[h2]4. 容错性测试 (关键)[/h2]\n" +
                "[b]这是一个忘记闭合的粗体标签 (应该在文末自动闭合)\n" +
                "[b]错位嵌套测试：[i]粗体加斜体[/b] 只有斜体[/i] (解析器应该处理这种交错)\n" +
                "\n" +
                "[h2]5. 干扰项与转义[/h2]\n" +
                "array[0] = 1 (数组下标不应被解析为标签)\n" +
                "\\[b]这里使用了转义字符，不应显示为粗体\\[/b]\n" +
                "[code]\n" +
                "fun main() {\n" +
                "    println(\"Code 标签内的 [b] 内容不应被解析\")\n" +
                "}\n" +
                "[/code]"

        val content =
            "组件：[url]https://bgm.tv/dev/app/5445[/url]\r\n" +
                    "原贴：[url=https://bgm.tv/group/topic/449305][组件预发布&好友鉴定现场] Your Angle，计算你与班友的评分同步率（动画或书籍）[/url]\r\n" +
                    "之前与喵子的讨论：[url]https://bgm.tv/group/topic/448711#post_3690110[/url]\r\n\r\n" +
                    "在原贴中已经对定义出的 bgm 空间的性质有了大量的解释，本贴重点讨论的是[b]组件中同步率的计算方法[/b]：\r\n" +
                    "1. 标准化“评分标准”\r\n" +
                    "2. 角度同步率和欧氏同步率\r\n" +
                    "3. “好中差”分类\r\n" +
                    "\r\n" +
                    "[b]1. 标准化“评分标准”[/b]\r\n" +
                    "第一步，根据用户（这里是我）的评分分布图[img=217,201]https://lsky.ry.mk/i/2026/01/22/8ff6389684cf0.webp[/img]，能将每一分都转换成整条线段上占其评分数比例的线段，比如7分及其位置：\r\n" +
                    "[img]https://lsky.ry.mk/i/2026/01/22/7d5a572da5ab4.webp[/img]，第一步只是为了看着更直观。\r\n" +
                    "\r\n" +
                    "第二步，规定整条线段的左侧为1，右侧为0，均匀过渡。取这些分线段的中点：[img]https://lsky.ry.mk/i/2026/01/23/e5a6e0be8e59d.webp[/img]，并从右往左依次算出这些点在[0,1]中的位置，这些位置值是原分数的第一次映射，结果：\r\n" +
                    "[img]https://lsky.ry.mk/i/2026/01/23/7a48adb2f3049.webp[/img]\r\n" +
                    "\r\n" +
                    "第三步，让所有值减去0.5（它实际上是每个人收藏中“不喜欢”所占比例，这里就默认为一半，取0.5。但不管这个值取多少，对于用户没看过或不打分的作品，它的标准化分数一定是：0），得到的新值就是用户新的“评分标准”，也就是标准化分数，标准化完成。[img]https://lsky.ry.mk/i/2026/01/23/bb804632b8bce.webp[/img]\r\n" +
                    "\r\n" +
                    "更详细地解释一下“标准化分数”的规则：一旦某条分线段的中点低于整条线段的中点，那么他将打出负分，代表着“不喜欢”；而若这个点高于中点，那么就会打出正分，代表着“喜欢”。\r\n" +
                    "\r\n" +
                    "解释：它的作用参照我在原贴中写的内容：[img]https://lsky.ry.mk/i/2026/01/23/9c741820eabd5.webp[/img]\r\n" +
                    "举例来说，[img]https://lsky.ry.mk/i/2026/01/23/807c5dbf686a3.webp[/img] 我和白葱都给无职2打了7分，但在标准化分数后，这是我们的新打分标准：\r\n" +
                    "[img]https://lsky.ry.mk/i/2026/01/23/1381f7491109e.webp[/img][img]https://lsky.ry.mk/i/2026/01/23/9136fc94ba18c.webp[/img]\r\n" +
                    "原本我们都给一个作品打7分，但现在我打的是0.06分，他打的是0.14分。可能你会发现我们没有差多少，是的，这是因为我们都在一定程度上遵守了“均值在6~7浮动的正态分布（他是6.45，我是6.86）”，实际这也是最自然的概率分布。\r\n\r\n" +
                    "最后需要注意一点：这个标准化会使用“个人空间”下的所有作品，而非“评分空间”——[img]https://lsky.ry.mk/i/2026/01/23/0e4d3c993029f.webp[/img]，其中蓝圈部分是白葱的实际标准化空间，绿圈部分是我的标准化空间。\r\n\r\n" +
                    "[b]2. 角度同步率和欧氏同步率[/b]\r\n" +
                    "    角度同步率使用余弦相似度计算：\r\n" +
                    "参考原贴规定的五个bgm空间所包含的作品，制作出两人各自的向量（每个向量是对应作品的标准化评分），然后应用公式：\r\n" +
                    "[img]https://lsky.ry.mk/i/2026/01/23/fc44f16f7fcd3.webp[/img]\r\n" +
                    "完成对应的数值累加与计算即可得到余弦相似度。再对余弦相似度取 arccos 值，就得到了弧度，弧度可以直接转换成角度（从（-π, π）映射到（-180°, 180°）。\r\n" +
                    "有一点需要注意，本组件对“在看、未打分、未看”都视为标准化分数下的 0，这其实可能并不完全合理。\r\n" +
                    "\r\n    欧氏同步率使用欧几里得距离计算：\r\n" +
                    "公式：[img]https://lsky.ry.mk/i/2026/01/23/a3ab06627b936.webp[/img]\r\n" +
                    "这样得到的是bgm空间中，两人的绝对距离，但需要将其映射到百分数上，这样做：\r\n" +
                    "在累加公式时，顺便计算出当前空间所能取到的最大长度（举例来说就是，对x维的“立方体”，获取它的斜边长度）。将这个斜边长度映射到 -1，而 长度0（两点重合） 映射到1，这样，绝对距离就能映射到（-1, 1）上，再转换成百分数就可以了。\r\n\r\n" +
                    "问题：对距离的比例直接使用了线性插值，但有可能它并不是均匀的？这方面不太懂。网上查到一种算法是 相似度=1/(1+dis)，它保证了 dis=0 时相似度为100%，但dis本身会随着维度增加而单纯地增加，这个公式里的'1'应该是某种随维度变化的长度，但我现在没什么想法。\r\n\r\n" +
                    "[b]3. “好中差”分类[/b]\r\n" +
                    "第一步，定义“好、中、差”作品的标准化分数，在本组件中我定义它们分别是：0.4, 0, -0.3。\r\n\r\n" +
                    "第二步，对每部作品的标准化分数，取其与好中差标准化分数的差的绝对值，绝对值最小的，对应的标准就是作品的类别。\r\n" +
                    "例如：[img]https://lsky.ry.mk/i/2026/01/23/cf31c947bdf5b.webp[/img]图中三个蓝色的点分别代表“好、中、差”的标准分，而《记忆缝线》的标准分位置是黑点，更靠近“好”，所以它是我的好作品。"

        val parser = BBCodeParser(object : BBCodeHandler {

            override fun onOpenTagName(name: String) {
//                println("onOpenTagName: $name")
            }

            override fun onOpenTag(
                name: String,
                attributes: Map<String, String>,
                isImplied: Boolean
            ) {
                println("onOpenTag: $name, $attributes, $isImplied")
            }

            override fun onCloseTag(name: String, isImplied: Boolean) {
                println("onCloseTag: $name, $isImplied")
            }

            override fun onText(text: String) {
                println("onText: \"$text\"")
            }

            override fun onEnd() {
                println("onEnd")
            }

            override fun onError(error: Exception) {
                throw error
            }
        })

        parser.parse(content)
    }
}