package com.xiaoyv.bangumi.ui.feature.setting.privacy

import com.xiaoyv.bangumi.databinding.ActivityPrivacyItemBinding
import com.xiaoyv.common.api.parser.entity.PrivacyEntity
import com.xiaoyv.common.config.annotation.PrivacyType
import com.xiaoyv.common.helper.callback.IdDiffItemCallback
import com.xiaoyv.widget.binder.BaseQuickBindingHolder
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [PrivacyAdapter]
 *
 * @author why
 * @since 12/17/23
 */
class PrivacyAdapter :
    BaseQuickDiffBindingAdapter<PrivacyEntity, ActivityPrivacyItemBinding>(IdDiffItemCallback()) {

    internal val tip = mapOf(
        PrivacyType.TYPE_ALL to "全部",
        PrivacyType.TYPE_FRIEND to "我的好友",
        PrivacyType.TYPE_NONE to "不接收"
    )

    override fun BaseQuickBindingHolder<ActivityPrivacyItemBinding>.converted(item: PrivacyEntity) {
        binding.itemPrivacy.title = item.title
        binding.itemPrivacy.desc = tip[item.privacyType].orEmpty()
    }
}