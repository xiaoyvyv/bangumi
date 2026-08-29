package com.xiaoyv.bangumi.shared.data.repository

import com.xiaoyv.bangumi.shared.core.types.settings.SettingUpdateChannel
import com.xiaoyv.bangumi.shared.data.model.response.bgm.ComposeUploadImage
import com.xiaoyv.bangumi.shared.data.model.response.chore.ComposeAppRelease
import com.xiaoyv.bangumi.shared.data.model.response.chore.ComposeBangumiStatus
import io.github.vinceglb.filekit.PlatformFile

interface ChoreRepository {
    suspend fun fetchDns(hostname: String): Result<Pair<String, List<String>>>

    suspend fun fetchPictureFileByUrl(url: String): Result<PlatformFile>

    suspend fun compressImageAndUpload(file: PlatformFile): Result<ComposeUploadImage>

    suspend fun compressImage(file: PlatformFile): Result<PlatformFile>

    suspend fun uploadImage(file: PlatformFile): Result<ComposeUploadImage>

    suspend fun translate(text: String, isHtml: Boolean): Result<String>

    suspend fun fetchBangumiStatus(): Result<ComposeBangumiStatus>

    /**
     * 根据更新渠道获取应用发布信息。
     *
     * @param channel 更新渠道
     */
    suspend fun fetchAppRelease(@SettingUpdateChannel channel: Int): Result<ComposeAppRelease>
}
