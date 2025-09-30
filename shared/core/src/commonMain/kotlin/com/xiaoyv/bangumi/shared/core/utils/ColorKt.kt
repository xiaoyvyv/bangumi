@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.shared.core.utils

import androidx.compose.ui.graphics.Color

private val cssColorMap = mapOf(
    "aliceblue" to Color(0xFFF0F8FF),
    "antiquewhite" to Color(0xFFFAEBD7),
    "aqua" to Color.Cyan,
    "aquamarine" to Color(0xFF7FFFD4),
    "azure" to Color(0xFFF0FFFF),
    "beige" to Color(0xFFF5F5DC),
    "bisque" to Color(0xFFFFE4C4),
    "black" to Color.Black,
    "blanchedalmond" to Color(0xFFFFEBCD),
    "blue" to Color.Blue,
    "blueviolet" to Color(0xFF8A2BE2),
    "brown" to Color(0xFFA52A2A),
    "burlywood" to Color(0xFFDEB887),
    "cadetblue" to Color(0xFF5F9EA0),
    "chartreuse" to Color(0xFF7FFF00),
    "chocolate" to Color(0xFFD2691E),
    "coral" to Color(0xFFFF7F50),
    "cornflowerblue" to Color(0xFF6495ED),
    "cornsilk" to Color(0xFFFFF8DC),
    "crimson" to Color(0xFFDC143C),
    "cyan" to Color.Cyan,
    "darkblue" to Color(0xFF00008B),
    "darkcyan" to Color(0xFF008B8B),
    "darkgoldenrod" to Color(0xFFB8860B),
    "darkgray" to Color(0xFFA9A9A9),
    "darkgreen" to Color(0xFF006400),
    "darkgrey" to Color(0xFFA9A9A9),
    "darkkhaki" to Color(0xFFBDB76B),
    "darkmagenta" to Color(0xFF8B008B),
    "darkolivegreen" to Color(0xFF556B2F),
    "darkorange" to Color(0xFFFF8C00),
    "darkorchid" to Color(0xFF9932CC),
    "darkred" to Color(0xFF8B0000),
    "darksalmon" to Color(0xFFE9967A),
    "darkseagreen" to Color(0xFF8FBC8F),
    "darkslateblue" to Color(0xFF483D8B),
    "darkslategray" to Color(0xFF2F4F4F),
    "darkslategrey" to Color(0xFF2F4F4F),
    "darkturquoise" to Color(0xFF00CED1),
    "darkviolet" to Color(0xFF9400D3),
    "deeppink" to Color(0xFFFF1493),
    "deepskyblue" to Color(0xFF00BFFF),
    "dimgray" to Color(0xFF696969),
    "dimgrey" to Color(0xFF696969),
    "dodgerblue" to Color(0xFF1E90FF),
    "firebrick" to Color(0xFFB22222),
    "floralwhite" to Color(0xFFFFFAF0),
    "forestgreen" to Color(0xFF228B22),
    "fuchsia" to Color.Magenta,
    "gainsboro" to Color(0xFFDCDCDC),
    "ghostwhite" to Color(0xFFF8F8FF),
    "gold" to Color(0xFFFFD700),
    "goldenrod" to Color(0xFFDAA520),
    "gray" to Color.Gray,
    "green" to Color.Green,
    "greenyellow" to Color(0xFFADFF2F),
    "grey" to Color.Gray,
    "honeydew" to Color(0xFFF0FFF0),
    "hotpink" to Color(0xFFFF69B4),
    "indianred" to Color(0xFFCD5C5C),
    "indigo" to Color(0xFF4B0082),
    "ivory" to Color(0xFFFFFFF0),
    "khaki" to Color(0xFFF0E68C),
    "lavender" to Color(0xFFE6E6FA),
    "lavenderblush" to Color(0xFFFFF0F5),
    "lawngreen" to Color(0xFF7CFC00),
    "lemonchiffon" to Color(0xFFFFFACD),
    "lightblue" to Color(0xFFADD8E6),
    "lightcoral" to Color(0xFFF08080),
    "lightcyan" to Color(0xFFE0FFFF),
    "lightgoldenrodyellow" to Color(0xFFFAFAD2),
    "lightgray" to Color(0xFFD3D3D3),
    "lightgreen" to Color(0xFF90EE90),
    "lightgrey" to Color(0xFFD3D3D3),
    "lightpink" to Color(0xFFFFB6C1),
    "lightsalmon" to Color(0xFFFFA07A),
    "lightseagreen" to Color(0xFF20B2AA),
    "lightskyblue" to Color(0xFF87CEFA),
    "lightslategray" to Color(0xFF778899),
    "lightslategrey" to Color(0xFF778899),
    "lightsteelblue" to Color(0xFFB0C4DE),
    "lightyellow" to Color(0xFFFFFFE0),
    "lime" to Color(0xFF00FF00),
    "limegreen" to Color(0xFF32CD32),
    "linen" to Color(0xFFFAF0E6),
    "magenta" to Color.Magenta,
    "maroon" to Color(0xFF800000),
    "mediumaquamarine" to Color(0xFF66CDAA),
    "mediumblue" to Color(0xFF0000CD),
    "mediumorchid" to Color(0xFFBA55D3),
    "mediumpurple" to Color(0xFF9370DB),
    "mediumseagreen" to Color(0xFF3CB371),
    "mediumslateblue" to Color(0xFF7B68EE),
    "mediumspringgreen" to Color(0xFF00FA9A),
    "mediumturquoise" to Color(0xFF48D1CC),
    "mediumvioletred" to Color(0xFFC71585),
    "midnightblue" to Color(0xFF191970),
    "mintcream" to Color(0xFFF5FFFA),
    "mistyrose" to Color(0xFFFFE4E1),
    "moccasin" to Color(0xFFFFE4B5),
    "navajowhite" to Color(0xFFFFDEAD),
    "navy" to Color(0xFF000080),
    "oldlace" to Color(0xFFFDF5E6),
    "olive" to Color(0xFF808000),
    "olivedrab" to Color(0xFF6B8E23),
    "orange" to Color(0xFFFFA500),
    "orangered" to Color(0xFFFF4500),
    "orchid" to Color(0xFFDA70D6),
    "palegoldenrod" to Color(0xFFEEE8AA),
    "palegreen" to Color(0xFF98FB98),
    "paleturquoise" to Color(0xFFAFEEEE),
    "palevioletred" to Color(0xFFDB7093),
    "papayawhip" to Color(0xFFFFEFD5),
    "peachpuff" to Color(0xFFFFDAB9),
    "peru" to Color(0xFFCD853F),
    "pink" to Color(0xFFFFC0CB),
    "plum" to Color(0xFFDDA0DD),
    "powderblue" to Color(0xFFB0E0E6),
    "purple" to Color(0xFF800080),
    "rebeccapurple" to Color(0xFF663399),
    "red" to Color.Red,
    "rosybrown" to Color(0xFFBC8F8F),
    "royalblue" to Color(0xFF4169E1),
    "saddlebrown" to Color(0xFF8B4513),
    "salmon" to Color(0xFFFA8072),
    "sandybrown" to Color(0xFFF4A460),
    "seagreen" to Color(0xFF2E8B57),
    "seashell" to Color(0xFFFFF5EE),
    "sienna" to Color(0xFFA0522D),
    "silver" to Color(0xFFC0C0C0),
    "skyblue" to Color(0xFF87CEEB),
    "slateblue" to Color(0xFF6A5ACD),
    "slategray" to Color(0xFF708090),
    "slategrey" to Color(0xFF708090),
    "snow" to Color(0xFFFFFAFA),
    "springgreen" to Color(0xFF00FF7F),
    "steelblue" to Color(0xFF4682B4),
    "tan" to Color(0xFFD2B48C),
    "teal" to Color(0xFF008080),
    "thistle" to Color(0xFFD8BFD8),
    "tomato" to Color(0xFFFF6347),
    "turquoise" to Color(0xFF40E0D0),
    "violet" to Color(0xFFEE82EE),
    "wheat" to Color(0xFFF5DEB3),
    "white" to Color.White,
    "whitesmoke" to Color(0xFFF5F5F5),
    "yellow" to Color.Yellow,
    "yellowgreen" to Color(0xFF9ACD32)
)

