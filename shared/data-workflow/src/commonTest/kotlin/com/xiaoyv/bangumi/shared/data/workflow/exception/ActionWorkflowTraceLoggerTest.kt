package com.xiaoyv.bangumi.shared.data.workflow.exception

import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * [ActionWorkflowException] 与 [ActionWorkflowTraceLogger] 诊断日志追溯单元测试。
 */
class ActionWorkflowTraceLoggerTest {

    @Test
    fun testFormattedTraceLogOutput() {
        val exception = ActionNodeExecutionException(
            code = "MATH_DIVIDE_BY_ZERO",
            messageText = "除数不能为 0",
            nodeId = "divide_node_1",
            nodeType = "math.divide",
            nodeLabel = "计算平均折扣",
            configKey = "right",
            details = mapOf("left" to JsonPrimitive(100), "right" to JsonPrimitive(0)),
            hint = "请检测除数变量是否可能为 0，或在执行除法前加入 control.if 条件校验。",
            cause = ArithmeticException("/ by zero")
        )

        val formattedLog = exception.buildFormattedTraceLog()

        assertTrue(formattedLog.contains("[WORKFLOW ERROR TRACE]"))
        assertTrue(formattedLog.contains("divide_node_1"))
        assertTrue(formattedLog.contains("math.divide"))
        assertTrue(formattedLog.contains("MATH_DIVIDE_BY_ZERO"))
        assertTrue(formattedLog.contains("除数不能为 0"))
        assertTrue(formattedLog.contains("right"))
        assertTrue(formattedLog.contains("ArithmeticException"))

        val executionError = exception.toExecutionError()
        assertEquals("MATH_DIVIDE_BY_ZERO", executionError.code)
        assertEquals("divide_node_1", executionError.nodeId)
        assertEquals("除数不能为 0", executionError.message)
    }

    @Test
    fun testLoggerListenerRegistry() {
        var loggedMessage: String? = null
        val listener = ActionWorkflowLogListener { _, _, message ->
            loggedMessage = message
        }

        ActionWorkflowTraceLogger.addListener(listener)

        val exception = ActionWorkflowException(
            code = "TEST_ERROR",
            messageText = "测试错误消息",
            nodeId = "test_node"
        )
        ActionWorkflowTraceLogger.logError(exception)

        assertTrue(loggedMessage?.contains("TEST_ERROR") == true)
        assertTrue(loggedMessage?.contains("test_node") == true)

        ActionWorkflowTraceLogger.removeListener(listener)
    }
}
