package com.xiaoyv.bangumi.ui.discover.blog.detail

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityBlogBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.blueprint.kts.toJson
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.web.page.BlogView
import com.xiaoyv.common.widget.reply.ReplyDialog
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.stateview.StateViewLiveData

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

        setSupportActionBar(binding.toolbar)
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        viewModel.queryBlogDetail()
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
                ReplyDialog.show(supportFragmentManager, replyForm, replyJs, formEntity)
            } else {
                RouteHelper.jumpLogin()
            }
        }

        blogWeb.onReplyNewListener = {
            val replyForm = viewModel.onBlogDetailLiveData.value?.replyForm
            if (replyForm != null && replyForm.isEmpty.not()) {
                ReplyDialog.show(supportFragmentManager, replyForm, null, null)
            } else {
                RouteHelper.jumpLogin()
            }
        }

        blogWeb.onNeedLoginListener = {
            RouteHelper.jumpLogin()
        }

        blogWeb.onClickRelatedListener = {
            RouteHelper.jumpMediaDetail(it.id)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onBlogDetailLiveData.observe(this) {
            debugLog { it.toJson(true) }

            launchUI {
                blogWeb.loadBlogDetail(it)
                binding.pbProgress.hide()
            }
        }

        viewModel.loadingViewState.observe(this) {
            when (it.type) {
                StateViewLiveData.StateType.STATE_LOADING -> {
                    binding.pbProgress.show()
                }

                StateViewLiveData.StateType.STATE_TIPS -> {
                    binding.pbProgress.hide()
                }
            }
        }

        UserHelper.observe(this) {
            if (!it.isEmpty) {
                viewModel.queryBlogDetail()
            }
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}