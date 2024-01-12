package com.xiaoyv.bangumi.ui.feature.setting.translate

import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivitySettingTranslateBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [TranslateConfigActivity]
 *
 * @author why
 * @since 12/17/23
 */
class TranslateConfigActivity : BaseBindingActivity<ActivitySettingTranslateBinding>() {
    override fun initView() {
        binding.settingAppId.title = "百度翻译 AppId"
        binding.settingAppSecret.title = "百度翻译 AppSecret"

        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        refresh()
    }

    override fun initListener() {
        binding.settingAppId.setOnFastLimitClickListener {
            val (id, _) = ConfigHelper.readBaiduTranslateConfig()

            showInputDialog(
                title = "百度翻译 AppId",
                inputHint = "请输入 AppId",
                default = id,
                onInput = {
                    ConfigHelper.configBaiduTranslateId(it.trim())

                    refresh()
                }
            )
        }

        binding.settingAppSecret.setOnFastLimitClickListener {
            val (_, secret) = ConfigHelper.readBaiduTranslateConfig()

            showInputDialog(
                title = "百度翻译 AppSecret",
                inputHint = "请输入 AppSecret",
                default = secret,
                onInput = {
                    ConfigHelper.configBaiduTranslateSecret(it.trim())

                    refresh()
                }
            )
        }
    }

    /**
     * 刷新 UI
     */
    private fun refresh() {
        val (id, secret) = ConfigHelper.readBaiduTranslateConfig()
        binding.settingAppId.desc = if (id.isBlank()) "未配置" else "已配置"
        binding.settingAppSecret.desc = if (secret.isBlank()) "未配置" else "已配置"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("如何获取")
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = "可以去百度开放平台注册，个人每月5万字免费额度",
                    confirmText = "去申请",
                    onConfirmClick = {
                        RouteHelper.jumpWeb(
                            url = "http://api.fanyi.baidu.com/api/trans/product/desktop",
                            forceBrowser = true
                        )
                    }
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