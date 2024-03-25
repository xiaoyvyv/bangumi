package com.xiaoyv.common.kts

import android.content.Context
import com.xiaoyv.widget.kts.getAttrColor

class FileExtension {

    /**
     * 视频
     */
    internal val videoExtensions = listOf(
        ".mp4", ".avi", ".mov", ".wmv", ".flv",
        ".mkv", ".mpg", ".mpeg", ".rm", ".rmvb",
        ".3gp", ".3g2", ".webm", ".ts", ".vob",
        ".m4v", ".ogg", ".ogv", ".m2ts", ".divx",
        ".dat", ".asf", ".m2v"
    )

    /**
     * 音频
     */
    internal val audioExtensions = listOf(
        ".mp3", ".wav", ".ogg", ".m4a", ".flac",
        ".aac", ".wma", ".aiff", ".ape", ".opus",
        ".amr", ".mid", ".midi", ".pcm", ".alac"
    )

    /**
     * 图片
     */
    internal val imageExtensions = listOf(
        ".jpg", ".jpeg", ".png", ".gif", ".bmp",
        ".tiff", ".webp", ".svg", ".ico", ".psd"
    )

    /**
     * 纯文本
     */
    internal val textFileExtensions = listOf(".txt", ".md", ".rtf", ".log", ".ini")

    /**
     * 编程代码文件扩展名列表
     */
    internal val codeFileExtensions = listOf(
        ".css", ".js", ".java", ".kt", ".c", ".html", ".htm",
        ".cpp", ".h", ".hpp", ".py", ".rb",
        ".php", ".xml", ".json", ".yaml", ".yml",
        ".ini", ".cfg", ".conf", ".sql", ".sh",
        ".bat", ".ps1", ".pl", ".asp", ".jsp",
        ".ejs", ".aspx", ".jsp", ".cs", ".vb",
        ".go", ".lua", ".swift", ".scala", ".perl"
    )

    /**
     * 办公文件扩展名
     */
    internal val officeFileExtensions = listOf(
        ".doc", ".docx", ".xls", ".xlsx", ".ppt",
        ".pptx", ".odt", ".ods", ".odp", ".pdf", ".csv"
    )

    /**
     * 安装文件
     */
    internal val installFileExtensions = listOf(
        ".exe", ".msi", ".dmg",
        ".pkg", ".deb", ".rpm"
    )

    /**
     * 压缩文件
     */
    internal val archiveFileExtensions = listOf(
        ".zip", ".rar", ".7z", ".tar",
        ".gz", ".gzip", ".bz2", ".bzip2",
        ".tar.gz", ".tar.bz2"
    )
}

/**
 * 加载扩展名对应的图标
 *
 * @return 图标ID - 颜色
 */
fun FileExtension.loadFileIconByExtension(context: Context, extension: String): Pair<Int, Int> {
    val element = extension.lowercase()

    return when {
        videoExtensions.contains(element) -> {
            CommonDrawable.ic_file_video to context.getColor(CommonColor.file_video)
        }

        imageExtensions.contains(element) -> {
            CommonDrawable.ic_file_image to context.getColor(CommonColor.file_image)
        }

        audioExtensions.contains(element) -> {
            CommonDrawable.ic_file_audio to context.getColor(CommonColor.file_audio)
        }

        archiveFileExtensions.contains(element) -> {
            CommonDrawable.ic_file_archive to context.getColor(CommonColor.file_zip)
        }

        textFileExtensions.contains(element) -> {
            CommonDrawable.ic_file_txt to context.getColor(CommonColor.file_txt)
        }

        codeFileExtensions.contains(element) -> {
            CommonDrawable.ic_file_code to context.getColor(CommonColor.file_code)
        }

        officeFileExtensions.contains(element) -> {
            CommonDrawable.ic_file_office to context.getColor(CommonColor.file_office)
        }

        installFileExtensions.contains(element) -> {
            CommonDrawable.ic_file_install to context.getColor(CommonColor.file_install)
        }

        else -> {
            CommonDrawable.ic_file to context.getAttrColor(GoogleAttr.colorOnSurfaceVariant)
        }
    }
}