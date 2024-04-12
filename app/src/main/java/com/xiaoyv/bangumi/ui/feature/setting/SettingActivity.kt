package com.xiaoyv.bangumi.ui.feature.setting

import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.xiaoyv.bangumi.databinding.ActivitySettingBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.config.GlobalConfig
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UpdateHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Class: [SettingActivity]
 *
 * @author why
 * @since 12/8/23
 */
class SettingActivity : BaseViewModelActivity<ActivitySettingBinding, SettingViewModel>() {

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        refresh()
    }

    override fun initListener() {
        binding.btnAction.setOnFastLimitClickListener {
            if (UserHelper.isLogin) {
                showConfirmDialog(message = "是否退出登录？", onConfirmClick = {
                    UserHelper.logout()
                    finish()
                })
            } else {
                RouteHelper.jumpSignIn()
            }
        }

        binding.settingInfo.setOnFastLimitClickListener {
            RouteHelper.jumpEditProfile()
        }

        binding.settingPrivacy.setOnFastLimitClickListener {
            RouteHelper.jumpPrivacy()
        }

        binding.settingBlock.setOnFastLimitClickListener {
            RouteHelper.jumpBlockUser()
        }

        binding.settingRobot.setOnFastLimitClickListener {
            RouteHelper.jumpRobotConfig()
        }

        binding.settingTab.setOnFastLimitClickListener {
            RouteHelper.jumpTabConfig()
        }

        binding.settingClean.setOnFastLimitClickListener {
            showConfirmDialog(
                message = "是否清空缓存？\n\n注意：\n清空缓存后，浏览过的图片等资源需要重新加载，空间够的情况下不建议清理。",
                onConfirmClick = {
                    viewModel.cleanCache()
                }
            )
        }

        binding.settingTranslate.setOnFastLimitClickListener {
            RouteHelper.jumpTranslateConfig()
        }

        binding.settingFeedback.setOnFastLimitClickListener {
            showOptionsDialog(
                title = "反馈建议",
                items = listOf("Github Issues", "班固米小组"),
                onItemClick = { _, position ->
                    if (position == 0) {
                        RouteHelper.jumpWeb(
                            url = "https://github.com/xiaoyvyv/bangumi/issues",
                            forceBrowser = true
                        )
                    } else {
                        RouteHelper.jumpGroupDetail("android_client")
                    }
                }
            )
        }

        binding.settingNetwork.setOnFastLimitClickListener {
            RouteHelper.jumpConfigNetwork()
        }

        binding.settingUi.setOnFastLimitClickListener {
            RouteHelper.jumpUiConfig()
        }

        binding.settingDonation.setOnFastLimitClickListener {
            RouteHelper.jumpPreviewImage(
                showImage = "file:///android_asset/image/ic_donation.jpg"
            )
        }

        binding.settingDonationUser.setOnFastLimitClickListener {
            RouteHelper.jumpWeb(GlobalConfig.docDonation, fitToolbar = true, smallToolbar = true)
        }

        binding.settingGroup.setOnFastLimitClickListener {
            RouteHelper.jumpWeb("https://qm.qq.com/q/YomiSMeyUs", forceBrowser = true)
        }

        binding.settingGithub.setOnFastLimitClickListener {
            RouteHelper.jumpWeb("https://github.com/xiaoyvyv/bangumi", forceBrowser = true)
        }

        binding.settingAgreement.setOnFastLimitClickListener {
            RouteHelper.jumpWeb(GlobalConfig.docPrivacy, fitToolbar = true, smallToolbar = true)
        }

        binding.settingAuthor.setOnFastLimitClickListener {
            showOptionsDialog(
                title = "关于作者",
                items = listOf("个人介绍", "班固米主页"),
                onItemClick = { _, position ->
                    if (position == 0) {
                        RouteHelper.jumpWeb(
                            url = GlobalConfig.docAuthor,
                            fitToolbar = true,
                            smallToolbar = true
                        )
                    } else {
                        RouteHelper.jumpUserDetail("837364")
                    }
                }
            )
        }

        binding.settingAbout.setOnFastLimitClickListener {
            if (ConfigHelper.updateChannel == UpdateHelper.CHANNEL_RELEASE) {
                UpdateHelper.checkUpdateRelease(this, true)
            } else {
                UpdateHelper.checkUpdateAction(this, true)
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        viewModel.onRefreshItem.observe(this) {
            refresh()
        }

        UserHelper.observeUserInfo(this) {
            refresh()
        }
    }

    private fun refresh() {
        binding.settingAbout.title = "关于 " + AppUtils.getAppName()
        binding.settingAbout.desc = "版本 " + AppUtils.getAppVersionName()
        binding.btnAction.text = if (UserHelper.isLogin) "退出登录" else "登录"

        launchUI {
            val cacheSize = withContext(Dispatchers.IO) {
                ConvertUtils.byte2MemorySize(
                    FileUtils.getLength(PathUtils.getExternalAppCachePath()) + FileUtils.getLength(
                        PathUtils.getInternalAppCachePath()
                    ), MemoryConstants.MB
                )
            }
            binding.settingClean.desc = String.format("%.2f MB", cacheSize)
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