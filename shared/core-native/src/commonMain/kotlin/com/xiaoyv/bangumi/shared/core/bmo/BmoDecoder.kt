package com.xiaoyv.bangumi.shared.core.bmo

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

/**
 * BMO 小表情代码解码器 (Kotlin Multiplatform)
 */
object BmoDecoder {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private data class BmoItemMeta(
        val id: String,
        val codeId: String,
        val src: String,
        val layer: Int,
        val order: Int,
        val category: String,
        val categoryId: String,
        val alias: String?,
        val version: Int,
        val categoryMaxSelect: Int?,
        val rawItem: BmoManifestItem
    )

    private class BmoManifestParsed(
        val itemIndex: Map<String, BmoItemMeta>,
        val compactReverse: List<BmoItemMeta>,
        val categoryLimits: Map<String, Int?>
    )

    private val defaultParsedManifest by lazy {
        parseManifestJson(DEFAULT_BMO_MANIFEST_JSON)
    }

    private fun getParsedManifest(manifestJson: String?): BmoManifestParsed {
        if (manifestJson == null || manifestJson == DEFAULT_BMO_MANIFEST_JSON) {
            return defaultParsedManifest
        }
        return parseManifestJson(manifestJson)
    }

    private fun parseManifestJson(manifestJson: String): BmoManifestParsed {
        val categoriesMap = json.decodeFromString<Map<String, BmoCategory>>(manifestJson)

        val itemIndex = mutableMapOf<String, BmoItemMeta>()
        val categoryLimits = mutableMapOf<String, Int?>()
        val versionBuckets = mutableMapOf<Int, MutableList<Pair<BmoItemMeta, String>>>()

        var nextCompactId = 0
        val compactIndex = mutableMapOf<String, Int>()
        val compactReverseList = mutableListOf<BmoItemMeta>()

        for ((categoryKey, category) in categoriesMap) {
            val categoryId = category.id ?: categoryKey
            val categoryLayer = category.layer
            val maxSelectRaw = category.maxSelect
            val multiSelect = category.multiSelect

            val categoryMaxSelect: Int? = when {
                maxSelectRaw != null && maxSelectRaw > 0 -> maxSelectRaw
                maxSelectRaw == 0 -> null
                multiSelect == false -> 1
                multiSelect == true -> null
                else -> 1
            }
            categoryLimits[categoryKey] = categoryMaxSelect

            for ((index, item) in category.items.withIndex()) {
                val itemId = item.id
                val isCustom = item.custom == true
                val isDigits = itemId.all { it.isDigit() }
                val codeId = if (categoryId.isNotEmpty() && isDigits && !isCustom) {
                    categoryId + itemId
                } else {
                    itemId
                }
                val alias = item.alias
                val itemLayer = item.layer ?: categoryLayer
                val order = item.order ?: index
                val version = item.version?.coerceAtLeast(1) ?: 1

                val meta = BmoItemMeta(
                    id = itemId,
                    codeId = codeId,
                    src = item.src,
                    layer = itemLayer,
                    order = order,
                    category = categoryKey,
                    categoryId = categoryId,
                    alias = alias,
                    version = version,
                    categoryMaxSelect = categoryMaxSelect,
                    rawItem = item
                )

                if (!itemIndex.containsKey(itemId)) itemIndex[itemId] = meta
                if (!itemIndex.containsKey(codeId)) itemIndex[codeId] = meta
                if (!alias.isNullOrEmpty() && !itemIndex.containsKey(alias)) itemIndex[alias] = meta

                versionBuckets.getOrPut(version) { mutableListOf() }.add(meta to codeId)
            }
        }

        val sortedVersions = versionBuckets.keys.sorted()
        for (v in sortedVersions) {
            val entries = versionBuckets[v] ?: continue
            for ((meta, primaryKey) in entries) {
                if (!compactIndex.containsKey(primaryKey)) {
                    val compactId = nextCompactId++
                    compactIndex[primaryKey] = compactId
                    compactReverseList.add(meta)
                }
                val resolvedId = compactIndex[primaryKey]!!
                if (!compactIndex.containsKey(meta.id)) compactIndex[meta.id] = resolvedId
                if (!meta.alias.isNullOrEmpty() && !compactIndex.containsKey(meta.alias)) {
                    compactIndex[meta.alias] = resolvedId
                }
            }
        }

        return BmoManifestParsed(
            itemIndex = itemIndex,
            compactReverse = compactReverseList,
            categoryLimits = categoryLimits
        )
    }

