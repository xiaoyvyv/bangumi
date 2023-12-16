package com.xiaoyv.bangumi.ui.feature.post.topic

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.xiaoyv.bangumi.ui.feature.post.BasePostActivity
import com.xiaoyv.blueprint.constant.NavKey


/**
 * Class: [PostTopicActivity]
 *
 * @author why
 * @since 12/8/23
 */
class PostTopicActivity : BasePostActivity<PostTopicViewModel>() {

    override val toolbarTitle: String
        get() = if (viewModel.isEditMode) "编辑话题内容" else "发表讨论话题"

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.targetId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.groupOrMedia = bundle.getBoolean(NavKey.KEY_BOOLEAN)
    }

    override fun initView() {
        super.initView()

        // 发布话题不能设置的选项
        binding.tvMedia.isVisible = false
        binding.tvPublic.isVisible = false
    }
}