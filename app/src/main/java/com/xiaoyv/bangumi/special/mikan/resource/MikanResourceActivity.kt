package com.xiaoyv.bangumi.special.mikan.resource

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.special.magnet.MagnetActivity.Companion.onMagnetItemClick
import com.xiaoyv.bangumi.special.magnet.MagnetAdapter
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.response.anime.AnimeMagnetEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.CommonString
import com.xiaoyv.common.kts.i18n
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [MikanResourceActivity]
 *
 * @author why
 * @since 3/20/24
 */
class MikanResourceActivity :
    BaseListActivity<AnimeMagnetEntity.Resource, MikanResourceViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = buildString {
            append(i18n(CommonString.mikan_resource_detail))
            append("-")
            append(viewModel.subtitleGroupName)
        }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.bangumiId = bundle.getString(NavKey.KEY_STRING).orEmpty()
        viewModel.subtitleGroupId = bundle.getString(NavKey.KEY_STRING_SECOND).orEmpty()
        viewModel.subtitleGroupName = bundle.getString(NavKey.KEY_STRING_THIRD).orEmpty()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.addOnItemChildClickListener(R.id.item_magnet) { adapter, _, position ->
            val resource = adapter.getItem(position) ?: return@addOnItemChildClickListener
            onMagnetItemClick(resource)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(i18n(CommonString.common_help))
            .setIcon(CommonDrawable.ic_help)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                showConfirmDialog(
                    message = i18n(CommonString.mikan_about, viewModel.subtitleGroupName),
                    cancelText = null
                )
                true
            }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<AnimeMagnetEntity.Resource, *> {
        return MagnetAdapter { "" }
    }
}