    /**
     * 根据 (bmoxxxxx) 或 (bmoCxxxxx) 代码解码出图层叠加数据列表
     *
     * @param code 表情代码，例如 "(bmo_f1_m2_e3)" 或 "(bmoC...)"
     * @param manifestJson 可选的清单 JSON 字符串，默认为 [DEFAULT_BMO_MANIFEST_JSON]
     * @return [BmoDecodeResult]，其中 `items` 已按图层在画布上的绘制顺序（底层到顶层）排序
     */
    fun decode(code: String?, manifestJson: String? = null): BmoDecodeResult {
        if (code.isNullOrBlank()) {
            return BmoDecodeResult(rawCode = code.orEmpty(), items = emptyList())
        }

        var trimmed = code.trim()
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = trimmed.substring(1, trimmed.length - 1).trim()
        }

        val manifest = getParsedManifest(manifestJson)

        if (trimmed.startsWith("bmoC")) {
            val compactResult = decodeCompact(trimmed, manifest)
            if (compactResult != null) {
                return compactResult
            }
        }

        val prefix = "bmo"
        var payload = if (trimmed.startsWith(prefix)) {
            trimmed.substring(prefix.length)
        } else {
            return BmoDecodeResult(rawCode = trimmed, items = emptyList(), unknown = listOf(trimmed))
        }

        if (payload.startsWith("_") || payload.startsWith(":") || payload.startsWith("-")) {
            payload = payload.substring(1)
        }

        val parts = if (payload.isEmpty()) emptyList() else payload.split("_")
        val resolvedList = mutableListOf<BmoResolvedItem>()
        val unknownList = mutableListOf<String>()
        val options = mutableMapOf<String, Any?>()
        var scale = 1f

        var index = 0
        while (index < parts.size) {
            var consumed = 0
            var matchedMeta: BmoItemMeta? = null
            var matchedModifiers: Map<String, Any?>? = null

            var maxSegments = parts.size - index
            for (probe in index until parts.size) {
                if (parts[probe].contains(":")) {
                    maxSegments = probe - index + 1
                    break
                }
            }

            for (len in maxSegments downTo 1) {
                val chunk = parts.subList(index, index + len).joinToString("_")
                val token = splitToken(chunk)
                val meta = manifest.itemIndex[token.first]
                if (meta != null) {
                    matchedMeta = meta
                    matchedModifiers = token.second
                    consumed = len
                    break
                }
            }

            if (matchedMeta != null) {
                val rawMods = matchedModifiers ?: emptyMap()
                resolvedList.add(
                    BmoResolvedItem(
                        id = matchedMeta.id,
                        codeId = matchedMeta.codeId,
                        src = matchedMeta.src,
                        layer = matchedMeta.layer,
                        order = matchedMeta.order,
                        category = matchedMeta.category,
                        categoryId = matchedMeta.categoryId,
                        modifiers = buildBmoModifiers(rawMods),
                        rawItem = matchedMeta.rawItem
                    )
                )
                index += consumed
                continue
            }

            val part = parts[index]
            if (part.isEmpty()) {
                index += 1
                continue
            }

            if (part.contains("=")) {
                val kv = part.split("=", limit = 2)
                val k = kv[0]
                val v = kv.getOrNull(1).orEmpty()
                options[k] = decodeModifierValue(v)
            } else if (part == "x2" || part == "x3" || part == "x4") {
                scale = part.substring(1).toFloatOrNull() ?: scale
                options["scale"] = scale
            } else {
                unknownList.add(part)
            }
            index += 1
        }

        options["scale"] = options["scale"] ?: scale
        val limited = enforceCategoryLimits(resolvedList, manifest.categoryLimits)
        val sorted = sortLayerItems(limited)

