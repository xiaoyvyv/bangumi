package com.xiaoyv.bangumi.special.syncer

import android.content.Intent
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.xiaoyv.bangumi.databinding.ActivitySyncerBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.database.BgmDatabaseManager
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
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

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data?.data
            if (it.resultCode == RESULT_OK && data != null) {
                viewModel.installDatabase(data)
            }
        }

    private val platformName
        get() = if (viewModel.isBilibili.value == true) i18n(CommonString.syncer_bilibili)
        else i18n(CommonString.syncer_dou_ban)

    override fun initView() {
        binding.toolbar.initNavBack(this)

        refreshSubjectDatabase()
    }

    override fun initData() {
        viewModel.queryLocalBgmCollection()
    }

    override fun initListener() {
        binding.gpButtons.addOnButtonCheckedListener { _, i, checked ->
            if (!checked) {
                return@addOnButtonCheckedListener
            }
            viewModel.isBilibili.value = i == binding.btnB.id
        }

        binding.etB.doAfterTextChanged {
            binding.btnStart.isEnabled =
                it.toString().isNotBlank() && viewModel.isDoing.value == false
        }

        binding.btnStart.setOnFastLimitClickListener {
            viewModel.handleId(binding.etB.text.toString().trim())
            KeyboardUtils.hideSoftInput(this)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.isBilibili.observe(this) {
            if (it) {
                binding.tvB.text = i18n(CommonString.syncer_bilibili_title)
                binding.etB.hint = i18n(CommonString.syncer_bilibili_id)
            } else {
                binding.tvB.text = i18n(CommonString.syncer_dou_ban_title)
                binding.etB.hint = i18n(CommonString.syncer_dou_ban_id)
            }
        }

        // 正在导入数据中，禁止重复点击
        viewModel.isDoing.observe(this) {
            binding.btnStart.isEnabled = binding.etB.text.toString().isNotBlank() && !it
            if (it) {
                binding.btnStart.text = i18n(CommonString.syncer_loading)
            } else {
                binding.btnStart.text = i18n(CommonString.syncer_start)
            }
        }

        // 显示收藏载入结果，解析同步操作
        viewModel.onShowSyncListLiveData.observe(this) {
            RouteHelper.jumpSyncerList()
            finish()
        }

        // 进度
        viewModel.onBgmCollectProgress.observe(this) {
            binding.pbBgmCollect.max = it.second
            binding.pbBgmCollect.setProgress(it.first, true)
            binding.tvBgm.text = i18n(CommonString.syncer_bgm_data_format, it.first, it.second)
        }

        // 想看数目
        viewModel.onPlatformWishCollectProgress.observe(this) {
            binding.pbPlatformWishCollect.max = it.second
            binding.pbPlatformWishCollect.setProgress(it.first, true)
            binding.tvPlatformWish.text =
                i18n(
                    CommonString.syncer_platform_data_format_wish,
                    platformName,
                    it.first,
                    it.second
                )
        }

        // 在看数目
        viewModel.onPlatformDoingCollectProgress.observe(this) {
            binding.pbPlatformDoingCollect.max = it.second
            binding.pbPlatformDoingCollect.setProgress(it.first, true)
            binding.tvPlatformDoing.text =
                i18n(
                    CommonString.syncer_platform_data_format_doing,
                    platformName,
                    it.first,
                    it.second
                )
        }

        // 看过数目
        viewModel.onPlatformDoneCollectProgress.observe(this) {
            binding.pbPlatformDoneCollect.max = it.second
            binding.pbPlatformDoneCollect.setProgress(it.first, true)
            binding.tvPlatformDone.text =
                i18n(
                    CommonString.syncer_platform_data_format_done,
                    platformName,
                    it.first,
                    it.second
                )
        }

        viewModel.onDatabaseInstall.observe(this) {
            refreshSubjectDatabase()
        }
    }

    private fun refreshSubjectDatabase() {
        if (BgmDatabaseManager.isSubjectInstalled()) {
            SpanUtils.with(binding.tvDatabase)
                .append(i18n(CommonString.syncer_database_installed))
                .setForegroundColor(Color.GREEN)
                .create()
        } else {
            SpanUtils.with(binding.tvDatabase)
                .append(i18n(CommonString.syncer_database_not_install))
                .setForegroundColor(Color.RED)
                .create()
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(i18n(CommonString.common_help))
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = i18n(CommonString.syncer_config),
                    cancelText = null
                )
                true
            }

        menu.add(i18n(CommonString.syncer_database_install))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("application/zip")
                launcher.launch(intent)
                true
            }

        menu.add(i18n(CommonString.syncer_database_download))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                RouteHelper.jumpWeb(
                    url = "https://github.com/xiaoyvyv/Bangumi-Data/raw/main/subject/subject.db.zip",
                    forceBrowser = true
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