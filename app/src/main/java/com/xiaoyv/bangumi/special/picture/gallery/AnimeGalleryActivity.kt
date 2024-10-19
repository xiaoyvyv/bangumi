package com.xiaoyv.bangumi.special.picture.gallery

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.DebouncingUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.dialog.UiDialog

/**
 * AnimeGalleryActivity
 *
 * @author why
 * @since 11/19/23
 */
class AnimeGalleryActivity : BaseListActivity<GalleryEntity, AnimeGalleryViewModel>() {
    override fun initBarConfig() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .statusBarDarkFont(true)
            .init()
    }

    override val toolbarTitle: String
        get() = if (viewModel.isPreviewSubject) i18n(CommonString.anime_gallery_subject)
        else i18n(CommonString.anime_gallery_ap)

    override val isOnlyOnePage: Boolean
        get() = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.douBanPhotoId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initData() {
        super.initData()
        contentAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn)

        // 移除滑动停止加载图片
        binding.rvContent.removeImageScrollLoadController()
    }

    override fun initListener() {
        super.initListener()

        // 单击预览
        contentAdapter.addOnItemChildClickListener(R.id.iv_image) { adapter, v, position ->
            val item = adapter.getItem(position) ?: return@addOnItemChildClickListener
            if (DebouncingUtils.isValid(v)) {
                RouteHelper.jumpPreviewImage(item.largeImageUrl)
            }
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<GalleryEntity, *> {
        return AnimeGalleryAdapter(viewModel.isPreviewSubject)
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        val spanCount = if (viewModel.isPreviewSubject) 1 else 2
        return QuickGridLayoutManager(activity, spanCount, GridLayoutManager.VERTICAL, false)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!viewModel.isPreviewSubject) menu.add(i18n(CommonString.common_help))
            .setIcon(CommonDrawable.ic_notifications)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = i18n(CommonString.anime_gallery_about),
                    cancelText = null
                )
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}