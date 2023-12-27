package com.xiaoyv.bangumi.ui.feature.user.bg

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityUserBgBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.config.glide.ProgressTarget
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.common.widget.image.AnimeImageView
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getAttrColor
import java.io.File
import kotlin.math.roundToInt

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

        binding.srlRefresh.initRefresh { true }
        binding.srlRefresh.setColorSchemeColors(getAttrColor(GoogleAttr.colorPrimary))
    }

    override fun initListener() {
        binding.fabRandom.setOnFastLimitClickListener {
            viewModel.randomImage()
        }

        configBgAdapter.setOnDebouncedChildClickListener(R.id.item_image) {
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
        val background = ConfigHelper.userBackground
        if (background.isNotBlank()) {
            val target = ImageProgressTarget(
                background, this,
                object : CustomViewTarget<AnimeImageView, File>(binding.ivBanner) {
                    override fun onLoadFailed(errorDrawable: Drawable?) {

                    }

                    override fun onResourceCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
//                        view.setImage(ImageSource.uri(Uri.fromFile(resource)))
                    }
                }
            )

            Glide.with(this)
                .asFile()
                .load(background)
                .into(target)
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

    inner class ImageProgressTarget<Z>(model: String?, context: Context, target: Target<Z>) :
        ProgressTarget<String, Z>(model, context, target) {

        override fun onConnecting() {
            binding.pbMedia.progress = 0
            binding.pbMedia.isVisible = true
            debugLog { "ImageProgressTarget: onConnecting" }
        }

        override fun onDownloading(bytesRead: Long, expectedLength: Long) {
            if (expectedLength != 0L) {
                val progress = ((bytesRead / expectedLength.toDouble()) * 100).roundToInt()
                binding.pbMedia.progress = progress
            }
            debugLog { "ImageProgressTarget: bytesRead: $bytesRead / expectedLength: $$expectedLength" }
        }

        override fun onDownloaded() {
            debugLog { "ImageProgressTarget: onDownloaded" }
        }

        override fun onDelivered() {
            debugLog { "ImageProgressTarget: onDelivered" }
            binding.pbMedia.progress = 100
            binding.pbMedia.isVisible = false
        }
    }
}