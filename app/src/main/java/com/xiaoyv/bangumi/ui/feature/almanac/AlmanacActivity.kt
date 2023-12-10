package com.xiaoyv.bangumi.ui.feature.almanac

import androidx.core.view.updatePadding
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.helper.RouteHelper
import com.xiaoyv.common.api.BgmApiManager
import com.xiaoyv.common.config.bean.AlmanacEntity
import com.xiaoyv.common.kts.GoogleAttr
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.kts.dpi
import com.xiaoyv.widget.kts.getAttrColor

/**
 * Class: [AlmanacActivity]
 *
 * @author why
 * @since 12/10/23
 */
class AlmanacActivity : BaseListActivity<AlmanacEntity, AlmanacViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = "Bangumi 年鉴"

    override fun initView() {
        super.initView()
        binding.rvContent.setBackgroundColor(getAttrColor(GoogleAttr.colorSurfaceContainer))
        binding.rvContent.updatePadding(top = 8.dpi, bottom = 8.dpi)
    }

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_year) {
            RouteHelper.jumpWeb(BgmApiManager.URL_BASE_WEB + "/award/${it.id}")
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<AlmanacEntity, *> {
        return AlmanacAdapter()
    }
}