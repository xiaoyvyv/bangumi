package com.xiaoyv.bangumi.ui.profile.edit

import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivityEditProfileBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [EditProfileActivity]
 *
 * @author why
 * @since 11/24/23
 */
class EditProfileActivity :
    BaseViewModelActivity<ActivityEditProfileBinding, EditProfileViewModel>() {
    private val contentAdapter by lazy { EditProfileAdapter() }

    override fun initView() {
        enableEdgeToEdge()

        setSupportActionBar(binding.toolbar)
        binding.toolbar.initNavBack(this)

        binding.rvContent.adapter = contentAdapter

        binding.srlRefresh.initRefresh { true }
        binding.srlRefresh.setColorSchemeColors(getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.srlRefresh.setOnRefreshListener {
            viewModel.queryUserInfo()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onEditOptionLiveData.observe(this) {
            contentAdapter.submitList(it.orEmpty())
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