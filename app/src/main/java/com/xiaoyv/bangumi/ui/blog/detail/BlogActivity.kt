package com.xiaoyv.bangumi.ui.blog.detail

import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityBlogBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [BlogActivity]
 *
 * @author why
 * @since 11/24/23
 */
class BlogActivity : BaseViewModelActivity<ActivityBlogBinding, BlogViewModel>() {

    override fun initView() {
        enableEdgeToEdge()

    }

    override fun initData() {

    }

    override fun initListener() {

    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun LifecycleOwner.initViewObserver() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}