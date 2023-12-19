package com.xiaoyv.bangumi.ui.feature.post.topic

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.post.BasePostActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.kts.getParcelObj


/**
 * Class: [PostTopicActivity]
 *
 * @author why
 * @since 12/8/23
 */
class PostTopicActivity : BasePostActivity<PostTopicViewModel>() {

    override val toolbarTitle: String
        get() = if (viewModel.isEditMode) "编辑话题内容" else "发表讨论话题"

    override val maxAttachSize: Int
        get() = 1

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        val mediaAttach = bundle.getParcelObj<PostAttach>(NavKey.KEY_PARCELABLE)
        if (mediaAttach != null) {
            viewModel.addAttach(mediaAttach)
        }

        // 编辑模式
        viewModel.isEditMode = bundle.getBoolean(NavKey.KEY_BOOLEAN, false)
        viewModel.targetEditId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        super.initView()

        // 发布话题不能设置的选项
        binding.tvPublic.isVisible = false

        // 讨论板提示弹窗
        if (viewModel.topicType == TopicType.TYPE_SUBJECT) {
            showTip()
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onPostTopicId.observe(this) {
            val topicId = it?.first.orEmpty()
            val success = topicId.isNotBlank()
            if (success) {
                RouteHelper.jumpTopicDetail(topicId, viewModel.topicType)
                finish()
                return@observe
            }

            showConfirmDialog(
                title = getString(CommonString.post_result_error),
                message = it?.second.orEmpty(),
                cancelText = null
            )
        }
    }
}