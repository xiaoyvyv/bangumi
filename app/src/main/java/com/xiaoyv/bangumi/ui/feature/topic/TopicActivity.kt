package com.xiaoyv.bangumi.ui.feature.topic

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityTopicBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.response.ReplyResultEntity
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.fromJson
import com.xiaoyv.common.kts.initNavBack
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
    private val mock: ReplyResultEntity
        get() {
            return ("{\n" +
                    "  \"posts\": {\n" +
                    "    \"main\": {\n" +
                    "      \"200106\": {\n" +
                    "        \"pst_id\": \"200106\",\n" +
                    "        \"pst_mid\": \"328262\",\n" +
                    "        \"pst_uid\": \"837364\",\n" +
                    "        \"pst_content\": \"评论开发测试：文字内容 &nbsp; &nbsp; &nbsp; &nbsp; 快捷键 &nbsp; &nbsp; &nbsp; &nbsp; 显示效果<br />\\n正常文字 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n正常文字<br />\\n我是 <span style=\\\"font-weight:bold;\\\">粗体字</span> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+B &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n我是粗体字<br />\\n我是<span style=\\\"font-style:italic\\\">斜体字</span> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+I &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n我是斜体字<br />\\n我是<span style=\\\"text-decoration: underline;\\\">下划线文字</span> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+U &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n我是下划线文字<br />\\n我是<span style=\\\"text-decoration: line-through;\\\">删除线文字</span> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+D &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n我是删除线文字<br />\\n我是<span class=\\\"text_mask\\\" style=\\\"background-color:#555;color:#555;border:1px solid #555;\\\">马赛克文字</span> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+M &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n我是马赛克文字<br />\\n我是<br />\\n<span style=\\\"color: red;\\\">彩</span><span style=\\\"color: green;\\\">色</span><span style=\\\"color: blue;\\\">的</span><span style=\\\"color: orange;\\\">哟</span>。 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n我是<br />\\n彩色的哟。<br />\\n<span style=\\\"font-size:10px; line-height:10px;\\\">不同</span><span style=\\\"font-size:14px; line-height:14px;\\\">大小的</span><span style=\\\"font-size:18px; line-height:18px;\\\">文字</span>效果也可实现。 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n不同大小的文字效果也可实现。<br />\\nBangumi 番组计划: <a href=\\\"http://bgm.tv/\\\" target=\\\"_blank\\\" rel=\\\"nofollow external noopener noreferrer\\\" class=\\\"l\\\">http://bgm.tv/</a> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+L &nbsp; &nbsp; &nbsp; &nbsp; <br />\\nBangumi 番组计划: <a href=\\\"http://bgm.tv/\\\" target=\\\"_blank\\\" rel=\\\"nofollow external noopener noreferrer\\\" class=\\\"l\\\">http://bgm.tv/</a><br />\\n带文字说明的网站链接：<br />\\n<a href=\\\"http://bgm.tv\\\" target=\\\"_blank\\\" rel=\\\"nofollow external noopener noreferrer\\\" class=\\\"l\\\">Bangumi 番组计划</a> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+L &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n带文字说明的网站链接：<br />\\nBangumi 番组计划<br />\\n存放于其他网络服务器的图片：<br />\\n<img src=\\\"http://chii.in/img/ico/bgm88-31.gif\\\" class=\\\"code\\\" rel=\\\"noreferrer\\\" referrerpolicy=\\\"no-referrer\\\" alt=\\\"\\\" /> &nbsp; &nbsp; &nbsp; &nbsp; Ctrl+P &nbsp; &nbsp; &nbsp; &nbsp; <br />\\n存放于其他网络服务器的图片：\",\n" +
                    "        \"username\": \"837364\",\n" +
                    "        \"nickname\": \"xiaoyv\",\n" +
                    "        \"sign\": \"(hello world)\",\n" +
                    "        \"avatar\": \"//lain.bgm.tv/pic/user/l/000/83/73/837364.jpg?r=1702195217&hd=1\",\n" +
                    "        \"dateline\": \"2023-12-13 23:14\",\n" +
                    "        \"model\": \"blog\",\n" +
                    "        \"is_self\": true\n" +
                    "      }\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"timestamp\": 1702541683,\n" +
                    "  \"status\": \"ok\"\n" +
                    "}").fromJson()!!
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
        }

        UserHelper.observeUserInfo(this) {
            viewModel.queryTopicDetail()
        }
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