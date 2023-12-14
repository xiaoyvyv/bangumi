package com.xiaoyv.bangumi.ui.discover.blog.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityBlogBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.reply.ReplyDialog
import com.xiaoyv.common.widget.web.page.BlogView
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [BlogActivity]
 *
 * @author why
 * @since 11/24/23
 */
class BlogActivity : BaseViewModelActivity<ActivityBlogBinding, BlogViewModel>() {
    private val blogWeb by lazy {
        BlogView(binding.webView)
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.blogId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        blogWeb.startLoad()
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 60.dpi) {
                binding.toolbar.title = viewModel.onBlogDetailLiveData.value?.title
            } else {
                binding.toolbar.title = null
            }
        }

        blogWeb.onPreviewImageListener = { imageUrl, imageUrls ->
            RouteHelper.jumpPreviewImage(imageUrl, imageUrls)
        }

        blogWeb.onReplyUserListener = { replyJs, formEntity ->
            val replyForm = viewModel.onBlogDetailLiveData.value?.replyForm
            if (replyForm != null && replyForm.isEmpty.not()) {
                ReplyDialog.show(supportFragmentManager, replyForm, replyJs, formEntity) {
                    launchUI { blogWeb.addComment(it) }
                }
            } else {
                RouteHelper.jumpLogin()
            }
        }

        blogWeb.onReplyNewListener = {
            val replyForm = viewModel.onBlogDetailLiveData.value?.replyForm
            if (replyForm != null && replyForm.isEmpty.not()) {
                ReplyDialog.show(supportFragmentManager, replyForm, null, null) {
                    launchUI { blogWeb.addComment(it) }
                }
            } else {
                RouteHelper.jumpLogin()
            }
        }

        blogWeb.onNeedLoginListener = {
            RouteHelper.jumpLogin()
        }

        blogWeb.onClickUserListener = {
            RouteHelper.jumpUserDetail(it)
        }

        blogWeb.onClickRelatedListener = {
            RouteHelper.handleUrl(it.titleLink)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowContent = { false }
        )

        viewModel.onBlogDetailLiveData.observe(this) {
            launchUI {
                blogWeb.loadBlogDetail(it)
                binding.stateView.showContent()
            }

            invalidateMenu()
        }

        viewModel.onDeleteResult.observe(this) {
            finish()
        }

        UserHelper.observeUserInfo(this) {
            viewModel.queryBlogDetail()
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.isMine) {
            menu.add("编辑")
                .setOnMenuItemClickListener {
                    true
                }

            menu.add("删除")
                .setOnMenuItemClickListener {
                    showConfirmDialog(
                        message = "是否删除该日志？",
                        onConfirmClick = {
                            viewModel.deleteBlog()
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

    override fun onDestroy() {
        super.onDestroy()
        binding.webView.destroy()
    }
}