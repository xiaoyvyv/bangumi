@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.subtitle.api.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import com.xiaoyv.subtitle.api.parser.exception.InvalidColorCode
import java.util.Locale

object ColorUtils {
    /**
     * Convert the hexadecimal color code to BGR code
     *
     * @param hex hex
     */
    fun hexToBGR(hex: String): Int {
        return Color.parseColor(hex)
    }

    /**
     * Convert android color to &HAABBGGRR
     *
     * @param colorInt : the color code
     */
    @JvmStatic
    fun colorToHAABBGGRR(@ColorInt colorInt: Int): String {
        // 获取颜色值的 ARGB 分量
        val alpha = Color.alpha(colorInt)
        val red = Color.red(colorInt)
        val green = Color.green(colorInt)
        val blue = Color.blue(colorInt)

        // 将 ARGB 分量组合成 &HAABBGGRR 格式的字符串
        return String.format("&H%02X%02X%02X%02X", alpha, blue, green, red)
    }

    /**
     * Convert a &HAABBGGRR to hexadecimal
     *
     * @param haabbggrr: the color code
     * @return the hexadecimal code
     * @throws InvalidColorCode
     */
    fun HAABBGGRRToHex(haabbggrr: String): String {
        if (haabbggrr.length != 10) {
            throw InvalidColorCode("Invalid pattern, must be &HAABBGGRR")
        }
        val sb = StringBuilder()
        sb.append("#")
        sb.append(haabbggrr.substring(2, 4))
        sb.append(haabbggrr.substring(8))
        sb.append(haabbggrr.substring(6, 8))
        sb.append(haabbggrr.substring(4, 6))
        return sb.toString().lowercase(Locale.getDefault())
    }

    /**
     * Convert a &HBBGGRR to hexadecimal
     *
     * @param hbbggrr: the color code
     * @return the hexadecimal code
     * @throws InvalidColorCode
     */
    fun HBBGGRRToHex(hbbggrr: String): String {
        if (hbbggrr.length != 8) {
            throw InvalidColorCode("Invalid pattern, must be &HBBGGRR")
        }
        val sb = StringBuilder()
        sb.append("#")
        sb.append(hbbggrr.substring(6))
        sb.append(hbbggrr.substring(4, 6))
        sb.append(hbbggrr.substring(2, 4))
        return sb.toString().lowercase(Locale.getDefault())
    }

    /**
     * Convert a &HAABBGGRR to BGR
     *
     * @param haabbggrr: the color code
     * @return the BGR code
     * @throws InvalidColorCode
     */
    @JvmStatic
    fun HAABBGGRRToBGR(haabbggrr: String): Int {
        return hexToBGR(HAABBGGRRToHex(haabbggrr))
    }

    /**
     * Convert a &HBBGGRR to BGR
     *
     * @param hbbggrr: the color code
     * @return the BGR code
     * @throws InvalidColorCode
     */
    @JvmStatic
    fun HBBGGRRToBGR(hbbggrr: String): Int {
        return hexToBGR(HBBGGRRToHex(hbbggrr))
    }
}
