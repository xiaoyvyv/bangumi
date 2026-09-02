package com.xiaoyv.bangumi.shared.data.workflow.node

import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowException
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io.fileActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.port.DefaultActionWorkflowFileStorage
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.isDirectory
import io.github.vinceglb.filekit.list
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * 文件节点及工作流隔离沙箱的集成测试。
 */
class ActionFileNodesTest {
    private val homeDir = PlatformFile("/tmp/data-workflow-file-nodes-test")
    private val workflowId = "file_node_test"
    private val storage = DefaultActionWorkflowFileStorage(homeDir)
    private val definitions = fileActionNodeDefinitions(storage).associateBy { it.spec.type }
    private val context = ActionExecutionContext(workflowId = workflowId)

    @AfterTest
    fun cleanSandbox() = runBlocking {
        if (homeDir.exists()) deleteRecursively(homeDir)
    }

    @Test
    fun fileNodesPerformTextDirectoryCopyMoveAndDeleteOperations() = runBlocking {
        execute(
            ActionNodeType.FILE_WRITE_TEXT, config(
                ActionFileConfigKey.PATH to "logs/source.txt",
                ActionFileConfigKey.TEXT to "hello",
                ActionFileConfigKey.OUTPUT_KEY to "written",
            )
        )
        execute(
            ActionNodeType.FILE_WRITE_TEXT, config(
                ActionFileConfigKey.PATH to "logs/source.txt",
                ActionFileConfigKey.TEXT to " world",
                ActionFileConfigKey.APPEND to true,
                ActionFileConfigKey.OUTPUT_KEY to "appended",
            )
        )
        val read = execute(
            ActionNodeType.FILE_READ_TEXT, config(
                ActionFileConfigKey.PATH to "logs/source.txt",
                ActionFileConfigKey.OUTPUT_KEY to "content",
            )
        )
        assertEquals("hello world", read.output.getValue("content").jsonPrimitive.content)

        execute(
            ActionNodeType.FILE_MKDIR, config(
                ActionFileConfigKey.PATH to "archive",
                ActionFileConfigKey.OUTPUT_KEY to "created",
            )
        )
        execute(
            ActionNodeType.FILE_COPY, config(
                ActionFileConfigKey.FROM_PATH to "logs/source.txt",
                ActionFileConfigKey.TO_PATH to "archive/copy.txt",
                ActionFileConfigKey.OUTPUT_KEY to "copied",
            )
        )
        execute(
            ActionNodeType.FILE_MOVE, config(
                ActionFileConfigKey.FROM_PATH to "archive/copy.txt",
                ActionFileConfigKey.TO_PATH to "archive/moved.txt",
                ActionFileConfigKey.OUTPUT_KEY to "moved",
            )
        )
        val listed = execute(
            ActionNodeType.FILE_LIST, config(
                ActionFileConfigKey.PATH to "archive",
                ActionFileConfigKey.OUTPUT_KEY to "files",
            )
        )
        assertEquals(listOf("archive/moved.txt"), listed.output.getValue("files").jsonArray.map { it.jsonPrimitive.content })

        val exists = execute(
            ActionNodeType.FILE_EXISTS, config(
                ActionFileConfigKey.PATH to "archive/moved.txt",
                ActionFileConfigKey.OUTPUT_KEY to "exists",
            )
        )
        assertTrue(exists.output.getValue("exists").jsonPrimitive.boolean)
        execute(
            ActionNodeType.FILE_DELETE, config(
                ActionFileConfigKey.PATH to "archive/moved.txt",
                ActionFileConfigKey.OUTPUT_KEY to "deleted",
            )
        )
        assertFalse(storage.exists(workflowId, "archive/moved.txt"))
    }

    @Test
    fun sandboxRejectsParentTraversalAndAbsolutePaths() {
        val traversal = assertFailsWith<ActionWorkflowException> {
            storage.resolveSandboxPath(workflowId, "../outside.txt")
        }
        assertEquals(ActionErrorCode.FILE_ACCESS_DENIED, traversal.code)
        val absolute = assertFailsWith<ActionWorkflowException> {
            storage.resolveSandboxPath(workflowId, "/etc/passwd")
        }
        assertEquals(ActionErrorCode.FILE_ACCESS_DENIED, absolute.code)
    }

    private suspend fun execute(type: String, config: JsonObject) = definitions.getValue(type).executor.execute(
        ActionNode(id = type, type = type, config = config),
        context,
    )

    private suspend fun deleteRecursively(file: PlatformFile) {
        if (file.isDirectory()) {
            for (child in file.list()) deleteRecursively(child)
        }
        file.delete()
    }

    private fun config(vararg values: Pair<String, Any>): JsonObject = buildJsonObject {
        values.forEach { (key, value) ->
            put(
                key, when (value) {
                    is Boolean -> JsonPrimitive(value)
                    else -> JsonPrimitive(value.toString())
                }
            )
        }
    }
}