        return BmoDecodeResult(
            rawCode = trimmed,
            items = sorted,
            unknown = unknownList,
            options = options
        )
    }

    private fun decodeCompact(trimmed: String, manifest: BmoManifestParsed): BmoDecodeResult? {
        if (!trimmed.startsWith("bmoC")) return null
        var payload = trimmed.substring(4)
        if (payload.startsWith("_") || payload.startsWith(":") || payload.startsWith("-")) {
            payload = payload.substring(1)
        }
        if (payload.isEmpty()) {
            return BmoDecodeResult(rawCode = trimmed, items = emptyList())
        }

        val bytes = try {
            decodeBase64Url(payload)
        } catch (e: Throwable) {
            return null
        }
        if (bytes.isEmpty()) {
            return BmoDecodeResult(rawCode = trimmed, items = emptyList())
        }

        val reader = VarReader(bytes)
        val resolvedList = mutableListOf<BmoResolvedItem>()

        while (reader.hasMore()) {
            val combined = reader.readVarUint() ?: return null
            val compactId = (combined ushr 7).toInt()
            val flags = (combined and 127L).toInt()

            val meta = manifest.compactReverse.getOrNull(compactId) ?: return null
            val modifiersMap = mutableMapOf<String, Any?>()

            // COMPACT_FLAG_TF = 1
            if ((flags and 1) != 0) {
                val maskVal = reader.readVarUint() ?: return null
                modifiersMap["tf"] = (maskVal and 63L).toInt()
            }
            // COMPACT_FLAG_H = 2
            if ((flags and 2) != 0) {
                val h = reader.readVarInt() ?: return null
                if (h != 0L) modifiersMap["h"] = h.toInt()
            }
            // COMPACT_FLAG_L = 4
            if ((flags and 4) != 0) {
                val l = reader.readVarInt() ?: return null
                if (l != 0L) modifiersMap["l"] = l.toInt()
            }
            // COMPACT_FLAG_S = 8
            if ((flags and 8) != 0) {
                val s = reader.readVarInt() ?: return null
                if (s != 0L) modifiersMap["s"] = s.toInt()
            }
            // COMPACT_FLAG_X = 16
            if ((flags and 16) != 0) {
                val x = reader.readVarInt() ?: return null
                modifiersMap["x"] = x.toInt()
            }
            // COMPACT_FLAG_Y = 32
            if ((flags and 32) != 0) {
                val y = reader.readVarInt() ?: return null
                modifiersMap["y"] = y.toInt()
            }
            // COMPACT_FLAG_EXTRA = 64
            if ((flags and 64) != 0) {
                val extraLen = reader.readVarUint() ?: return null
                val extraBytes = reader.readBytes(extraLen.toInt()) ?: return null
                val extraString = extraBytes.decodeToString()
                if (extraString.isNotEmpty()) {
                    try {
                        val extraObj = json.decodeFromString<Map<String, JsonPrimitive>>(extraString)
                        for ((ek, ev) in extraObj) {
                            modifiersMap[ek] = ev.content
                        }
                    } catch (_: Throwable) {
                        return null
                    }
                }
            }

            resolvedList.add(
                BmoResolvedItem(
                    id = meta.id,
                    codeId = meta.codeId,
                    src = meta.src,
                    layer = meta.layer,
                    order = meta.order,
                    category = meta.category,
                    categoryId = meta.categoryId,
                    modifiers = buildBmoModifiers(modifiersMap),
                    rawItem = meta.rawItem
                )
            )
        }

        val limited = enforceCategoryLimits(resolvedList, manifest.categoryLimits)
        val sorted = sortLayerItems(limited)

        return BmoDecodeResult(
            rawCode = trimmed,
            items = sorted,
            unknown = emptyList(),
            options = emptyMap()
        )
    }

    /**
     * 将图层项按 PNG 叠加绘制顺序排序：
     * 1. layer 升序
     * 2. category 字母顺序
     * 3. order 升序
     */
    fun sortLayerItems(items: List<BmoResolvedItem>): List<BmoResolvedItem> {
        return items.sortedWith(
            compareBy<BmoResolvedItem> { it.layer }
                .thenBy { it.category }
                .thenBy { it.order }
        )
    }

    private fun enforceCategoryLimits(
        items: List<BmoResolvedItem>,
        categoryLimits: Map<String, Int?>
    ): List<BmoResolvedItem> {
        val usage = mutableMapOf<String, Int>()
        val result = mutableListOf<BmoResolvedItem>()
        for (item in items) {
            val limit = categoryLimits[item.category]
            if (limit != null) {
                val count = usage.getOrElse(item.category) { 0 }
                if (count >= limit) continue
                usage[item.category] = count + 1
            }
            result.add(item)
        }
        return result
    }

    private fun splitToken(raw: String): Pair<String, Map<String, Any?>> {
        val idx = raw.indexOf(':')
        if (idx == -1) {
            return raw to emptyMap()
        }
        val id = raw.substring(0, idx)
        val modStr = raw.substring(idx + 1)
        return id to parseModifiers(modStr)
    }

    private fun parseModifiers(raw: String): Map<String, Any?> {
        if (raw.isEmpty()) return emptyMap()
        val modifiers = mutableMapOf<String, Any?>()
        val parts = raw.split("|")
        for (piece in parts) {
            if (piece.isEmpty()) continue
            val kv = piece.split("=", limit = 2)
            if (kv.size == 1) {
                modifiers[kv[0]] = true
            } else {
                val key = kv[0]
                val rest = kv[1]
                modifiers[key] = decodeModifierValue(rest)
            }
        }
        return modifiers
    }

    private fun decodeModifierValue(value: String): Any? {
        if (value.isEmpty()) return null
        if (value == "true") return true
        if (value == "false") return false
        val num = value.toFloatOrNull()
        if (num != null) return num
        return value
    }

    private fun buildBmoModifiers(raw: Map<String, Any?>): BmoModifiers {
        val hue = raw["h"]?.toString()?.toFloatOrNull()
            ?: raw["hue"]?.toString()?.toFloatOrNull()
        val lightness = raw["l"]?.toString()?.toFloatOrNull()
            ?: raw["lightness"]?.toString()?.toFloatOrNull()
        val saturation = raw["s"]?.toString()?.toFloatOrNull()
            ?: raw["saturation"]?.toString()?.toFloatOrNull()

        val tfValue = raw["tf"]?.toString()?.toFloatOrNull()?.toInt()
        val (flipH, flipV, rotation) = if (tfValue != null) {
            val mask = tfValue and 63
            val fH = (mask and 1) != 0
            val fV = (mask and 2) != 0
            val rot = ((mask shr 2) and 3) * 90f
            Triple(fH, fV, rot)
        } else {
            val rot = raw["rotate"]?.toString()?.toFloatOrNull()
                ?: raw["rotation"]?.toString()?.toFloatOrNull()
                ?: 0f
            val fH = isTruthy(raw["flipH"]) || isTruthy(raw["fliph"]) || isTruthy(raw["mirror"])
            val fV = isTruthy(raw["flipV"]) || isTruthy(raw["flipv"])
            Triple(fH, fV, rot)
        }

        val x = raw["x"]?.toString()?.toFloatOrNull() ?: 0f
        val y = raw["y"]?.toString()?.toFloatOrNull() ?: 0f

        val scale = raw["scale"]?.toString()?.toFloatOrNull() ?: 1f
        val scaleX = raw["scaleX"]?.toString()?.toFloatOrNull()
            ?: raw["scaleH"]?.toString()?.toFloatOrNull()
            ?: scale
        val scaleY = raw["scaleY"]?.toString()?.toFloatOrNull()
            ?: raw["scaleV"]?.toString()?.toFloatOrNull()
            ?: scale

        return BmoModifiers(
            hue = hue,
            lightness = lightness,
            saturation = saturation,
            flipH = flipH,
            flipV = flipV,
            rotation = rotation,
            x = x,
            y = y,
            scale = scale,
            scaleX = scaleX,
            scaleY = scaleY,
            rawModifiers = raw
        )
    }

    private fun isTruthy(value: Any?): Boolean {
        if (value == null) return false
        if (value is Boolean) return value
        if (value is Number) return value.toDouble() != 0.0
        val s = value.toString().lowercase()
        return s == "true" || s == "1" || s == "yes" || s == "y"
    }

    private fun decodeBase64Url(str: String): ByteArray {
        val base64 = str.replace('-', '+').replace('_', '/')
        val padLen = (4 - base64.length % 4) % 4
        val padded = if (padLen == 4) base64 else base64 + "=".repeat(padLen)

        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
        val bytes = mutableListOf<Byte>()
        var i = 0
        while (i < padded.length) {
            val c0 = alphabet.indexOf(padded[i])
            val c1 = if (i + 1 < padded.length) alphabet.indexOf(padded[i + 1]) else 64
            val c2 = if (i + 2 < padded.length) alphabet.indexOf(padded[i + 2]) else 64
            val c3 = if (i + 3 < padded.length) alphabet.indexOf(padded[i + 3]) else 64

            if (c0 == -1 || c1 == -1) break

            val b0 = ((c0 shl 2) or (c1 shr 4)) and 0xFF
            bytes.add(b0.toByte())

            if (c2 != 64 && c2 != -1) {
                val b1 = (((c1 and 0xF) shl 4) or (c2 shr 2)) and 0xFF
                bytes.add(b1.toByte())
            }
            if (c3 != 64 && c3 != -1) {
                val b2 = (((c2 and 0x3) shl 6) or c3) and 0xFF
                bytes.add(b2.toByte())
            }
            i += 4
        }
        return bytes.toByteArray()
    }

    private class VarReader(private val bytes: ByteArray) {
        var offset = 0

        fun hasMore(): Boolean = offset < bytes.size

        fun readVarUint(): Long? {
            var result = 0L
            var shift = 0
            while (offset < bytes.size) {
                val b = bytes[offset++].toInt() and 0xFF
                result = result or ((b and 127).toLong() shl shift)
                if ((b and 128) == 0) {
                    return result
                }
                shift += 7
                if (shift > 35) return null
            }
            return null
        }

        fun readVarInt(): Long? {
            val unsigned = readVarUint() ?: return null
            return (unsigned ushr 1) xor (-(unsigned and 1L))
        }

        fun readBytes(length: Int): ByteArray? {
            if (offset + length > bytes.size) return null
            val result = bytes.copyOfRange(offset, offset + length)
            offset += length
            return result
        }
    }
}
