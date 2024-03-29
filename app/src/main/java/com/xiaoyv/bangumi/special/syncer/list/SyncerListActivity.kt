package com.xiaoyv.bangumi.special.syncer.list

import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.api.response.anime.AnimeSyncEntity
import com.xiaoyv.common.config.annotation.SubjectType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.common.kts.showOptionsDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.orEmpty

/**
 * Class: [SyncerListActivity]
 *
 * @author why
 * @since 1/26/24
 */
class SyncerListActivity : BaseListActivity<AnimeSyncEntity, SyncerListViewModel>() {

    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = "待同步条目列表"

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<AnimeSyncEntity, *> {
        return SyncerListAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_subject) {
            showOptionsDialog(
                title = "条目",
                items = it.subject.map { item ->
                    buildString {
                        append(item.name.orEmpty())
                        append(" - ")
                        append(SubjectType.string(item.type))
                    }
                },
                onItemClick = { _, index ->
                    RouteHelper.jumpMediaDetail(it.subject[index].id.toString())
                }
            )
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onSyncFinish.observe(this) {
            showConfirmDialog(
                message = "条目同步完成",
                cancelText = null,
                cancelable = false,
                onConfirmClick = {
                    RouteHelper.jumpUserDetail(UserHelper.currentUser.id)
                    finish()
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("开始同步")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setIcon(CommonDrawable.ic_play_round)
            .setOnMenuItemClickListener {
                if (viewModel.onListLiveData.value?.size.orEmpty() > 0) {
                    val listDialog = SyncerListDialog()
                    listDialog.isCancelable = false
                    listDialog.show(supportFragmentManager, "SyncerListDialog")

                    viewModel.startSync()
                }
                true
            }

        menu.add("没有内容？")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = "如果当前页面显示为空，可能是以下情况：\n\n1、当前你的 BGM 收藏已经包含全部条目。\n2、你在其它平台收藏的条目，BGM 未收录。",
                    cancelText = null
                )
                true
            }

        menu.add("速度优化")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = "如果你在该页面长时间处于加载状态，请返回前一页，在右上角菜单下载并导入索引数据库",
                    cancelText = null
                )
                true
            }

        return super.onCreateOptionsMenu(menu)
    }
}