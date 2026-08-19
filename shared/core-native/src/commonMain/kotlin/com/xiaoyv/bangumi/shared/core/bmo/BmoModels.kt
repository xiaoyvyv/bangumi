package com.xiaoyv.bangumi.shared.core.bmo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 清单中的图层项
 */
@Serializable
data class BmoManifestItem(
    @SerialName("id") val id: String,
    @SerialName("alias") val alias: String? = null,
    @SerialName("src") val src: String,
    @SerialName("layer") val layer: Int? = null,
    @SerialName("order") val order: Int? = null,
    @SerialName("version") val version: Int? = null,
    @SerialName("custom") val custom: Boolean? = null
)

/**
 * 清单中的分类
 */
@Serializable
data class BmoCategory(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("layer") val layer: Int = 0,
    @SerialName("multiSelect") val multiSelect: Boolean? = null,
    @SerialName("maxSelect") val maxSelect: Int? = null,
    @SerialName("items") val items: List<BmoManifestItem> = emptyList()
)

/**
 * 图层修饰参数（色彩、变形、翻转、偏移等）
 */
data class BmoModifiers(
    val hue: Float? = null,
    val lightness: Float? = null,
    val saturation: Float? = null,
    val flipH: Boolean = false,
    val flipV: Boolean = false,
    val rotation: Float = 0f,
    val x: Float = 0f,
    val y: Float = 0f,
    val scale: Float = 1f,
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rawModifiers: Map<String, Any?> = emptyMap()
)

/**
 * 解码后的单个 PNG 叠加图层对象
 */
data class BmoResolvedItem(
    val id: String,
    val codeId: String,
    val src: String,
    val layer: Int,
    val order: Int,
    val category: String,
    val categoryId: String,
    val modifiers: BmoModifiers,
    val rawItem: BmoManifestItem? = null
)

/**
 * BMO 代码解码总结果
 * @property rawCode 原始代码
 * @property items 按图层渲染顺序（从底层到顶层）排好序的叠加资源列表
 * @property unknown 未识别代码段
 * @property options 全局配置项（如 scale 缩放倍率）
 */
data class BmoDecodeResult(
    val rawCode: String,
    val items: List<BmoResolvedItem>,
    val unknown: List<String> = emptyList(),
    val options: Map<String, Any?> = emptyMap()
)
