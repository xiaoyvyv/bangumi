package com.xiaoyv.bangumi.ui.feature.setting.translate.model

import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.common.config.bean.LanguageModel
import com.xiaoyv.common.kts.CommonDrawable
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.common.kts.showConfirmDialog
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter

/**
 * Class: [TranslateModelActivity]
 *
 * @author why
 * @since 1/24/24
 */
class TranslateModelActivity : BaseListActivity<LanguageModel, TranslateModelViewModel>() {
    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = "离线翻译模型管理"

    override fun initListener() {
        super.initListener()

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_model) {
            if (!it.download) {
                showConfirmDialog(
                    message = "是否下载该模型？",
                    onConfirmClick = {
                        viewModel.downloadModel(it.id)
                    }
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add("Help")
            .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
            .setIcon(CommonDrawable.ic_help)
            .setOnMenuItemClickListener {
                showConfirmDialog(message = "AI离线模型翻译，下载后不需要联网即可实现翻译", cancelText = null)
                true
            }
        return super.onCreateOptionsMenu(menu)
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.onDownloadLiveData.observe(this) {
            viewModel.refresh()
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<LanguageModel, *> {
        return TranslateModelAdapter()
    }
}