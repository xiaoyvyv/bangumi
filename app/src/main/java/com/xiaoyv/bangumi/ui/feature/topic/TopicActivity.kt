package com.xiaoyv.bangumi.ui.feature.topic

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityTopicBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.common.widget.reply.ReplyDialog
import com.xiaoyv.common.widget.web.page.TopicView
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.dpi

/**
 * Class: [TopicActivity]
 *
 * @author why
 * @since 12/2/23
 */
class TopicActivity : BaseViewModelActivity<ActivityTopicBinding, TopicViewModel>() {
    private val topicView by lazy {
        TopicView(binding.webView)
    }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.topicId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.topicType = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
    }

    override fun initView() {
        topicView.startLoad()
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.webView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 60.dpi) {
                binding.toolbar.title = viewModel.onTopicDetailLiveData.value?.title
            } else {
                binding.toolbar.title = null
            }
        }

        topicView.onPreviewImageListener = { imageUrl, imageUrls ->
            RouteHelper.jumpPreviewImage(imageUrl, imageUrls)
        }

        topicView.onReplyUserListener = { replyJs, formEntity ->
            if (UserHelper.isLogin.not()) RouteHelper.jumpLogin()

            val replyForm = viewModel.onTopicDetailLiveData.value?.replyForm
            if (replyForm != null && replyForm.isEmpty.not()) {
                ReplyDialog.show(supportFragmentManager, replyForm, replyJs, formEntity) {
                    launchUI { topicView.addComment(it) }
                }
            }
        }

        topicView.onReplyNewListener = {
            if (UserHelper.isLogin.not()) RouteHelper.jumpLogin()

            val replyForm = viewModel.onTopicDetailLiveData.value?.replyForm
            if (replyForm != null && replyForm.isEmpty.not()) {
                ReplyDialog.show(supportFragmentManager, replyForm, null, null) {
                    launchUI { topicView.addComment(it) }
                }
            }
        }

        topicView.onNeedLoginListener = {
            RouteHelper.jumpLogin()
        }

        topicView.onClickUserListener = {
            RouteHelper.jumpUserDetail(it)
        }

        topicView.onClickRelatedListener = {
            RouteHelper.handleUrl(it.titleLink)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            canShowContent = { false }
        )

        viewModel.onTopicDetailLiveData.observe(this) {
            launchUI {
                topicView.loadTopicDetail(it)
                binding.stateView.showContent()
            }

            invalidateMenu()
        }

        viewModel.onDeleteResult.observe(this) {
            finish()
        }

        UserHelper.observeUserInfo(this) {
            viewModel.queryTopicDetail()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (viewModel.isMine) {
            menu.add("编辑")
                .setOnMenuItemClickListener {
                    RouteHelper.jumpEditTopic(viewModel.topicId)
                    true
                }

            menu.add("删除")
                .setOnMenuItemClickListener {
                    showConfirmDialog(
                        message = "是否删除该话题？",
                        onConfirmClick = {
                            viewModel.deleteTopic()
                        }
                    )
                    true
                }
        }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
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