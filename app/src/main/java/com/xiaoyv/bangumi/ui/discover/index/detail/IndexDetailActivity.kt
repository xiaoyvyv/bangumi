package com.xiaoyv.bangumi.ui.discover.index.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityIndexDetailBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [IndexDetailActivity]
 *
 * @author why
 * @since 12/12/23
 */
class IndexDetailActivity :
    BaseViewModelActivity<ActivityIndexDetailBinding, IndexDetailViewModel>() {

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.indexId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowContent = { false }
        )

        viewModel.onIndexDetailLiveData.observe(this) {
            debugLog { "Index: "+it.toJson(true) }
            launchUI {
//                blogWeb.loadBlogDetail(it)
                binding.stateView.showContent()
            }

            invalidateMenu()
        }

        viewModel.onDeleteResult.observe(this) {
            finish()
        }

        UserHelper.observeUserInfo(this) {
            viewModel.queryIndexDetail()
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.isMine) {
            menu.add("修改")
                .setOnMenuItemClickListener {
                    true
                }

            menu.add("删除")
                .setOnMenuItemClickListener {
                    showConfirmDialog(
                        message = "删除操作将抹掉所有关联数据以及用户留言，是否要继续？",
                        onConfirmClick = {
                            viewModel.deleteIndex()
                        }
                    )
                    true
                }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}