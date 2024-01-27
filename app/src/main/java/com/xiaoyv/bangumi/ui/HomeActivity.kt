package com.xiaoyv.bangumi.ui

import android.graphics.Typeface
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SpanUtils
import com.google.android.material.badge.BadgeDrawable
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityHomeBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.kts.launchProcess
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.FeatureType
import com.xiaoyv.common.currentApplication
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.UpdateHelper
import com.xiaoyv.common.helper.VolumeButtonHelper
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.customApplyWindowInsets
import com.xiaoyv.common.kts.debugLog
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.adjustScrollSensitivity
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.io.FileOutputStream

/**
 * Class: [HomeActivity]
 *
 * @author why
 * @since 11/24/23
 */
class HomeActivity : BaseViewModelActivity<ActivityHomeBinding, MainViewModel>() {
    private val vpAdapter by lazy { HomeAdapter(this, viewModel.mainTabs) }

    private val robot by lazy { HomeRobot(this) }

    /**
     * 设置页面快捷键
     */
    private val volumeHelper by lazy {
        VolumeButtonHelper(object : VolumeButtonHelper.OnSecretCodeListener {
            override fun onSecretCodeEntered() {
                RouteHelper.jumpSetting()
            }
        })
    }

    override fun initWindowConfig(window: Window) {
        // 设置屏幕方向
        ScreenUtils.setPortrait(this)

        // 窗口参数
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }

    override fun initView() {
        binding.vpView.adjustScrollSensitivity(ConfigHelper.vpTouchSlop.toFloat())
        binding.vpView.isUserInputEnabled = false
        binding.vpView.offscreenPageLimit = vpAdapter.itemCount.coerceAtLeast(1)
        binding.vpView.adapter = vpAdapter

        // 导入底栏
        binding.navView.menu.clear()
        binding.navView.customApplyWindowInsets()

        viewModel.mainTabs.forEachIndexed { index, tab ->
            binding.navView.menu
                .add(0, tab.id, index, tab.title)
                .setIcon(tab.icon)
        }

        // 初始底栏默认位置
        val defaultTabIndex = viewModel.mainDefaultTab()
        if (defaultTabIndex != 0) {
            binding.vpView.setCurrentItem(defaultTabIndex, false)
            binding.navView.selectedItemId = viewModel.mainTabs[defaultTabIndex].id
        }

        // 未配置底栏
        if (viewModel.mainTabs.isEmpty()) {
            showConfirmDialog(
                message = "你还未配置首页底栏，请到设置中心配置后重启应用",
                cancelText = null,
                cancelable = false,
                onConfirmClick = {
                    RouteHelper.jumpSetting()
                }
            )
        }
    }

    override fun initData() {
        // Bangumi 娘开关
        if (ConfigHelper.isRobotEnable) {
            viewModel.startRobotSayQueue()
        } else {
            robot.disable()
        }

        // 更新检测
        UpdateHelper.checkUpdate(this, false)

        showTip()
    }

    override fun initListener() {
        binding.navView.setOnItemSelectedListener {
            viewModel.mainTabs.forEachIndexed { index, tab ->
                if (tab.id == it.itemId) {
                    binding.vpView.setCurrentItem(index, false)
                }
            }
            true
        }

        binding.navView.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.bottom_menu_home -> {
                    viewModel.resetDiscoverIndex()
                }
            }
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        // 多条语句可能同时触发，先加个列队
        currentApplication.globalRobotSpeech.observe(this) {
            debugLog { "春菜：$it" }
            viewModel.addRobotSayQueue(it.trim())
        }

        // 电波和短信提醒
        currentApplication.globalNotify.observe(this) {
            val profileTab = viewModel.mainTabs.find { tab -> tab.type == FeatureType.TYPE_PROFILE }
            if (profileTab != null) {
                val badge = binding.navView.getOrCreateBadge(profileTab.id)
                if ((it.first + it.second) != 0) {
                    badge.number = it.first + it.second
                    badge.badgeGravity = BadgeDrawable.TOP_END
                } else {
                    binding.navView.removeBadge(profileTab.id)
                }
            }
        }

        // 春菜说话
        viewModel.onRobotSay.observe(this) {
            robot.onSay(it)
        }
    }

    private fun showTip() {
        if (ConfigHelper.showVersionTip) {
            launchUI {
                delay(2000)
                showConfirmDialog(
                    title = "App 声明",
                    message = SpanUtils.with(null)
                        .append("此客户端为班固米用户：")
                        .appendSpace(4.dpi)
                        .append("小玉")
                        .setTypeface(Typeface.DEFAULT_BOLD)
                        .setForegroundColor(getAttrColor(GoogleAttr.colorPrimary))
                        .appendSpace(4.dpi)
                        .append("为爱发电，耗时半个多月打造的班固米全功能三方客户端。")
                        .appendLine()
                        .appendLine()
                        .append("此 App 为原生安卓应用，并且有做大量优化如评论分页，缓存等，保证你在使用过程中响应极速不卡顿。")
                        .setForegroundColor(getAttrColor(GoogleAttr.colorPrimary))
                        .appendLine()
                        .appendLine()
                        .append("欢迎大家积极提出反馈或建议，或者加入交流群讨论，需求反馈等将第一时间得到回复。\n\n此软件不收集任何隐私数据并且完全开源。")
                        .create(),
                    neutralText = "不再提醒",
                    confirmText = "我知道了",
                    onNeutralClick = {
                        ConfigHelper.showVersionTip = false
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        robot.onResume()
    }

    override fun onPause() {
        super.onPause()
        robot.onPause()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        robot.onAttachedToWindow(binding.navView)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        robot.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        volumeHelper.handleVolumeButton(event)
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }
}