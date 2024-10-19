package com.xiaoyv.bangumi.special.collection

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import com.blankj.utilcode.util.ActivityUtils
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.annotation.LocalCollectionType
import com.xiaoyv.common.database.collection.Collection
import com.xiaoyv.common.helper.CollectionHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [CollectionActivity]
 *
 * @author why
 * @since 1/3/24
 */
class CollectionActivity : BaseListActivity<Collection, CollectionViewModel>() {

    private val pickFile =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.importData(uri)
                }
            }
        }

    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = when (viewModel.type) {
            LocalCollectionType.TYPE_BLOG -> i18n(CommonString.collect_data_blog)
            LocalCollectionType.TYPE_TOPIC -> i18n(CommonString.collect_data_topic)
            else -> i18n(CommonString.collect_data)
        }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.type = bundle.getInt(NavKey.KEY_INTEGER)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_collection) {
            RouteHelper.handleUrl(it.tUrl)
        }

        contentAdapter.addOnItemChildLongClickListener(R.id.item_collection) { adapter, _, position ->
            val collection =
                adapter.getItem(position) ?: return@addOnItemChildLongClickListener true

            requireActivity.showConfirmDialog(
                title = i18n(CommonString.collect_cancel_title),
                message = i18n(CommonString.collect_cancel_message),
                onConfirmClick = {
                    viewModel.deleteCollection(collection)
                }
            )
            true
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onExportLivaData.observe(this) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/json"
            shareIntent.putExtra(Intent.EXTRA_STREAM, it)
            ActivityUtils.startActivity(
                Intent.createChooser(shareIntent, i18n(CommonString.collect_export))
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(i18n(CommonString.collect_merge_remote))
            .setIcon(CommonDrawable.ic_sync_cloud)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                if (!CollectionHelper.isEnable) {
                    RouteHelper.jumpConfigNetwork()
                    return@setOnMenuItemClickListener true
                }
                CollectionHelper.syncCollection(
                    state = viewModel.loadingDialogState(cancelable = false),
                    overrideRemote = false,
                    toast = true
                )
                true
            }

        menu.add(i18n(CommonString.collect_override_remote))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                if (!CollectionHelper.isEnable) {
                    RouteHelper.jumpConfigNetwork()
                    return@setOnMenuItemClickListener true
                }
                CollectionHelper.syncCollection(
                    state = viewModel.loadingDialogState(cancelable = false),
                    overrideRemote = true,
                    toast = true
                )
                true
            }

        menu.add(i18n(CommonString.collect_import))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                pickFile.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                })
                true
            }

        menu.add(i18n(CommonString.collect_export_title))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                viewModel.exportData()
                true
            }

        menu.add(i18n(CommonString.collect_config))
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                RouteHelper.jumpConfigNetwork()
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<Collection, *> {
        return CollectionAdapter()
    }
}