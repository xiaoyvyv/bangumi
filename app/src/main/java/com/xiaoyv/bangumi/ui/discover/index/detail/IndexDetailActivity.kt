package com.xiaoyv.bangumi.ui.discover.index.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.tabs.TabLayoutMediator
import com.xiaoyv.bangumi.databinding.ActivityIndexDetailBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.blueprint.kts.activity
import com.xiaoyv.blueprint.kts.launchUI
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.api.parser.entity.IndexDetailEntity
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.addCommonMenu
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showInputLine2Dialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog
import kotlinx.coroutines.delay

/**
 * Class: [IndexDetailActivity]
 *
 * @author why
 * @since 12/12/23
 */
class IndexDetailActivity :
    BaseViewModelActivity<ActivityIndexDetailBinding, IndexDetailViewModel>() {
    private var mediator: TabLayoutMediator? = null

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.indexId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)
    }

    override fun initData() {

    }

    override fun initListener() {
        binding.ivAvatar.setOnFastLimitClickListener {
            val userId = viewModel.onIndexDetailLiveData.value?.userId.orEmpty()
            if (userId.isBlank().not()) {
                RouteHelper.jumpUserDetail(userId)
            }
        }

        binding.tvDesc.setOnFastLimitClickListener {
            RouteHelper.jumpSummaryDetail(viewModel.onIndexDetailLiveData.value?.contentHtml.orEmpty())
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState
        )

        // 目录详情
        viewModel.onIndexDetailLiveData.observe(this) {
            val entity = it ?: return@observe
            showPages(entity)

            binding.tvName.text = entity.userName
            binding.ivAvatar.loadImageAnimate(entity.userAvatar)
            binding.ivBanner.loadImageBlur(entity.userAvatar)
            binding.tvDesc.text = entity.content

            invalidateMenu()
        }

        viewModel.onDeleteResult.observe(this) {
            finish()
        }

        UserHelper.observeUserInfo(this) {
            viewModel.queryIndexDetail()
        }
    }

    /**
     * 加载 tab 页面
     */
    private fun showPages(entity: IndexDetailEntity) {
        if (entity.tabs.isEmpty()) {
            launchUI {
                delay(100)
                binding.stateView.showTip(message = "该目录还没有添加内容")
            }
            return
        }

        val vpAdapter = IndexDetailAdapter(
            viewModel.indexId,
            entity.tabs,
            supportFragmentManager,
            activity.lifecycle
        )

        // 分类等页面
        binding.vpContent.adapter = vpAdapter
        binding.vpContent.offscreenPageLimit = vpAdapter.itemCount

        // 绑定 Tabs
        mediator?.detach()
        mediator = TabLayoutMediator(binding.tableLayout, binding.vpContent) { tab, position ->
            tab.text = entity.tabs[position].title
        }
        mediator?.attach()
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val entity = viewModel.onIndexDetailLiveData.value ?: return super.onCreateOptionsMenu(menu)
        if (viewModel.isMine) {
            menu.add("修改")
                .setOnMenuItemClickListener {
                    showInputLine2Dialog(
                        title = "修改目录",
                        inputHint1 = "标题",
                        inputHint2 = "描述",
                        default1 = entity.title,
                        default2 = entity.content,
                    )
                    true
                }

            menu.add("删除")
                .setOnMenuItemClickListener {
                    showConfirmDialog(
                        message = "删除操作将抹掉所有关联数据以及用户留言，是否要继续？",
                        onConfirmClick = {
                            viewModel.deleteIndex()
                        }
                    )
                    true
                }
        } else {
            val isCollected = viewModel.isCollected
            menu.add(if (isCollected) "取消收藏" else "收藏")
                .setIcon(if (isCollected) CommonDrawable.ic_bookmark_added else CommonDrawable.ic_bookmark_add)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    if (UserHelper.isLogin.not()) {
                        RouteHelper.jumpLogin()
                        return@setOnMenuItemClickListener true
                    }

                    val tip = if (isCollected) "是否取消收藏该目录？" else "是否收藏该目录？"
                    showConfirmDialog(message = tip) {
                        viewModel.actionCollection(isCollected.not())
                    }
                    true
                }
        }

        // 公共菜单
        menu.addCommonMenu(BgmApiManager.buildReferer(BgmPathType.TYPE_INDEX, viewModel.indexId))
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}