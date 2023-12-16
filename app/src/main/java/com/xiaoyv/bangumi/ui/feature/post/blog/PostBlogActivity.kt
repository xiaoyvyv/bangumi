package com.xiaoyv.bangumi.ui.feature.post.blog

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.bangumi.ui.feature.post.BasePostActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.PostAttach
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [PostBlogActivity]
 *
 * @author why
 * @since 12/2/23
 */
class PostBlogActivity : BasePostActivity<PostBlogViewModel>() {

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        val mediaAttach = bundle.getParcelObj<PostAttach>(NavKey.KEY_PARCELABLE)
        if (mediaAttach != null) {
            viewModel.addAttach(mediaAttach)
        }

        // 编辑模式
        viewModel.isEditMode = bundle.getBoolean(NavKey.KEY_BOOLEAN, false)
        viewModel.editModeBlogId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onPostBlogId.observe(this) {
            val blogId = it?.first.orEmpty()
            val success = blogId.isNotBlank()
            if (success) {
                RouteHelper.jumpBlogDetail(blogId)
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