/**
 * - #RGB	      简写 RGB
 * - #RGBA	  简写 RGBA
 * - #RRGGBB	  标准 RGB
 * - #RRGGBBAA  标准 RGBA，alpha 在最后 ✅
 */
fun parseHtmlHexColor(colorString: String): Color? {
    if (!colorString.contains("#")) {
        return cssColorMap[colorString.lowercase().trim()]
    }

    val hex = colorString.removePrefix("#")

    return try {
        when (hex.length) {
            3 -> {
                // #rgb
                val r = "${hex[0]}${hex[0]}".toInt(16)
                val g = "${hex[1]}${hex[1]}".toInt(16)
                val b = "${hex[2]}${hex[2]}".toInt(16)
                Color(r / 255f, g / 255f, b / 255f, 1f)
            }

            4 -> {
                // #rgba
                val r = "${hex[0]}${hex[0]}".toInt(16)
                val g = "${hex[1]}${hex[1]}".toInt(16)
                val b = "${hex[2]}${hex[2]}".toInt(16)
                val a = "${hex[3]}${hex[3]}".toInt(16)
                Color(r / 255f, g / 255f, b / 255f, a / 255f)
            }

            6 -> {
                // #rrggbb
                val r = hex.substring(0, 2).toInt(16)
                val g = hex.substring(2, 4).toInt(16)
                val b = hex.substring(4, 6).toInt(16)
                Color(r / 255f, g / 255f, b / 255f, 1f)
            }

            8 -> {
                // #rrggbbaa (alpha at the end)
                val r = hex.substring(0, 2).toInt(16)
                val g = hex.substring(2, 4).toInt(16)
                val b = hex.substring(4, 6).toInt(16)
                val a = hex.substring(6, 8).toInt(16)
                Color(r / 255f, g / 255f, b / 255f, a / 255f)
            }

            else -> null
        }
    } catch (_: Exception) {
        println("Invalid hex color: $colorString")
        null
    }
}

