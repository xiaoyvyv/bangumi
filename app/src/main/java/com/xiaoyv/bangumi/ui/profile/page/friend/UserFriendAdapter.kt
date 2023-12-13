package com.xiaoyv.bangumi.ui.profile.page.friend

import com.xiaoyv.bangumi.databinding.FragmentUserFriendItemBinding
import com.xiaoyv.common.api.parser.entity.FriendEntity
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.common.kts.loadImageAnimate
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [UserFriendAdapter]
 *
 * @author why
 * @since 12/14/23
 */
class UserFriendAdapter :
    BaseQuickDiffBindingAdapter<FriendEntity, FragmentUserFriendItemBinding>(IdDiffItemCallback()) {

    override fun BaseQuickBindingHolder<FragmentUserFriendItemBinding>.converted(item: FriendEntity) {
        binding.ivAvatar.loadImageAnimate(item.avatar)
        binding.tvTitle.text = item.name
    }
}