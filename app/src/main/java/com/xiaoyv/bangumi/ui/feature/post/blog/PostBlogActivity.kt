package com.xiaoyv.bangumi.ui.feature.post.blog

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.databinding.ActivityPostBlogBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.web.page.BlogPostView
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [PostBlogActivity]
 *
 * @author why
 * @since 12/2/23
 */
class PostBlogActivity : BaseViewModelActivity<ActivityPostBlogBinding, PostBlogViewModel>() {

    private val blogPostWeb by lazy {
        BlogPostView(binding.webView)
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.mediaEntity = bundle.getParcelObj(NavKey.KEY_PARCELABLE)
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        blogPostWeb.startLoad()

        viewModel.queryPostFormHash(blogPostWeb)
    }

    override fun initListener() {
        blogPostWeb.onClickRelatedListener = { related, isAddRelated ->
            debugLog { "isAddRelated: $isAddRelated, related: ${related.toJson(true)}" }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            interceptShowContent = true
        )

        viewModel.onCreateEntity.observe(this) {
            launchUI {
                blogPostWeb.setPostInfo(it)
                binding.stateView.showContent()
            }
        }

        viewModel.onPostBlogId.observe(this) {
            val blogId = it?.first.orEmpty()
            val success = blogId.isNotBlank()
            if (success) {
                RouteHelper.jumpBlogDetail(blogId)
                finish()
                return@observe
            }

            // 刷新
            viewModel.queryPostFormHash(blogPostWeb)

            MaterialAlertDialogBuilder(activity)
                .setTitle(getString(CommonString.post_result_error))
                .setMessage(it?.second.orEmpty())
                .setPositiveButton(getString(CommonString.post_result_known), null)
                .create()
                .show()
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("发送")
            .setIcon(CommonDrawable.ic_send)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                viewModel.sendPost(blogPostWeb)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}