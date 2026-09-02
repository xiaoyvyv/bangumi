package com.xiaoyv.bangumi.shared.data.workflow.node.builtin.parse

import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.inPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.nextPort
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.valueResult
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeCategory
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeDefinition
import com.xiaoyv.bangumi.shared.data.workflow.node.core.ActionNodeSpec
import com.xiaoyv.bangumi.shared.data.workflow.node.core.string
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.ActionTemplateResolver
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * 加密与哈希计算内置节点。
 */
internal val cryptoActionNodeDefinitions: List<ActionNodeDefinition> = listOf(
    cryptoHashDefinition(),
    cryptoHmacDefinition(),
    cryptoEncryptDefinition(),
    cryptoDecryptDefinition(),
    cryptoRandomBytesDefinition(),
    cryptoUuidDefinition(),
)

@OptIn(ExperimentalStdlibApi::class)
private fun cryptoHashDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CRYPTO_HASH,
        category = ActionNodeCategory.CRYPTO,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCryptoConfigKey.TEXT, ActionCryptoConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCryptoConfigKey.TEXT), context)
        val algoStr = node.config[ActionCryptoConfigKey.ALGORITHM]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: ActionHashAlgorithm.SHA_256
        val algorithm = resolveAlgorithm(algoStr)
        val hashHex = algorithm.hash(text.encodeToByteArray()).toHexString()
        node.valueResult(node.config.string(ActionCryptoConfigKey.OUTPUT_KEY), JsonPrimitive(hashHex))
    },
)

@OptIn(ExperimentalStdlibApi::class)
private fun cryptoHmacDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CRYPTO_HMAC,
        category = ActionNodeCategory.CRYPTO,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCryptoConfigKey.TEXT, ActionCryptoConfigKey.SECRET, ActionCryptoConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCryptoConfigKey.TEXT), context)
        val secret = ActionTemplateResolver.resolveText(node.config.string(ActionCryptoConfigKey.SECRET), context)
        val algoStr = node.config[ActionCryptoConfigKey.ALGORITHM]?.let { ActionTemplateResolver.resolveText(it.jsonPrimitive.content, context) } ?: ActionHashAlgorithm.SHA_256
        val algorithm = resolveAlgorithm(algoStr)
        val macBytes = computeHmac(algorithm, secret.encodeToByteArray(), text.encodeToByteArray())
        node.valueResult(node.config.string(ActionCryptoConfigKey.OUTPUT_KEY), JsonPrimitive(macBytes.toHexString()))
    },
)

private fun computeHmac(algorithm: com.appmattus.crypto.Algorithm, key: ByteArray, data: ByteArray): ByteArray {
    val blockSize = if (algorithm == com.appmattus.crypto.Algorithm.SHA_512) 128 else 64
    val actualKey = when {
        key.size > blockSize -> algorithm.createDigest().digest(key)
        key.size < blockSize -> key.copyOf(blockSize)
        else -> key
    }
    val oKeyPad = ByteArray(blockSize) { i -> (actualKey[i].toInt() xor 0x5c).toByte() }
    val iKeyPad = ByteArray(blockSize) { i -> (actualKey[i].toInt() xor 0x36).toByte() }
    val innerHash = algorithm.createDigest().digest(iKeyPad + data)
    return algorithm.createDigest().digest(oKeyPad + innerHash)
}

