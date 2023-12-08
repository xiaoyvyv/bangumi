package com.xiaoyv.bangumi.ui.discover.group.topic

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.api.parser.entity.TopicSampleEntity
import com.xiaoyv.common.config.annotation.TopicType
import com.xiaoyv.common.helper.UserHelper
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [GroupTopicsActivity]
 *
 * @author why
 * @since 12/8/23
 */
class GroupTopicsActivity : BaseListActivity<TopicSampleEntity, GroupTopicsViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = false

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.groupId = bundle.getString(NavKey.KEY_STRING).orEmpty()
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_topic) {
            RouteHelper.jumpTopicDetail(it.id, TopicType.TYPE_GROUP)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("发表")
            .setIcon(CommonDrawable.ic_post_add)
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setOnMenuItemClickListener {
                if (UserHelper.isLogin.not()) {
                    RouteHelper.jumpLogin()
                    return@setOnMenuItemClickListener true
                }
                RouteHelper.jumpPostTopic(viewModel.groupId)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBindListDataFinish(list: List<TopicSampleEntity>) {
        binding.toolbar.title = list.getOrNull(0)?.groupName
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<TopicSampleEntity, *> {
        return GroupTopicsAdapter()
    }
}