package com.xiaoyv.bangumi.special.picture.gallery

import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.DebouncingUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.api.response.ImageGalleryEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi

/**
 * AnimeGalleryActivity
 *
 * @author why
 * @since 11/19/23
 */
class AnimeGalleryActivity : BaseListActivity<ImageGalleryEntity.Post, AnimeGalleryViewModel>() {
    override fun initBarConfig() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .transparentNavigationBar()
            .statusBarDarkFont(true)
            .init()
    }

    override val toolbarTitle: String
        get() = "Anime-Pictures"

    override val isOnlyOnePage: Boolean
        get() = false

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<ImageGalleryEntity.Post, *> {
        return AnimeGalleryAdapter()
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return QuickGridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
    }

    override fun initView() {
        super.initView()
        val offsetStart = BarUtils.getStatusBarHeight()
        binding.srlRefresh.setProgressViewOffset(true, offsetStart, offsetStart + 40.dpi)
    }

    override fun initData() {
        super.initData()
        contentAdapter.setItemAnimation(BaseQuickAdapter.AnimationType.ScaleIn)
    }

    override fun initListener() {
        super.initListener()

        // 单击预览
        contentAdapter.addOnItemChildClickListener(R.id.iv_image) { adapter, v, position ->
            val item = adapter.getItem(position) ?: return@addOnItemChildClickListener
            if (DebouncingUtils.isValid(v)) {
                RouteHelper.jumpPreviewImage(item.largeUrl.orEmpty())
            }
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Help")
            .setIcon(CommonDrawable.ic_notifications)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = "列表显示的缩略图，滑动停止时才会开始加载图片，点击可以查看大图。\n\n数据来源：anime-pictures.net",
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