private fun resolveAlgorithm(algoName: String): com.appmattus.crypto.Algorithm {
    return when (algoName.lowercase().replace("-", "").replace("_", "")) {
        ActionHashAlgorithm.MD5 -> com.appmattus.crypto.Algorithm.MD5
        ActionHashAlgorithm.SHA_1 -> com.appmattus.crypto.Algorithm.SHA_1
        ActionHashAlgorithm.SHA_224 -> com.appmattus.crypto.Algorithm.SHA_224
        ActionHashAlgorithm.SHA_384 -> com.appmattus.crypto.Algorithm.SHA_384
        ActionHashAlgorithm.SHA_512 -> com.appmattus.crypto.Algorithm.SHA_512
        ActionHashAlgorithm.SHA3_224 -> com.appmattus.crypto.Algorithm.SHA3_224
        ActionHashAlgorithm.SHA3_256 -> com.appmattus.crypto.Algorithm.SHA3_256
        ActionHashAlgorithm.SHA3_384 -> com.appmattus.crypto.Algorithm.SHA3_384
        ActionHashAlgorithm.SHA3_512 -> com.appmattus.crypto.Algorithm.SHA3_512
        ActionHashAlgorithm.SM3 -> com.appmattus.crypto.Algorithm.SM3
        ActionHashAlgorithm.CRC32 -> com.appmattus.crypto.Algorithm.CRC32
        else -> com.appmattus.crypto.Algorithm.SHA_256
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun cryptoEncryptDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CRYPTO_ENCRYPT,
        category = ActionNodeCategory.CRYPTO,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCryptoConfigKey.TEXT, ActionCryptoConfigKey.SECRET_KEY, ActionCryptoConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val text = ActionTemplateResolver.resolveText(node.config.string(ActionCryptoConfigKey.TEXT), context)
        val secretKey = ActionTemplateResolver.resolveText(node.config.string(ActionCryptoConfigKey.SECRET_KEY), context)
        val encryptedBytes = cipherStream(secretKey.encodeToByteArray(), text.encodeToByteArray())
        node.valueResult(node.config.string(ActionCryptoConfigKey.OUTPUT_KEY), JsonPrimitive(encryptedBytes.toHexString()))
    },
)

@OptIn(ExperimentalStdlibApi::class)
private fun cryptoDecryptDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CRYPTO_DECRYPT,
        category = ActionNodeCategory.CRYPTO,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCryptoConfigKey.TEXT, ActionCryptoConfigKey.SECRET_KEY, ActionCryptoConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val hexText = ActionTemplateResolver.resolveText(node.config.string(ActionCryptoConfigKey.TEXT), context)
        val secretKey = ActionTemplateResolver.resolveText(node.config.string(ActionCryptoConfigKey.SECRET_KEY), context)
        val decryptedBytes = cipherStream(secretKey.encodeToByteArray(), hexText.hexToByteArray())
        node.valueResult(node.config.string(ActionCryptoConfigKey.OUTPUT_KEY), JsonPrimitive(decryptedBytes.decodeToString()))
    },
)

@OptIn(ExperimentalStdlibApi::class)
private fun cryptoRandomBytesDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CRYPTO_RANDOM_BYTES,
        category = ActionNodeCategory.CRYPTO,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCryptoConfigKey.OUTPUT_KEY),
    ),
    executor = { node, context ->
        val count = node.config[ActionCryptoConfigKey.KEY_BYTES]?.let { ActionTemplateResolver.resolveElement(it, context).jsonPrimitive.content.toInt() } ?: 16
        val bytes = kotlin.random.Random.nextBytes(count)
        node.valueResult(node.config.string(ActionCryptoConfigKey.OUTPUT_KEY), JsonPrimitive(bytes.toHexString()))
    },
)

private fun cipherStream(key: ByteArray, data: ByteArray): ByteArray {
    val hashKey = com.appmattus.crypto.Algorithm.SHA_256.hash(key)
    return ByteArray(data.size) { i ->
        val keyByte = hashKey[i % hashKey.size]
        (data[i].toInt() xor keyByte.toInt()).toByte()
    }
}

@OptIn(kotlin.uuid.ExperimentalUuidApi::class)
private fun cryptoUuidDefinition() = ActionNodeDefinition(
    spec = ActionNodeSpec(
        type = ActionNodeType.CRYPTO_UUID,
        category = ActionNodeCategory.CRYPTO,
        inputPorts = persistentListOf(inPort),
        outputPorts = persistentListOf(nextPort),
        requiredConfigKeys = setOf(ActionCryptoConfigKey.OUTPUT_KEY),
    ),
    executor = { node, _ ->
        node.valueResult(
            node.config.string(ActionCryptoConfigKey.OUTPUT_KEY),
            JsonPrimitive(kotlin.uuid.Uuid.random().toString()),
        )
    },
)
