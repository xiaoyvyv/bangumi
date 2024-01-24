package com.xiaoyv.bangumi.special.syncer

import android.view.Menu
import android.view.MenuItem
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.databinding.ActivitySyncerBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [SyncerActivity]
 *
 * @author why
 * @since 1/23/24
 */
class SyncerActivity : BaseViewModelActivity<ActivitySyncerBinding, SyncerViewModel>() {

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.gpButtons.addOnButtonCheckedListener { _, i, checked ->
            if (!checked) {
                return@addOnButtonCheckedListener
            }
            viewModel.isBilibili.value = i == binding.btnB.id
        }

        binding.etB.doAfterTextChanged {
            binding.btnStart.isEnabled = it.toString().isNotBlank()
        }

        binding.btnStart.setOnFastLimitClickListener {
            val b = binding.etB.text.toString().trim()
            viewModel.handleId(b)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.isBilibili.observe(this) {
            if (it) {
                binding.tvB.text = "哔哩哔哩同步"
                binding.tlB.hint = "哔哩哔哩空间链接或ID"
            } else {
                binding.tvB.text = "豆瓣同步"
                binding.tlB.hint = "豆瓣空间链接或ID"
            }
        }

        viewModel.idLiveData.observe(this) {
            if (it != null) {


            }
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Help")
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = "链接不支持短链，若只有短链请在浏览器打开短链后，复制原始带ID的链接。\n\nB站追番同步，请在APP设置内暂时公开追番隐私权限。\n\n权限配置路径：设置->安全隐私->空间隐私设置->公开我的追番",
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