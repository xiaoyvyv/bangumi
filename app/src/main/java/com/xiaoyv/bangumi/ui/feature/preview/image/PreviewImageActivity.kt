package com.xiaoyv.bangumi.ui.feature.preview.image

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import androidx.viewpager2.widget.ViewPager2
import com.xiaoyv.bangumi.databinding.ActivityPreviewBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.common.kts.initNavBack

/**
 * Class: [PreviewImageActivity]
 *
 * @author why
 * @since 12/1/23
 */
class PreviewImageActivity :
    BaseViewModelActivity<ActivityPreviewBinding, PreviewImageViewModel>() {

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.currentImageUrl = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.totalImageUrls = bundle.getStringArray(NavKey.KEY_SERIALIZABLE_ARRAY)
            .orEmpty().toMutableList()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onImageListLiveData.observe(this) {
            val position = it.first

            binding.toolbar.title = String.format(
                "图片预览（%d/%d）", position + 1,
                viewModel.totalImageUrls.size
            )

            binding.vpImage.adapter = PreviewImageAdapter(activity, it.second)
            if (position != -1 && position < it.second.size) {
                binding.vpImage.setCurrentItem(position, false)
            }
        }
    }

    override fun initListener() {
        binding.vpImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val count = binding.vpImage.adapter?.itemCount ?: 0
                binding.toolbar.title = String.format("图片预览（%d/%d）", position + 1, count)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}