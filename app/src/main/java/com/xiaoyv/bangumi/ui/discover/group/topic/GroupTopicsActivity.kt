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
import com.xiaoyv.common.config.bean.PostAttach
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

    override fun onListDataFinish(list: List<TopicSampleEntity>) {
        binding.toolbar.title = viewModel.groupName

        invalidateMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 只有在小组详情内才显示发布按钮
        if (viewModel.isQueryMyTopic.not()) {
            menu.add("发表")
                .setIcon(CommonDrawable.ic_post_add)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
                .setOnMenuItemClickListener {
                    if (UserHelper.isLogin.not()) {
                        RouteHelper.jumpSignIn()
                        return@setOnMenuItemClickListener true
                    }

                    // 构建小组话题发布的附件模型，跳转到发布小组话题
                    RouteHelper.jumpPostTopic(
                        PostAttach(
                            id = viewModel.groupId,
                            image = "",
                            title = viewModel.groupName,
                            type = TopicType.TYPE_GROUP
                        )
                    )
                    true
                }
        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<TopicSampleEntity, *> {
        return GroupTopicsAdapter()
    }
}