@file:Suppress("SpellCheckingInspection")

package com.xiaoyv.bangumi.ui.rakuen

import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.xiaoyv.blueprint.base.mvvm.normal.BaseViewModel
import com.xiaoyv.common.config.annotation.SuperType

/**
 * Class: [RakuenViewModel]
 *
 * @author why
 * @since 11/24/23
 */
class RakuenViewModel : BaseViewModel() {

    internal val rakuenGroupType = UnPeekLiveData(SuperType.TYPE_GROUP)
}