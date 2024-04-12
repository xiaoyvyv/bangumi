package com.xiaoyv.bangumi.ui.discover.group.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.databinding.ActivityGroupDetailBinding
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModelActivity
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.annotation.BgmPathType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.helper.addCommonMenu
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.initNavBack
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.common.kts.loadImageBlur
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.widget.dialog.AnimeLoadingDialog
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.dialog.UiDialog

/**
 * Class: [GroupDetailActivity]
 *
 * @author why
 * @since 12/7/23
 */
class GroupDetailActivity :
    BaseViewModelActivity<ActivityGroupDetailBinding, GroupDetailViewModel>() {
    private val recentlyAdapter by lazy { GroupDetailAdapter() }
    private val otherAdapter by lazy { GroupDetailAdapter() }
    private val viewPool by lazy { RecycledViewPool() }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.groupId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initView() {
        binding.toolbar.initNavBack(this)

        binding.rvGrid.setRecycledViewPool(viewPool)
        binding.rvOther.setRecycledViewPool(viewPool)
    }

    override fun initData() {
        binding.toolbar.title = String.format("Group: %s", viewModel.groupId)

        binding.sectionRecently.title = "最近加入"
        binding.sectionRecently.more = null
        binding.sectionOther.title = "相关的小组"
        binding.sectionOther.more = null

        binding.rvOther.adapter = otherAdapter
        binding.rvGrid.adapter = recentlyAdapter
    }

    override fun initListener() {
        recentlyAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpUserDetail(it.id)
        }

        otherAdapter.setOnDebouncedChildClickListener(R.id.iv_avatar) {
            RouteHelper.jumpGroupDetail(it.id)
        }

        binding.btnEnter.setOnFastLimitClickListener {
            RouteHelper.jumpGroupTopics(viewModel.groupId)
        }
    }

    override fun LifecycleOwner.initViewObserver() {
        binding.stateView.initObserver(
            lifecycleOwner = this,
            loadingViewState = viewModel.loadingViewState,
            loadingBias = 0.4f
        )

        viewModel.onGroupDetailLiveData.observe(this) {
            val entity = it ?: return@observe
            binding.ivBanner.loadImageBlur(entity.avatar)
            binding.ivAvatar.loadImageAnimate(entity.avatar)
            binding.tvName.text = entity.name
            binding.tvDesc.text = entity.id
            binding.toolbar.title = entity.name
            binding.tvTime.text = entity.time
            binding.tvSummary.text = entity.summary.ifBlank { "这个小组暂时没有介绍呢" }
            binding.tvSummary.setOnFastLimitClickListener {
                RouteHelper.jumpSummaryDetail(entity.summaryHtml)
            }

            recentlyAdapter.submitList(entity.recently)
            otherAdapter.submitList(entity.otherGroups)

            binding.clContainer.isVisible = true

            // 刷新
            invalidateOptionsMenu()
        }

        UserHelper.observeUserInfo(this) {
            viewModel.queryGroupDetail()
        }
    }

    override fun onCreateLoadingDialog(): UiDialog {
        return AnimeLoadingDialog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val joined = viewModel.isJoined

        menu.add(if (joined) "退出" else "加入")
            .setIcon(if (joined) CommonDrawable.ic_group_remove else CommonDrawable.ic_group_add)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                if (UserHelper.isLogin.not()) {
                    RouteHelper.jumpSignIn()
                    return@setOnMenuItemClickListener true
                }

                val tip = if (joined) "是否退出该小组？" else "是否加入该小组？"
                showConfirmDialog(message = tip) {
                    viewModel.actionGroup(joined.not())
                }
                true
            }

        // 公共菜单
        menu.addCommonMenu(BgmApiManager.buildReferer(BgmPathType.TYPE_GROUP, viewModel.groupId))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.initNavBack(this)
        return super.onOptionsItemSelected(item)
    }
}