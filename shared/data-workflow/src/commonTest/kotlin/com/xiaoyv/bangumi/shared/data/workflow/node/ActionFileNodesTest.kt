package com.xiaoyv.bangumi.shared.data.workflow.node

import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowException
import com.xiaoyv.bangumi.shared.data.workflow.model.definition.ActionNode
import com.xiaoyv.bangumi.shared.data.workflow.model.execution.ActionExecutionContext
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionFileConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionHttpConfigKey
import com.xiaoyv.bangumi.shared.data.workflow.model.spec.ActionNodeType
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io.fileActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.builtin.io.httpActionNodeDefinitions
import com.xiaoyv.bangumi.shared.data.workflow.node.effect.ActionHttpRequestEffect
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpDownloadResponse
import com.xiaoyv.bangumi.shared.data.workflow.port.ActionHttpRequestExecutor
import com.xiaoyv.bangumi.shared.data.workflow.port.DefaultActionWorkflowFileStorage
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.isDirectory
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import no.synth.kmpzip.io.ByteArrayOutputStream
import no.synth.kmpzip.zip.ZipEntry
import no.synth.kmpzip.zip.ZipOutputStream
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
    private val homeDir = PlatformFile("data-workflow-file-nodes-test")
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
            storage.resolveSandboxPath(workflowId, homeDir.absolutePath())
        }
        assertEquals(ActionErrorCode.FILE_ACCESS_DENIED, absolute.code)
    }

    @Test
    fun sandboxRejectsInvalidWorkflowId() {
        listOf("../other", "workflow id", "工作流").forEach { invalidWorkflowId ->
            val error = assertFailsWith<ActionWorkflowException> {
                storage.resolveSandboxPath(invalidWorkflowId, "log.txt")
            }
            assertEquals(ActionErrorCode.WORKFLOW_ID_INVALID, error.code)
            assertEquals(ActionErrorCode.WORKFLOW_ID_INVALID_MSG, error.messageText)
        }
    }

    @Test
    fun readTextRejectsFilesLargerThanTenMiB() = runBlocking {
        storage.writeText(workflowId, "large.txt", "x".repeat(10 * 1024 * 1024 + 1), append = false)

        val error = assertFailsWith<ActionWorkflowException> {
            storage.readText(workflowId, "large.txt")
        }

        assertEquals(ActionErrorCode.FILE_SIZE_EXCEEDED, error.code)
    }

    @Test
    fun fileCreatePreservesExistingContentAndWorkingDirectoryIsSandboxDirectory() = runBlocking {
        execute(
            ActionNodeType.FILE_CREATE, config(
                ActionFileConfigKey.PATH to "created/empty.txt",
                ActionFileConfigKey.OUTPUT_KEY to "created",
            )
        )
        assertEquals("", storage.readText(workflowId, "created/empty.txt"))
        storage.writeText(workflowId, "created/empty.txt", "preserved", append = false)
        execute(
            ActionNodeType.FILE_CREATE, config(
                ActionFileConfigKey.PATH to "created/empty.txt",
                ActionFileConfigKey.OUTPUT_KEY to "createdAgain",
            )
        )
        assertEquals("preserved", storage.readText(workflowId, "created/empty.txt"))

        val result = execute(
            ActionNodeType.FILE_GET_WORKING_DIRECTORY, config(ActionFileConfigKey.OUTPUT_KEY to "workingDirectory")
        )
        assertEquals(storage.workingDirectory(workflowId), result.output.getValue("workingDirectory").jsonPrimitive.content)
    }

    @Test
    fun httpDownloadWritesResponseBytesAndUsesHeadersAndLocalCookies() = runBlocking {
        var downloadedRequest: ActionHttpRequestEffect? = null
        val executor = object : ActionHttpRequestExecutor {
            override suspend fun execute(request: ActionHttpRequestEffect) = JsonObject(emptyMap())

            override suspend fun download(
                request: ActionHttpRequestEffect,
                onResponse: suspend (ActionHttpDownloadResponse) -> Unit,
                consumeChunk: suspend (ByteArray) -> Unit,
            ): ActionHttpDownloadResponse {
                downloadedRequest = request
                val response = ActionHttpDownloadResponse(
                    statusCode = 200,
                    contentType = "text/plain",
                    contentDisposition = "attachment; filename=server.txt",
                )
                onResponse(response)
                consumeChunk("down".encodeToByteArray())
                consumeChunk("loaded".encodeToByteArray())
                return response
            }
        }
        val definition = httpActionNodeDefinitions(executor, storage).associateBy { it.spec.type }
            .getValue(ActionNodeType.HTTP_DOWNLOAD)
        val result = definition.executor.execute(
            ActionNode(
                id = "download",
                type = ActionNodeType.HTTP_DOWNLOAD,
                config = config(
                    "url" to "https://example.com/download",
                    "path" to "downloads",
                    "headers" to JsonObject(mapOf("Authorization" to JsonPrimitive("Bearer token"))),
                    "useLocalCookieStorage" to true,
                    "outputKey" to "download",
                ),
            ),
            context,
        )

        assertEquals("Bearer token", downloadedRequest?.headers?.get("Authorization")?.jsonPrimitive?.content)
        assertTrue(downloadedRequest?.useLocalCookieStorage == true)
        assertEquals("downloaded", storage.readText(workflowId, "downloads/server.txt"))
        assertEquals("server.txt", result.output.getValue("download").jsonObject.getValue("fileName").jsonPrimitive.content)
    }

    /**
     * 普通 HTTP 节点应将声明的 Header 原样传入底层执行器。
     */
    @Test
    fun httpRequestPassesConfiguredHeadersToExecutor() = runBlocking {
        var capturedRequest: ActionHttpRequestEffect? = null
        val executor = ActionHttpRequestExecutor { request ->
            capturedRequest = request
            JsonObject(emptyMap())
        }
        val definition = httpActionNodeDefinitions(executor, storage).associateBy { it.spec.type }
            .getValue(ActionNodeType.HTTP_REQUEST)

        definition.executor.execute(
            ActionNode(
                id = "request",
                type = ActionNodeType.HTTP_REQUEST,
                config = config(
                    ActionHttpConfigKey.URL to "https://example.com/resource",
                    ActionHttpConfigKey.HEADERS to JsonObject(
                        mapOf("User-Agent" to JsonPrimitive("workflow-test-agent")),
                    ),
                ),
            ),
            context,
        )

        assertEquals(
            "workflow-test-agent",
            capturedRequest?.headers?.get("User-Agent")?.jsonPrimitive?.content,
        )
    }

    @Test
    fun httpDownloadRemovesPartialFileWhenChannelReadFails() = runBlocking {
        val executor = object : ActionHttpRequestExecutor {
            override suspend fun execute(request: ActionHttpRequestEffect) = JsonObject(emptyMap())

            override suspend fun download(
                request: ActionHttpRequestEffect,
                onResponse: suspend (ActionHttpDownloadResponse) -> Unit,
                consumeChunk: suspend (ByteArray) -> Unit,
            ): ActionHttpDownloadResponse {
                val response = ActionHttpDownloadResponse(200, "application/octet-stream", null)
                onResponse(response)
                consumeChunk("partial".encodeToByteArray())
                error("channel disconnected")
            }
        }
        val definition = httpActionNodeDefinitions(executor, storage).associateBy { it.spec.type }
            .getValue(ActionNodeType.HTTP_DOWNLOAD)

        assertFailsWith<IllegalStateException> {
            definition.executor.execute(
                ActionNode(
                    id = "partialDownload",
                    type = ActionNodeType.HTTP_DOWNLOAD,
                    config = config(
                        "url" to "https://example.com/download",
                        "path" to "downloads",
                        "fileName" to "partial.bin",
                        "outputKey" to "download",
                    ),
                ),
                context,
            )
        }
        assertFalse(storage.exists(workflowId, "downloads/partial.bin"))
    }

    @Test
    fun fileNodesCompressAndExtractZipWithDirectoryHierarchy() = runBlocking {
        storage.writeText(workflowId, "source/first.txt", "first", append = false)
        storage.writeText(workflowId, "source/nested/second.txt", "second", append = false)

        execute(
            ActionNodeType.FILE_COMPRESS_ZIP, config(
                ActionFileConfigKey.PATHS to JsonArray(listOf(JsonPrimitive("source"))),
                ActionFileConfigKey.TO_PATH to "archives/source.zip",
                ActionFileConfigKey.OUTPUT_KEY to "compressed",
            )
        )
        assertTrue(storage.exists(workflowId, "archives/source.zip"))

        execute(
            ActionNodeType.FILE_EXTRACT_ZIP, config(
                ActionFileConfigKey.FROM_PATH to "archives/source.zip",
                ActionFileConfigKey.TO_PATH to "restored",
                ActionFileConfigKey.OUTPUT_KEY to "extracted",
            )
        )
        assertEquals("first", storage.readText(workflowId, "restored/source/first.txt"))
        assertEquals("second", storage.readText(workflowId, "restored/source/nested/second.txt"))
    }

    @Test
    fun extractZipRejectsEntriesThatEscapeDestinationDirectory() = runBlocking {
        val archive = ByteArrayOutputStream().also { output ->
            ZipOutputStream(output).use { zip ->
                zip.putNextEntry(ZipEntry("../outside.txt"))
                zip.write("unsafe".encodeToByteArray())
                zip.closeEntry()
                zip.finish()
            }
        }.toByteArray()
        storage.mkdir(workflowId, "archives")
        storage.resolveSandboxPath(workflowId, "archives/unsafe.zip") write archive

        val error = assertFailsWith<ActionWorkflowException> {
            storage.extractZip(workflowId, "archives/unsafe.zip", "restored")
        }

        assertEquals(ActionErrorCode.FILE_ARCHIVE_INVALID, error.code)
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
                    is JsonElement -> value
                    is Boolean -> JsonPrimitive(value)
                    else -> JsonPrimitive(value.toString())
                }
            )
        }
    }
}
