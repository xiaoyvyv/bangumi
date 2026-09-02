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
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.readString
import io.github.vinceglb.filekit.sink
import io.github.vinceglb.filekit.size
import io.github.vinceglb.filekit.source
import io.github.vinceglb.filekit.write
import io.github.vinceglb.filekit.writeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.readByteArray
import no.synth.kmpzip.io.ByteArrayInputStream
import no.synth.kmpzip.io.ByteArrayOutputStream
import no.synth.kmpzip.zip.ZipEntry
import no.synth.kmpzip.zip.ZipInputStream
import no.synth.kmpzip.zip.ZipOutputStream

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
     * 创建空文件；若目标已经存在，则不修改原内容。
     */
    suspend fun createFile(workflowId: String, path: String)

    /**
     * 写入二进制文件内容。
     */
    suspend fun writeBytes(workflowId: String, path: String, bytes: ByteArray, append: Boolean = false)

    /**
     * 获取当前工作流的文件沙箱目录绝对路径。
     */
    fun workingDirectory(workflowId: String): String

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

    /**
     * 将沙箱内的文件或目录压缩为 ZIP 文件。
     */
    suspend fun compressZip(workflowId: String, paths: List<String>, toPath: String)

    /**
     * 将 ZIP 文件安全解压至沙箱内目录。
     */
    suspend fun extractZip(workflowId: String, fromPath: String, toPath: String)

    companion object {
        val Default = DefaultActionWorkflowFileStorage()
    }
}

/**
 * 将 FileKit 的文件输出适配为 kmp-zip 所需的输出流。
 *
 * ZipOutputStream 关闭时由外层的 FileKit sink 统一释放，避免重复关闭。
 */
