package com.xiaoyv.bangumi.ui.feature.friendly.rank

import android.view.Menu
import android.view.MenuItem
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.layoutmanager.QuickGridLayoutManager
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.database.friendly.FriendlyRankEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi

class FriendRankDetailActivity : BaseListActivity<FriendlyRankEntity, FriendRankDetailViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false

    override val toolbarTitle: String
        get() = "我的友评榜（${viewModel.overScoreCount}+ 人）"

    override fun initView() {
        super.initView()
        binding.rvContent.updatePadding(left = 8.dpi, right = 8.dpi)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.iv_cover) {
            RouteHelper.jumpMediaDetail(it.subjectId.toString())
        }
    }


    override fun onListDataFinish(list: List<FriendlyRankEntity>) {
        if (list.isEmpty() && viewModel.overScoreCount == 1) {
            showConfirmDialog(
                title = "温馨提示",
                message = "本地暂无好友评分数据，是否去拉取重新生成排行榜？",
                onConfirmClick = {
                    RouteHelper.jumpFriendRankImport()
                    finish()
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("去刷新")
            .setIcon(CommonDrawable.ic_refresh)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                RouteHelper.jumpFriendRankImport()
                true
            }

        menu.add("仅看超过 n 位友评")
            .setOnMenuItemClickListener {
                showOptionsDialog(
                    title = "超过 n 位好友评分才看",
                    items = mutableListOf<String>().apply {
                        repeat(50) {
                            add(String.format("仅看 %d+ 人评分条目", it + 1))
                        }
                    },
                    onItemClick = { _, index ->
                        viewModel.overScoreCount = index + 1
                        viewModel.refresh()

                        // 刷新标题
                        binding.toolbar.title = toolbarTitle
                    }
                )
                true
            }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<FriendlyRankEntity, *> {
        return FriendRankDetailAdapter()
    }

    override fun onCreateLayoutManager(): LinearLayoutManager {
        return QuickGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
    }
}