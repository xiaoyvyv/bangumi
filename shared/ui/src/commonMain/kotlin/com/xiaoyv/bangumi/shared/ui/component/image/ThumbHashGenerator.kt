package com.xiaoyv.bangumi.shared.ui.component.image

import com.xiaoyv.bangumi.shared.core.utils.KotlinThumbHash
import kotlin.io.encoding.Base64
import kotlin.math.roundToInt
import kotlin.random.Random

object ThumbHashGenerator {

    /**
     * 根据唯一的 Key 生成一个确定性的 ThumbHash (Base64 String)
     */
    fun generate(key: String): String {
        // 1. 使用 Key 的哈希值作为随机数种子，保证相同 Key 生成相同结果
        val seed = key.hashCode()
        val rng = Random(seed)

        // 2. 定义微缩图尺寸 (ThumbHash 只需要极小的尺寸，3x4 或 4x4 足够)
        // 稍大的比例 (如 4x3) 可以模拟风景照，(3x4) 模拟人像
        val width = 4
        val height = 4
        val rgba = ByteArray(width * height * 4)

        // 3. 生成两个随机的主色调 (Color A 和 Color B)
        // 限制 RGB 范围在 50-200 之间，避免生成太黑或太白的颜色
        val r1 = rng.nextInt(50, 200)
        val g1 = rng.nextInt(50, 200)
        val b1 = rng.nextInt(50, 200)

        val r2 = rng.nextInt(50, 200)
        val g2 = rng.nextInt(50, 200)
        val b2 = rng.nextInt(50, 200)

        // 4. 填充像素数据 (构建一个简单的线性渐变)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = (y * width + x) * 4

                // 计算渐变比例 (从左上到右下)
                val ratio = (x.toFloat() / width + y.toFloat() / height) / 2f

                // 线性插值混合颜色
                val r = (r1 + (r2 - r1) * ratio).roundToInt()
                val g = (g1 + (g2 - g1) * ratio).roundToInt()
                val b = (b1 + (b2 - b1) * ratio).roundToInt()

                rgba[index] = r.toByte()     // R
                rgba[index + 1] = g.toByte() // G
                rgba[index + 2] = b.toByte() // B
                rgba[index + 3] = 255.toByte() // Alpha (不透明)
            }
        }

        // 5. 调用 ThumbHash 库进行编码
        // 假设你使用的库有 rgbaToThumbHash 方法
        val hashBytes = KotlinThumbHash.rgbaToThumbHash(width, height, rgba)

        // 6. 转 Base64
        return Base64.Mime.encode(hashBytes)
    }
}