package com.xiaoyv.bangumi.ui.feature.setting.privacy

import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.common.api.parser.entity.PrivacyEntity
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showOptionsDialog

/**
 * Class: [PrivacyActivity]
 *
 * @author why
 * @since 12/17/23
 */
class PrivacyActivity : BaseListActivity<PrivacyEntity, PrivacyViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = "隐私设置"

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_privacy) {
            val stringMap = (contentAdapter as PrivacyAdapter).tip
            val keys = stringMap.keys.toList()
            val values = stringMap.values.toList()

            showOptionsDialog(
                title = it.title,
                items = values,
                onItemClick = { _, position ->
                    viewModel.changeNotifySetting(it.id, keys[position])
                }
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("保存")
            .setIcon(CommonDrawable.ic_save)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
               viewModel.savePrivacy()
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateContentAdapter(): PrivacyAdapter {
        return PrivacyAdapter()
    }
}