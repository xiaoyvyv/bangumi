package com.xiaoyv.bangumi.ui.discover.blog.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityBlogBinding
import com.xiaoyv.bangumi.helper.CommentHelper
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.addCommonMenu
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.web.page.BlogView
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.errorMsg
import com.xiaoyv.widget.kts.toast

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
        viewModel.anchorCommentId = bundle.getString(NavKey.KEY_STRING_SECOND)

        // NewIntent 时刷新
        if (isNewIntent) {
            viewModel.queryBlogDetail()
        }
    }

    override fun initView() {
        blogWeb.startLoad()
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY > 60.dpi) {
                binding.toolbar.title = viewModel.onBlogDetailLiveData.value?.title
            } else {
                binding.toolbar.title = null
            }

            if (scrollY - oldScrollY > 0) {
                binding.fabComment.hide()
            } else {
                binding.fabComment.show()
            }
        }

        blogWeb.onPreviewImageListener = { imageUrl, imageUrls ->
            RouteHelper.jumpPreviewImage(imageUrl, imageUrls)
        }

        blogWeb.onReplyUserListener = { replyJs, targetComment ->
            CommentHelper.showCommentDialog(
                activity = requireActivity,
                replyForm = viewModel.replyForm,
                replyJs = replyJs,
                targetComment = targetComment,
                onReplyListener = { blogWeb.addComment(it) }
            )
        }

        blogWeb.onReplyNewListener = {
            CommentHelper.showCommentDialog(
                activity = requireActivity,
                replyForm = viewModel.replyForm,
                onReplyListener = { blogWeb.addComment(it) }
            )
        }

        binding.fabComment.setOnFastLimitClickListener {
            blogWeb.onReplyNewListener.invoke()
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
            launchUI(error = { toast(it.errorMsg) }) {
                blogWeb.loadBlogDetail(it)
                binding.stateView.showContent()
            }

            invalidateMenu()
        }

        viewModel.onDeleteResult.observe(this) {
            finish()
        }

        viewModel.isCollected.observe(this) {
            invalidateMenu()
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
                    RouteHelper.jumpEditBlog(viewModel.blogId)
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

        // 收藏菜单
        if (viewModel.onBlogDetailLiveData.value != null) menu.add("收藏")
            .setIcon(if (viewModel.isCollected.value == true) CommonDrawable.ic_star else CommonDrawable.ic_star_empty)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                viewModel.toggleCollection()
                true
            }

        // 公共菜单
        menu.addCommonMenu(BgmApiManager.buildReferer(BgmPathType.TYPE_BLOG, viewModel.blogId))

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