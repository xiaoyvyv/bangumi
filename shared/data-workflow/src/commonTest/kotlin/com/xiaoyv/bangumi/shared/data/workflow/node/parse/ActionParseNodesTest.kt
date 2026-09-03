package com.xiaoyv.bangumi.shared.data.workflow.node.parse

import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionBilibiliConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCodecConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionCryptoConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionDataConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHashAlgorithm
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionJsonConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures
import com.xiaoyv.bangumi.shared.data.workflow.node.fixture.ActionNodeTestFixtures.config
import io.ktor.http.Url
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonDecodingException
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * 文本解析、序列化格式转换 (JSON, XML, CSV)、编解码 (Codec)、哈希计算与 Bilibili WBI 加签测试。
 */
class ActionParseNodesTest {

    private val definitions = ActionNodeTestFixtures.nodeDefinitions()

    @Test
    fun bilibiliWbiSignMergesPublicQueryParameters() = runBlocking {
        val definition = definitions.getValue(ActionNodeType.BILIBILI_SIGN_URL)
        val result = definition.executor.execute(
            ActionNode(
                id = "sign_bilibili_wbi",
                type = ActionNodeType.BILIBILI_SIGN_URL,
                config = config(
                    ActionBilibiliConfigKey.URL to JsonPrimitive(
                        "https://api.bilibili.com/x/web-interface/wbi/search/type?keyword=workflow!*(test)'&page=2&page_size=60&search_type=media_bangumi&w_rid=stale",
                    ),
                    ActionBilibiliConfigKey.OUTPUT_KEY to JsonPrimitive("signedUrl"),
                ),
            ),
            ActionExecutionContext(),
        )
        val query = Url(result.output.getValue("signedUrl").jsonPrimitive.content).parameters

        assertEquals("workflowtest", query["keyword"])
        assertEquals("2", query["page"])
        assertEquals("media_bangumi", query["search_type"])
        assertEquals("24", query["dynamic_offset"])
        assertEquals("pc", query["platform"])
        assertEquals("totalrank", query["order"])
        assertEquals("5654", query["ad_resource"])
        assertEquals("333.337", query["from_spmid"])
        assertEquals("1430654", query["web_location"])
        assertEquals("60", query["page_size"])
        assertTrue(query["qv_id"].orEmpty().matches(Regex("[0-9A-Za-z]{32}")))
        assertTrue(query["wts"].orEmpty().isNotBlank())
        assertTrue(query["w_rid"].orEmpty().isNotBlank())
    }

    @Test
    fun appSignatureVectorMatchesExpectedQueryAndMd5() = runBlocking {
        val expectedQuery =
            "appkey=1d8b6e7d45233436&id=114514&str=1919810&test=%E3%81%84%E3%81%84%E3%82%88%EF%BC%8C%E3%81%93%E3%81%84%E3%82%88"
        val expectedSign = "01479cf20504d865519ac50f33ba3a7d"
        var context = ActionExecutionContext()

        val encoded = definitions.getValue(ActionNodeType.CODEC_URL_ENCODE).executor.execute(
            ActionNode(
                id = "encode_test_value",
                type = ActionNodeType.CODEC_URL_ENCODE,
                config = config(
                    ActionCodecConfigKey.TEXT to JsonPrimitive("いいよ，こいよ"),
                    ActionCodecConfigKey.OUTPUT_KEY to JsonPrimitive("encodedTest"),
                ),
            ),
            context,
        )
        context = context.copy(stepOutputs = (context.stepOutputs + ("encode_test_value" to encoded.output)).toPersistentMap())

        val query = definitions.getValue(ActionNodeType.TEMPLATE).executor.execute(
            ActionNode(
                id = "build_canonical_query",
                type = ActionNodeType.TEMPLATE,
                config = config(
                    ActionDataConfigKey.TEMPLATE to JsonPrimitive(
                        "appkey=1d8b6e7d45233436&id=114514&str=1919810&test=\${steps.encode_test_value.encodedTest}",
                    ),
                    ActionDataConfigKey.OUTPUT_KEY to JsonPrimitive("canonicalQuery"),
                ),
            ),
            context,
        )
        context = context.copy(stepOutputs = (context.stepOutputs + ("build_canonical_query" to query.output)).toPersistentMap())

        val signature = definitions.getValue(ActionNodeType.CRYPTO_HASH).executor.execute(
            ActionNode(
                id = "calculate_signature",
                type = ActionNodeType.CRYPTO_HASH,
                config = config(
                    ActionCryptoConfigKey.TEXT to JsonPrimitive(
                        "\${steps.build_canonical_query.canonicalQuery}560c52ccd288fed045859ed18bffd973",
                    ),
                    ActionCryptoConfigKey.ALGORITHM to JsonPrimitive(ActionHashAlgorithm.MD5),
                    ActionCryptoConfigKey.OUTPUT_KEY to JsonPrimitive("sign"),
                ),
            ),
            context,
        )

        assertEquals(expectedQuery, query.output.getValue("canonicalQuery").jsonPrimitive.content)
        assertEquals(expectedSign, signature.output.getValue("sign").jsonPrimitive.content)
    }

    @Test
    fun testJsonParseMalformedStringThrowsException() {
        val definition = definitions.getValue(ActionNodeType.JSON_PARSE)
        val node = ActionNode(
            id = "json_parse_bad",
            type = ActionNodeType.JSON_PARSE,
            config = config(ActionJsonConfigKey.TEXT to JsonPrimitive("{invalid_json_text")),
        )

        assertFailsWith<JsonDecodingException> {
            runBlocking {
                definition.executor.execute(node, ActionExecutionContext())
            }
        }
    }
}
