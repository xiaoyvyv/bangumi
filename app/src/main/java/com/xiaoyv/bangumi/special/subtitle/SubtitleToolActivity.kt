package com.xiaoyv.bangumi.special.subtitle

import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import com.xiaoyv.bangumi.databinding.ActivitySubtitleToolBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog

class SubtitleToolActivity :
    BaseViewModelActivity<ActivitySubtitleToolBinding, SubtitleToolViewModel>() {

    private val selectSubtitleLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            viewModel.loadSubtitleFromMedia(it ?: return@registerForActivityResult)
        }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.btnSubtitle.setOnFastLimitClickListener {
            selectSubtitleLauncher.launch("*/*")
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    @CallSuper
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }

}