package com.xiaoyv.bangumi.ui.feature.user.bg

import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.SnackbarUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityUserBgBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.getAttrColor

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
        binding.rvImage.setHasFixedSize(true)
        binding.rvImage.adapter = configBgAdapter
        binding.rvImage.removeImageScrollLoadController()

        binding.srlRefresh.initRefresh { true }
        binding.srlRefresh.setColorSchemeColors(getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initListener() {
        binding.fabRandom.setOnFastLimitClickListener {
            binding.appBar.setExpanded(true, true)

            viewModel.randomImage()
        }

        configBgAdapter.setOnDebouncedChildClickListener(R.id.item_image) {
            // 加载图片
            binding.appBar.setExpanded(true, true)

            // 配置大图
            viewModel.cacheImageLink.value = it.largeImageUrl
        }

        binding.srlRefresh.setOnRefreshListener {
            viewModel.randomImage()
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onImageListLiveData.observe(this) {
            configBgAdapter.submitList(it.orEmpty())
        }

        viewModel.cacheImageLink.observe(this) {
            binding.ivBanner.loadImageAnimate(it)
        }

        viewModel.onSaveBgResultLiveData.observe(this) {
            SnackbarUtils.with(binding.root).setMessage(it.orEmpty()).show()
        }

        UserHelper.observeUserInfo(this) {
            loadUserBg()

            binding.ivAvatar.loadImageAnimate(it.avatar)
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
        val signBg = UserHelper.currentUser.roomPic
        if (signBg.isNullOrBlank()) {
            binding.ivBanner.loadImageBlur(UserHelper.currentUser.avatar)
        } else {
            binding.ivBanner.loadImageAnimate(signBg)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Save")
            .setIcon(CommonDrawable.ic_save)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                val link = viewModel.cacheImageLink.value.orEmpty()
                if (link.isNotBlank()) {
                    showConfirmDialog(
                        title = "空间背景",
                        message = "是否保存设置空间背景？\n\n配置说明：\n若为本地图片则为私密，否则为公开背景，此配置将会以0字体大小储存在你的个人简介末尾，不会影响网页端的个人简介希显示。",
                        onConfirmClick = {
                            viewModel.saveSingBg(link)
                        }
                    )
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

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}