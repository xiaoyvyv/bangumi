package com.xiaoyv.bangumi.ui.feature.setting.network

import android.view.Menu
import android.view.MenuItem
import android.webkit.URLUtil
import com.xiaoyv.bangumi.databinding.ActivitySettingGithubBinding
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import com.xiaoyv.widget.kts.toast

/**
 * Class: [NetworkConfigActivity]
 *
 * @author why
 * @since 12/17/23
 */
class NetworkConfigActivity :
    BaseViewModelActivity<ActivitySettingGithubBinding, NetworkConfigViewModel>() {

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {
        binding.settingUser.setOnFastLimitClickListener {
            showInputDialog(
                title = "Github 用户名",
                inputHint = "用户名",
                default = ConfigHelper.githubUser,
                onInput = {
                    ConfigHelper.githubUser = it.trim()
                    refresh()
                }
            )
        }

        binding.settingRepo.setOnFastLimitClickListener {
            showInputDialog(
                title = "Github 仓库名称",
                inputHint = "仓库名",
                default = ConfigHelper.githubRepo,
                onInput = {
                    ConfigHelper.githubRepo = it.trim()
                    refresh()
                }
            )
        }

        binding.settingToken.setOnFastLimitClickListener {
            showInputDialog(
                title = "Github 个人 Token",
                inputHint = "Token",
                default = ConfigHelper.githubToken,
                onInput = {
                    ConfigHelper.githubToken = it.trim()
                    refresh()
                }
            )
        }

        binding.settingHost.setOnFastLimitClickListener {
            showInputDialog(
                title = "Hosts 解析配置",
                inputHint = "内容",
                default = ConfigHelper.netHosts,
                minLines = 8,
                onInput = {
                    ConfigHelper.netHosts = it.trim()
                    refresh()
                }
            )
        }

        binding.settingHostTest.setOnFastLimitClickListener {
            showInputDialog(
                title = "Hosts 解析测试",
                inputHint = "网址",
                minLines = 1,
                onInput = { input ->
                    val url = input.trim().let {
                        if (URLUtil.isNetworkUrl(it)) it else "https://$it"
                    }

                    viewModel.testNetwork(url)
                }
            )
        }

        refresh()
    }

    private fun refresh() {
        binding.settingUser.desc = ConfigHelper.githubUser
        binding.settingRepo.desc = ConfigHelper.githubRepo
        binding.settingToken.desc = ConfigHelper.githubToken
        binding.settingHost.desc = ConfigHelper.netHosts
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Github 收藏仓库指南")
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    title = "Github 收藏配置指南",
                    message = "此功能可以同步你的话题和日志收藏数据到 Github 仓库，需要你在 Github 创建一个空仓库，然后在个人中心创建一个 Token 即可。\n\nToken 请在 Github 个人中心 -> Setting -> Developer settings -> Personal access token -> Fine-grained tokens (Beta 版) 进行创建，注意需要勾选你创建用来保存收藏数据的仓库，以及需要 Content 和 Pages 权限，然后点击创建即可。\n\n填写配置时，用户名和仓库名填写时都要区分大小写，请确保输入正确！",
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