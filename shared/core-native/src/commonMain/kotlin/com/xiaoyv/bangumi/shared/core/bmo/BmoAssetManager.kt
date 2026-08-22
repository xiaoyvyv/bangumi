package com.xiaoyv.bangumi.shared.core.bmo

/**
 * 负责加载、解码并合成 BMO 表情资源。
 *
 * 资源读取通过回调注入，使核心 BMO 模块不依赖具体的资源系统。清单内容在首次加载后缓存，
 * 图层资源则根据解码结果按需加载。
 *
 * @param loadManifest 加载 BMO 清单 JSON 的回调。
 * @param loadAsset 根据清单中的相对路径加载图层字节的回调。
 */
class BmoAssetManager(
    private val loadManifest: suspend () -> String,
    private val loadAsset: suspend (String) -> ByteArray?,
) {
    private var manifestJson: String? = null

    /**
     * 根据 BMO 编码生成合成后的 PNG 数据。
     *
     * @param code BMO 表情编码。
     * @param width 输出画布宽度。
     * @param height 输出画布高度。
     * @return 合成后的 PNG 字节；编码无效或没有可用图层时返回 `null`。
     */
    suspend fun getOrGenerateCompositeImage(
        code: String,
        width: Int,
        height: Int,
    ): ByteArray? {
        val decodeResult = BmoDecoder.decode(code, manifestJson = getManifestJson())
        if (decodeResult.items.isEmpty()) return null

        val layers = buildList {
            for (item in decodeResult.items) {
                val bytes = loadAsset(item.src) ?: continue
                add(item to bytes)
            }
        }
        if (layers.isEmpty()) return null

        return BmoImageCompositor.composite(layers, width, height)
    }

    private suspend fun getManifestJson(): String {
        manifestJson?.let { return it }
        return loadManifest().also { manifestJson = it }
    }
}
