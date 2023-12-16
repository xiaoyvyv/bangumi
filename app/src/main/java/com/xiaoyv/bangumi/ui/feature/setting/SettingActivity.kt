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
import com.xiaoyv.common.helper.UpdateHelper
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.openInBrowser
import com.xiaoyv.common.kts.showConfirmDialog
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
                RouteHelper.jumpLogin()
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

        }

        binding.settingDonation.setOnFastLimitClickListener {
            RouteHelper.jumpPreviewImage(
                showImage = "file:///android_asset/image/ic_donation.jpg"
            )
        }

        binding.settingDonationUser.setOnFastLimitClickListener {

        }

        binding.settingGroup.setOnFastLimitClickListener {
            openInBrowser("https://qm.qq.com/q/YomiSMeyUs")
        }

        binding.settingGithub.setOnFastLimitClickListener {
            openInBrowser("https://github.com/xiaoyvyv/Bangumi-for-Android")
        }

        binding.settingAgreement.setOnFastLimitClickListener {

        }

        binding.settingAuthor.setOnFastLimitClickListener {

        }

        binding.settingAbout.setOnFastLimitClickListener {
            UpdateHelper.checkUpdate(this, true)
        }

        binding.settingAuthor.setOnFastLimitClickListener {

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
        binding.settingInfo.title = "资料与账号"
        binding.settingPrivacy.title = "隐私设置"
        binding.settingBlock.title = "绝交用户"

        binding.settingClean.title = "清理缓存"
        binding.settingTranslate.title = "翻译设置"

        binding.settingAgreement.title = "隐私政策摘要"
        binding.settingAbout.title = "关于 " + AppUtils.getAppName()
        binding.settingAbout.desc = "版本 " + AppUtils.getAppVersionName()
        binding.settingAuthor.title = "关于作者"

        binding.settingRobot.title = "Bangumi 娘"
        binding.settingRobot.desc = "关闭"

        binding.settingFeedback.title = "反馈 BUG"
        binding.settingFeedback.desc = "建议或反馈"
        binding.settingDonation.title = "投食🍚"
        binding.settingDonation.desc = ""
        binding.settingDonationUser.title = "赞助者"
        binding.settingGroup.title = "QQ 交流群"
        binding.settingGroup.desc = "671395625"
        binding.settingGithub.title = "开源地址"
        binding.btnAction.text = if (UserHelper.isLogin) {
            "退出登录"
        } else {
            "登录"
        }

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