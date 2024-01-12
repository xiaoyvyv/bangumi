package com.xiaoyv.bangumi.ui.media.detail.preview

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.blankj.utilcode.util.DebouncingUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.response.GalleryEntity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [MediaPreviewActivity]
 *
 * @author why
 * @since 1/12/24
 */
class MediaPreviewActivity : BaseListActivity<GalleryEntity, MediaPreviewViewModel>() {

    override val toolbarTitle: String
        get() = "条目预览"

    override val isOnlyOnePage: Boolean
        get() = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.douBanPhotoId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.mediaName = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
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
        return MediaPreviewAdapter()
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Help")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showConfirmDialog(message = "数据来源：douban.com", cancelText = null)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}