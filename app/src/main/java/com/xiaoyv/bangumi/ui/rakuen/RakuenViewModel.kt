package com.xiaoyv.bangumi.ui.rakuen

import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.config.annotation.SuperType
import com.xiaoyv.common.helper.ConfigHelper
import com.xiaoyv.common.helper.lazyLiveSp

/**
 * Class: [RakuenViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class RakuenViewModel : BaseViewModel() {

    /**
     * 默认 小组类型
     */
    @SuperType
    internal val rakuenGroupType by lazyLiveSp(SuperType.TYPE_GROUP) {
        ConfigHelper.KEY_RAKUEN_DEFAULT_GROUP
    }

    /**
     * 默认 TAB
     */
    internal val defaultTab by lazyLiveSp<Int>(0) {
        ConfigHelper.KEY_RAKUEN_DEFAULT_TAB
    }
}