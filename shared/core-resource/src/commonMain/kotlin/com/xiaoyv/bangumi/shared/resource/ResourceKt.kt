package com.xiaoyv.bangumi.shared.resource

import com.xiaoyv.bangumi.core_resource.resources.Res
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.parent
import io.github.vinceglb.filekit.size
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * 将 [resourcePath] 指定的 Resource 文件复制到内部指定目标文件 [targetFile]。
 * 若目标文件已存在且长度与 Resource 数据一致，则跳过复制。
 *
 * @param resourcePath 资源文件相对路径，如 "files/live2d/model.zip"
 * @param targetFile 目标 [PlatformFile]
 * @return 目标文件 [PlatformFile]
 */
suspend fun Res.copyTo(
    resourcePath: String,
    targetFile: PlatformFile
): PlatformFile = withContext(Dispatchers.IO) {
    val bytes = readBytes(resourcePath)
    if (targetFile.exists() && targetFile.size() == bytes.size.toLong()) {
        return@withContext targetFile
    }
    targetFile.parent()?.createDirectories()
    targetFile.write(bytes)
    targetFile
}

/**
 * 将 [resourcePath] 指定的 Resource 文件复制到内部指定目录 [targetDir] 下。
 * 若目标文件已存在且长度与 Resource 数据一致，则跳过复制。
 *
 * @param resourcePath 资源文件相对路径，如 "files/live2d/model.zip"
 * @param targetDir 目标目录 [PlatformFile]
 * @return 目标文件 [PlatformFile]
 */
suspend fun Res.copyToDir(
    resourcePath: String,
    targetDir: PlatformFile
): PlatformFile {
    val fileName = resourcePath.substringAfterLast('/')
    val targetFile = targetDir / fileName
    return copyTo(resourcePath, targetFile)
}
