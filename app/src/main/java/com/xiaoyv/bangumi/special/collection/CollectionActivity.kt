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
import com.xiaoyv.common.config.annotation.CollectionType
import com.xiaoyv.common.database.collection.Collection
import com.xiaoyv.common.helper.CollectionHelper
import com.xiaoyv.common.kts.CommonDrawable
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
            CollectionType.TYPE_BLOG -> "我收藏的日志"
            CollectionType.TYPE_TOPIC -> "我收藏的话题"
            else -> "我的收藏"
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
                title = "删除",
                message = "是否删除该收藏？",
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
            ActivityUtils.startActivity(Intent.createChooser(shareIntent, "收藏数据导出"))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("同步合并远程数据")
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

        menu.add("覆盖远程数据")
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

        menu.add("导入")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                pickFile.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                })
                true
            }

        menu.add("导出")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_NEVER)
            .setOnMenuItemClickListener {
                viewModel.exportData()
                true
            }

        menu.add("配置")
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