package com.xiaoyv.bangumi.shared.data.workflow.port

import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionErrorCode
import com.xiaoyv.bangumi.shared.data.workflow.exception.ActionWorkflowException
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.atomicMove
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.isAbsolute
import io.github.vinceglb.filekit.isDirectory
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.parent
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.writeString

/**
 * 工作流文件沙箱端口。
 */
interface ActionWorkflowFileStorage {
    /**
     * 工作流文件沙箱的基础目录。
     */
    val homeDir: PlatformFile

    /**
     * 将工作流相对路径解析为已校验的沙箱内路径。
     */
    fun resolveSandboxPath(workflowId: String, path: String): PlatformFile

    /**
     * 读取 UTF-8 文本文件。
     */
    suspend fun readText(workflowId: String, path: String): String

    /**
     * 写入 UTF-8 文本文件。
     */
    suspend fun writeText(workflowId: String, path: String, text: String, append: Boolean)

    /**
     * 删除文件或目录。
     */
    suspend fun delete(workflowId: String, path: String)

    /**
     * 判断文件或目录是否存在。
     */
    suspend fun exists(workflowId: String, path: String): Boolean

    /**
     * 创建目录及其缺失父目录。
     */
    suspend fun mkdir(workflowId: String, path: String)

    /**
     * 列出目录中直接包含的文件和目录相对路径。
     */
    suspend fun list(workflowId: String, path: String): List<String>

    /**
     * 复制文件或目录。
     */
    suspend fun copy(workflowId: String, fromPath: String, toPath: String)

    /**
     * 移动或重命名文件、目录。
     */
    suspend fun move(workflowId: String, fromPath: String, toPath: String)

    companion object {
        val Default = DefaultActionWorkflowFileStorage()
    }
}

/**
 * 基于项目内置 FileKit 的默认工作流文件沙箱。
 */
class DefaultActionWorkflowFileStorage(
    private val configuredHomeDir: PlatformFile? = null,
) : ActionWorkflowFileStorage {
    override val homeDir: PlatformFile
        get() = configuredHomeDir ?: (FileKit.filesDir / "workflows")

    override fun resolveSandboxPath(workflowId: String, path: String): PlatformFile {
        require(workflowId.matches(WORKFLOW_ID_PATTERN)) { "workflowId 不合法" }
        if (path.isBlank() || PlatformFile(path).isAbsolute() || path.split('/', '\\').any { it == ".." }) {
            throw accessDenied(workflowId)
        }
        val sandbox = sandboxDir(workflowId)
        val target = sandbox / path
        requireInsideSandbox(target, sandbox, workflowId)
        return target
    }

    override suspend fun readText(workflowId: String, path: String): String = runFileOperation(workflowId) {
        existingPath(workflowId, path).readString()
    }

    override suspend fun writeText(workflowId: String, path: String, text: String, append: Boolean): Unit = runFileOperation(workflowId) {
        val target = resolveSandboxPath(workflowId, path)
        target.parent()?.createDirectories()
        if (append && target.exists()) target.writeString(target.readString() + text) else target.writeString(text)
    }

    override suspend fun delete(workflowId: String, path: String): Unit = runFileOperation(workflowId) {
        deleteRecursively(existingPath(workflowId, path))
    }

    override suspend fun exists(workflowId: String, path: String): Boolean = runFileOperation(workflowId) {
        resolveSandboxPath(workflowId, path).exists()
    }

    override suspend fun mkdir(workflowId: String, path: String): Unit = runFileOperation(workflowId) {
        resolveSandboxPath(workflowId, path).createDirectories()
    }

    override suspend fun list(workflowId: String, path: String): List<String> = runFileOperation(workflowId) {
        val directory = existingPath(workflowId, path)
        if (!directory.isDirectory()) throw ioFailed(workflowId, "指定路径不是目录")
        directory.list().map { it.absolutePath().removePrefix(sandboxDir(workflowId).absolutePath()).trimStart('/', '\\') }
    }

    override suspend fun copy(workflowId: String, fromPath: String, toPath: String): Unit = runFileOperation(workflowId) {
        val source = existingPath(workflowId, fromPath)
        val target = resolveSandboxPath(workflowId, toPath)
        target.parent()?.createDirectories()
        if (target.exists()) deleteRecursively(target)
        copyRecursively(source, target)
    }

    override suspend fun move(workflowId: String, fromPath: String, toPath: String): Unit = runFileOperation(workflowId) {
        val source = existingPath(workflowId, fromPath)
        val target = resolveSandboxPath(workflowId, toPath)
        target.parent()?.createDirectories()
        source.atomicMove(target)
    }

    private fun sandboxDir(workflowId: String): PlatformFile = homeDir / workflowId

    private fun existingPath(workflowId: String, path: String): PlatformFile = resolveSandboxPath(workflowId, path).also {
        if (!it.exists()) throw notFound(workflowId)
    }

    private suspend fun copyRecursively(source: PlatformFile, target: PlatformFile) {
        if (source.isDirectory()) {
            target.createDirectories()
            source.list().forEach { child -> copyRecursively(child, target / child.name) }
        } else {
            target.writeString(source.readString())
        }
    }

    private suspend fun deleteRecursively(target: PlatformFile) {
        if (target.isDirectory()) {
            for (child in target.list()) deleteRecursively(child)
        }
        target.delete()
    }

    private fun requireInsideSandbox(target: PlatformFile, sandbox: PlatformFile, workflowId: String) {
        val sandboxPath = sandbox.absolutePath().trimEnd('/', '\\')
        val targetPath = target.absolutePath()
        if (targetPath != sandboxPath && !targetPath.startsWith("$sandboxPath/") && !targetPath.startsWith("$sandboxPath\\\\"))
            throw accessDenied(workflowId)
    }

    private inline fun <T> runFileOperation(workflowId: String, block: () -> T): T = try {
        block()
    } catch (exception: ActionWorkflowException) {
        throw exception
    } catch (exception: Throwable) {
        throw ioFailed(workflowId, exception.message)
    }

    private fun accessDenied(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.FILE_ACCESS_DENIED,
        messageText = ActionErrorCode.FILE_ACCESS_DENIED_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_ACCESS_DENIED_HINT,
    )

    private fun notFound(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.FILE_NOT_FOUND,
        messageText = ActionErrorCode.FILE_NOT_FOUND_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_NOT_FOUND_HINT,
    )

    private fun ioFailed(workflowId: String, detail: String? = null) = ActionWorkflowException(
        code = ActionErrorCode.FILE_IO_FAILED,
        messageText = detail?.ifBlank { ActionErrorCode.FILE_IO_FAILED_MSG } ?: ActionErrorCode.FILE_IO_FAILED_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_IO_FAILED_HINT,
    )

    private companion object {
        val WORKFLOW_ID_PATTERN = Regex("[A-Za-z0-9_-]+")
    }
}
