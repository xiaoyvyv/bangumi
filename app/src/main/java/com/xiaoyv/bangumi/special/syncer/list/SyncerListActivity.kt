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
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
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
        get() = i18n(CommonString.syncer_list)

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<AnimeSyncEntity, *> {
        return SyncerListAdapter()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_subject) {
            showOptionsDialog(
                title = i18n(CommonString.syncer_list_item),
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
                message = i18n(CommonString.syncer_list_success),
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
        menu.add(i18n(CommonString.syncer_list_start))
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

        menu.add(i18n(CommonString.syncer_list_empty))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = i18n(CommonString.syncer_list_empty_reason),
                    cancelText = null
                )
                true
            }

        menu.add(i18n(CommonString.syncer_list_pref))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = i18n(CommonString.syncer_list_pref_reason),
                    cancelText = null
                )
                true
            }

        return super.onCreateOptionsMenu(menu)
    }
}