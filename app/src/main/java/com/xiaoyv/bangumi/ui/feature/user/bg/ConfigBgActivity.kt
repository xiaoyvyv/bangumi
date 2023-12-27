package com.xiaoyv.bangumi.ui.feature.user.bg

import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityUserBgBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [ConfigBgActivity]
 *
 * @author why
 * @since 12/27/23
 */
class ConfigBgActivity : BaseViewModelActivity<ActivityUserBgBinding, ConfigBgViewModel>() {

    private val configBgAdapter by lazy { ConfigBgAdapter() }

    /**
     * 手动选取图片
     */
    private val selectLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.let { it1 -> viewModel.handleImagePicture(it1) }
        }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.rvImage.hasFixedSize()
        binding.rvImage.adapter = configBgAdapter
    }

    override fun initListener() {
        binding.fabRandom.setOnFastLimitClickListener {
            viewModel.randomImage()
        }

        configBgAdapter.setOnDebouncedChildClickListener(R.id.item_image) {
            viewModel.cacheImageLink.value = it.largeImageUrl
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onImageListLiveData.observe(this) {
            configBgAdapter.submitList(it.orEmpty())
        }

        viewModel.cacheImageLink.observe(this) {
            binding.ivBanner.loadImageAnimate(it)
        }

        UserHelper.observeUserInfo(this) {
            loadUserBg()

            binding.ivAvatar.loadImageAnimate(it.avatar?.large)
            binding.tvEmail.text = buildString {
                append(it.nickname)
                append("@")
                append(it.id)
            }
        }
    }

    /**
     * 加载背景
     */
    private fun loadUserBg() {
        if (ConfigHelper.userBackground.isNotBlank()) {
            binding.ivBanner.loadImageAnimate(ConfigHelper.userBackground)
        } else {
            binding.ivBanner.loadImageBlur(UserHelper.currentUser.avatar?.large)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Save")
            .setIcon(CommonDrawable.ic_save)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                if (viewModel.cacheImageLink.value.isNullOrBlank().not()) {
                    ConfigHelper.userBackground = viewModel.cacheImageLink.value.orEmpty()
                    finish()
                }
                true
            }

        menu.add("本地图片")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                selectLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                true
            }

        menu.add("网络图片")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showInputDialog(
                    title = "输入图片链接",
                    inputHint = "输入网络图片链接",
                    onInput = {
                        viewModel.cacheImageLink.value = it.trim()
                    }
                )
                true
            }

        menu.add("数据来源")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = "效果预览会加载大图，比较耗时，请耐心等待。\n\n数据来源：anime-pictures.net",
                    cancelText = null
                )
                true
            }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}