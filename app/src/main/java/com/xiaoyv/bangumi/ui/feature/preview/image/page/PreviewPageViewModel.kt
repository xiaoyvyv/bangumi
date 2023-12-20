package com.xiaoyv.bangumi.ui.feature.preview.image.page

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.IntentUtils
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.blueprint.kts.launchIO
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.requireStoragePermission

/**
 * Class: [PreviewPageViewModel]
 *
 * @author why
 * @since 12/20/23
 */
class PreviewPageViewModel : BaseViewModel() {
    internal var imageUrl: String = ""
    internal var position: Int = 0
    internal var total = 0

    internal val isLoadNormal
        get() = imageUrl.endsWith(".gif", false)
                || imageUrl.startsWith("file:///android_asset")

    internal val shareMenus by lazy {
        arrayOf(
            StringUtils.getString(CommonString.anime_gallery_action_save),
            StringUtils.getString(CommonString.anime_gallery_action_share),
            StringUtils.getString(CommonString.anime_gallery_action_wallpaper)
        )
    }

    /**
     * 下载
     */
    fun downloadWallpaperFromUrl(item: String, action: String) {
        launchIO(
            state = loadingDialogState(cancelable = false),
            error = {
                it.printStackTrace()
            },
            block = {
                val wallpaperFile = Glide.with(Utils.getApp())
                    .asFile()
                    .load(item)
                    .submit()
                    .get()

                when (shareMenus.indexOf(action)) {
                    0 -> {
                        val saveImpl = {
                            ImageUtils.save2Album(
                                BitmapFactory.decodeFile(wallpaperFile.absolutePath),
                                Bitmap.CompressFormat.PNG
                            )
                        }
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            if (requireStoragePermission()) {
                                saveImpl()
                            }
                        } else {
                            saveImpl()
                        }
                    }

                    1 -> {
                        ActivityUtils.startActivity(IntentUtils.getShareImageIntent(wallpaperFile))
                    }

                    2 -> {
                        val wallpaperManager = WallpaperManager.getInstance(Utils.getApp())
                        val wallpaperBitmap = BitmapFactory.decodeFile(wallpaperFile.absolutePath)
                        wallpaperManager.setBitmap(
                            wallpaperBitmap,
                            null,
                            true,
                            WallpaperManager.FLAG_SYSTEM
                        )
                    }
                }
            }
        )
    }

}