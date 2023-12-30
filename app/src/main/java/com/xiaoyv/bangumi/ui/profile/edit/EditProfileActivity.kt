package com.xiaoyv.bangumi.ui.profile.edit

import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityEditProfileBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.config.annotation.FormInputType
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showInputDialog
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

    /**
     * 图片选取
     */
    private val changeAvatarLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let { uri -> viewModel.compressAndCropAvatar(uri) }
        }

    override fun initView() {
        enableEdgeToEdge()

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

        contentAdapter.setOnDebouncedChildClickListener(R.id.option_item) {
            when (it.type) {
                FormInputType.TYPE_FILE -> {
                    viewModel.selectFileItem = it
                    changeAvatarLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }

                FormInputType.TYPE_INPUT -> {
                    showInputDialog(
                        title = it.title,
                        default = it.value,
                        onInput = { input ->
                            it.value = input
                            viewModel.refreshOptionItem(it)
                        }
                    )
                }

                FormInputType.TYPE_SELECT -> {
                    val options = it.options
                    val item = options.map { item -> item.title }.toTypedArray()
                    MaterialAlertDialogBuilder(activity)
                        .setTitle(it.title)
                        .setItems(item) { _, position ->
                            options.onEach { select -> select.isSelected = false }
                            options[position].isSelected = true
                            it.value = options[position].value
                            it.options = options
                            viewModel.refreshOptionItem(it)
                        }
                        .create()
                        .show()
                }
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onEditOptionLiveData.observe(this) {
            contentAdapter.submitList(viewModel.optItemSort(it.orEmpty()))
        }

        viewModel.onActionResultLiveData.observe(this) {
            MaterialAlertDialogBuilder(activity)
                .setTitle("Tips")
                .setMessage(it.orEmpty())
                .setPositiveButton("知道了", null)
                .create()
                .show()

            viewModel.queryUserInfo()
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Finish")
            .setIcon(CommonDrawable.ic_save)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                viewModel.updateSettings()
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}