private class FileKitZipOutputStream(
    private val sink: RawSink,
) : no.synth.kmpzip.io.OutputStream() {
    override fun write(b: Int) = write(byteArrayOf(b.toByte()), 0, 1)

    override fun write(b: ByteArray, off: Int, len: Int) {
        if (len == 0) return
        val buffer = Buffer().apply { write(b, off, len) }
        sink.write(buffer, len.toLong())
    }

    override fun flush() = sink.flush()

    override fun close() = Unit
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
        if (!workflowId.matches(WORKFLOW_ID_PATTERN)) throw invalidWorkflowId(workflowId)
        if (path.isBlank() || PlatformFile(path).isAbsolute() || path.split('/', '\\').any { it == ".." }) {
            throw accessDenied(workflowId)
        }
        val sandbox = sandboxDir(workflowId)
        val target = sandbox / path
        requireInsideSandbox(target, sandbox, workflowId)
        return target
    }

    override suspend fun readText(workflowId: String, path: String): String = runFileOperation(workflowId) {
        val target = existingPath(workflowId, path)
        if (target.size() > MAX_READ_TEXT_BYTES) throw sizeExceeded(workflowId)
        target.readString()
    }

    override suspend fun writeText(workflowId: String, path: String, text: String, append: Boolean): Unit = runFileOperation(workflowId) {
        val target = resolveSandboxPath(workflowId, path)
        target.parent()?.createDirectories()
        if (append && target.exists()) target.writeString(target.readString() + text) else target.writeString(text)
    }

    override suspend fun createFile(workflowId: String, path: String): Unit = runFileOperation(workflowId) {
        val target = resolveSandboxPath(workflowId, path)
        if (!target.exists()) {
            target.parent()?.createDirectories()
            target.writeString("")
        }
    }

    override suspend fun writeBytes(workflowId: String, path: String, bytes: ByteArray, append: Boolean): Unit = runFileOperation(workflowId) {
        val target = resolveSandboxPath(workflowId, path)
        target.parent()?.createDirectories()
        val buffer = Buffer().apply { write(bytes) }
        target.sink(append).use { it.write(buffer, bytes.size.toLong()) }
    }

    override fun workingDirectory(workflowId: String): String {
        if (!workflowId.matches(WORKFLOW_ID_PATTERN)) throw invalidWorkflowId(workflowId)
        return sandboxDir(workflowId).absolutePath()
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
        if (!directory.isDirectory()) throw notDirectory(workflowId)
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

    override suspend fun compressZip(workflowId: String, paths: List<String>, toPath: String): Unit = runFileOperation(workflowId) {
        if (paths.isEmpty()) throw archiveInvalid(workflowId)
        val sources = paths.map { existingPath(workflowId, it) }
        val target = resolveSandboxPath(workflowId, toPath)
        target.parent()?.createDirectories()
        try {
            target.sink(append = false).use { sink ->
                ZipOutputStream(FileKitZipOutputStream(sink)).use { zip ->
                    var totalSize = 0L
                    sources.forEach { source ->
                        totalSize = writeZipEntries(zip, source, source.name, totalSize, workflowId)
                    }
                    zip.finish()
                }
            }
        } catch (throwable: Throwable) {
            runCatching { if (target.exists()) target.delete() }
            throw throwable
        }
    }

    override suspend fun extractZip(workflowId: String, fromPath: String, toPath: String): Unit = runFileOperation(workflowId) {
        val archive = existingPath(workflowId, fromPath)
        if (archive.isDirectory() || archive.size() > MAX_ARCHIVE_INPUT_BYTES) throw archiveLimitExceeded(workflowId)
        val outputDir = resolveSandboxPath(workflowId, toPath)
        outputDir.createDirectories()
        var entryCount = 0
        var totalSize = 0L
        ZipInputStream(ByteArrayInputStream(archive.readBytes())).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                entryCount += 1
                if (entryCount > MAX_ARCHIVE_ENTRY_COUNT || entry.size > MAX_ARCHIVE_ENTRY_BYTES) {
                    throw archiveLimitExceeded(workflowId)
                }
                val target = resolveArchiveEntryPath(workflowId, toPath, entry.name)
                if (entry.isDirectory) {
                    target.createDirectories()
                } else {
                    target.parent()?.createDirectories()
                    val entryOutput = ByteArrayOutputStream()
                    val buffer = ByteArray(BUFFER_SIZE)
                    var entrySize = 0L
                    while (true) {
                        val read = zip.read(buffer)
                        if (read <= 0) break
                        entrySize += read
                        totalSize += read
                        if (entrySize > MAX_ARCHIVE_ENTRY_BYTES || totalSize > MAX_ARCHIVE_EXTRACTED_BYTES) {
                            throw archiveLimitExceeded(workflowId)
                        }
                        entryOutput.write(buffer, 0, read)
                    }
                    target write entryOutput.toByteArray()
                }
                zip.closeEntry()
            }
        }
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

    private suspend fun writeZipEntries(
        zip: ZipOutputStream,
        source: PlatformFile,
        entryName: String,
        totalSize: Long,
        workflowId: String,
    ): Long {
        if (source.isDirectory()) {
            zip.putNextEntry(ZipEntry("${entryName.trimEnd('/')}/"))
            zip.closeEntry()
            return source.list().fold(totalSize) { size, child ->
                writeZipEntries(zip, child, "$entryName/${child.name}", size, workflowId)
            }
        }
        val nextSize = totalSize + source.size()
        if (nextSize > MAX_ARCHIVE_INPUT_BYTES) throw archiveLimitExceeded(workflowId)
        zip.putNextEntry(ZipEntry(entryName))
        source.source().use { input ->
            val buffer = Buffer()
            while (true) {
                val read = input.readAtMostTo(buffer, BUFFER_SIZE.toLong())
                if (read == -1L) break
                zip.write(buffer.readByteArray(read.toInt()))
            }
        }
        zip.closeEntry()
        return nextSize
    }

    private fun resolveArchiveEntryPath(workflowId: String, outputPath: String, entryName: String): PlatformFile {
        if (entryName.isBlank() || PlatformFile(entryName).isAbsolute() || entryName.split('/', '\\').any { it == ".." }) {
            throw archiveInvalid(workflowId)
        }
        return resolveSandboxPath(workflowId, "${outputPath.trimEnd('/', '\\')}/$entryName")
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

    private suspend fun <T> runFileOperation(workflowId: String, block: suspend () -> T): T = try {
        withContext(Dispatchers.IO) { block() }
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

    private fun invalidWorkflowId(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.WORKFLOW_ID_INVALID,
        messageText = ActionErrorCode.WORKFLOW_ID_INVALID_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.WORKFLOW_ID_INVALID_HINT,
    )

    private fun notFound(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.FILE_NOT_FOUND,
        messageText = ActionErrorCode.FILE_NOT_FOUND_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_NOT_FOUND_HINT,
    )

    private fun notDirectory(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.FILE_NOT_DIRECTORY,
        messageText = ActionErrorCode.FILE_NOT_DIRECTORY_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_NOT_DIRECTORY_HINT,
    )

    private fun ioFailed(workflowId: String, detail: String? = null) = ActionWorkflowException(
        code = ActionErrorCode.FILE_IO_FAILED,
        messageText = detail?.ifBlank { ActionErrorCode.FILE_IO_FAILED_MSG } ?: ActionErrorCode.FILE_IO_FAILED_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_IO_FAILED_HINT,
    )

    private fun sizeExceeded(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.FILE_SIZE_EXCEEDED,
        messageText = ActionErrorCode.FILE_SIZE_EXCEEDED_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_SIZE_EXCEEDED_HINT,
    )

    private fun archiveInvalid(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.FILE_ARCHIVE_INVALID,
        messageText = ActionErrorCode.FILE_ARCHIVE_INVALID_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_ARCHIVE_INVALID_HINT,
    )

    private fun archiveLimitExceeded(workflowId: String) = ActionWorkflowException(
        code = ActionErrorCode.FILE_ARCHIVE_LIMIT_EXCEEDED,
        messageText = ActionErrorCode.FILE_ARCHIVE_LIMIT_EXCEEDED_MSG,
        workflowId = workflowId,
        hint = ActionErrorCode.FILE_ARCHIVE_LIMIT_EXCEEDED_HINT,
    )

    private companion object {
        const val MAX_READ_TEXT_BYTES = 10 * 1024 * 1024L
        const val MAX_ARCHIVE_INPUT_BYTES = 1024 * 1024 * 1024L
        const val MAX_ARCHIVE_EXTRACTED_BYTES = 200 * 1024 * 1024L
        const val MAX_ARCHIVE_ENTRY_BYTES = 100 * 1024 * 1024L
        const val MAX_ARCHIVE_ENTRY_COUNT = 1_000
        const val BUFFER_SIZE = 256 * 1024
        val WORKFLOW_ID_PATTERN = Regex("[A-Za-z0-9_-]+")
    }
}
