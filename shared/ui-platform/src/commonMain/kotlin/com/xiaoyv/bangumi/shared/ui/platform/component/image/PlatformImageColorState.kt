package com.xiaoyv.bangumi.shared.ui.platform.component.image

/**
 * 从 Coil3 Image 中计算平均亮度（0.0 ~ 1.0）。
 *
 * 平台实现需要从 coil3.Image 中提取像素数据进行计算。
 *
 * @param image 需要计算亮度的 Coil 图片。
 */
expect fun computeAverageLuminance(image: coil3.Image): Float
