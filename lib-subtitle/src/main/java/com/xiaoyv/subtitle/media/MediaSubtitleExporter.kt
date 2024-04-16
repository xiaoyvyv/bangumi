package com.xiaoyv.subtitle.media

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

class MediaSubtitleExporter private constructor(
    private val context: Context,
    private val uri: Uri
) {
    private val retriever = MediaMetadataRetriever()

    fun extractSubtitle() {
        retriever.setDataSource(context, uri)
//        val metadata: Map<String, String> =
//            retriever.get(MediaMetadataRetriever.METADATA_KEY_ALBUM)


    }

    companion object {
        fun from(context: Context, uri: Uri): MediaSubtitleExporter {
            return MediaSubtitleExporter(context, uri)
        }
    }
}