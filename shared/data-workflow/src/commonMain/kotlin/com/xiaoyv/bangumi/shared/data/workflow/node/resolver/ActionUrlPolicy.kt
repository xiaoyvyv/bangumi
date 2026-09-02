package com.xiaoyv.bangumi.shared.data.workflow.node.resolver

import com.xiaoyv.bangumi.shared.data.workflow.node.core.*
import com.xiaoyv.bangumi.shared.data.workflow.node.resolver.*
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.*
/**
 * 对导入 JSON 最终解析出的地址进行跨平台安全校验。
 */
object ActionUrlPolicy {
    /**
     * 验证仅供网页和内部 WebView 打开的 HTTP 地址。
     *
     * @param value 模板解析后的地址。
     */
    fun requireHttpUrl(value: String) {
        require(value.startsWith("https://", ignoreCase = true) || value.startsWith("http://", ignoreCase = true)) {
            "仅支持 http 或 https 地址"
        }
    }

    /**
     * 验证供系统唤起外部应用的自定义 Scheme 地址。
     *
     * @param value 模板解析后的 URI。
     */
    fun requireExternalAppUri(value: String) {
        val scheme = value.substringBefore(':', missingDelimiterValue = "").lowercase()
        require(scheme.isNotBlank() && scheme !in setOf("http", "https", "javascript", "file", "data")) {
            "外部 App 地址必须使用受支持的自定义 Scheme"
        }
    }
}
