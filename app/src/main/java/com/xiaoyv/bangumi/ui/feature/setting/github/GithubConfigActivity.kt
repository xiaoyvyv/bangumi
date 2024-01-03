package com.xiaoyv.bangumi.ui.feature.setting.github

import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.databinding.ActivitySettingGithubBinding
import com.xiaoyv.blueprint.base.binding.BaseBindingActivity
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener

/**
 * Class: [GithubConfigActivity]
 *
 * @author why
 * @since 12/17/23
 */
class GithubConfigActivity : BaseBindingActivity<ActivitySettingGithubBinding>() {

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
    }

    private fun refresh() {
        binding.settingUser.desc = ConfigHelper.githubUser
        binding.settingRepo.desc = ConfigHelper.githubRepo
        binding.settingToken.desc = ConfigHelper.githubToken
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("提示")
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    title = "指南",
                    message = "此功能可以同步你的话题和日志收藏数据到 Github 仓库，需要你在 Github 创建一个空仓库，然后在个人中心创建一个 Token 即可。\n\nToken 请在 Github 个人中心 -> Setting -> Developer settings -> Personal access token -> Fine-grained tokens (Beta 版) 进行创建，注意需要勾选你创建用来保存收藏数据的仓库，以及需要 Content 和 Pages 权限，然后点击创建即可。\n\n填写配置时，用户名和仓库名填写时都要区分大小写，请确保输入正确！",
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