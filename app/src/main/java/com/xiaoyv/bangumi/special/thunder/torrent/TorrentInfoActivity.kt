package com.xiaoyv.bangumi.special.thunder.torrent

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.LifecycleOwner
import com.xiaoyv.bangumi.R
import com.xiaoyv.bangumi.base.BaseListActivity
import com.xiaoyv.bangumi.databinding.ActivityTorrentInfoNavBinding
import com.xiaoyv.blueprint.constant.NavKey
import com.xiaoyv.common.config.bean.TorrentInfoWrap
import com.xiaoyv.common.kts.setOnDebouncedChildClickListener
import com.xiaoyv.widget.binder.BaseQuickDiffBindingAdapter
import com.xiaoyv.widget.callback.setOnFastLimitClickListener
import com.xiaoyv.widget.kts.getParcelObj

/**
 * Class: [TorrentInfoActivity]
 *
 * @author why
 * @since 3/24/24
 */
class TorrentInfoActivity : BaseListActivity<TorrentInfoWrap, TorrentInfoViewModel>() {
    private lateinit var navBinding: ActivityTorrentInfoNavBinding

    override val isOnlyOnePage: Boolean
        get() = true

    override val toolbarTitle: String
        get() = buildString {
            append("Torrent: ")
            append(viewModel.torrentInfo?.mInfoHash.orEmpty().uppercase())
        }

    override fun initIntentData(intent: Intent, bundle: Bundle, isNewIntent: Boolean) {
        viewModel.setTorrentMap(bundle.getParcelObj(NavKey.KEY_PARCELABLE))
    }

    override fun injectFilter(container: FrameLayout) {
        navBinding = ActivityTorrentInfoNavBinding.inflate(layoutInflater, container, true)
    }

    override fun initListener() {
        super.initListener()

        navBinding.tvNav.setOnFastLimitClickListener {
            if (viewModel.canGoBack()) {
                viewModel.goBack()
            }
        }

        contentAdapter.setOnDebouncedChildClickListener(R.id.item_torrent, time = 100) {
            if (it.dirPath.isNotBlank()) {
                viewModel.gotoDir(it)
            }
        }

        onBackPressedDispatcher.addCallback {
            if (viewModel.canGoBack()) {
                viewModel.goBack()
            } else {
                isEnabled = false
                finish()
            }
        }
    }

    override fun LifecycleOwner.initViewObserverExt() {
        viewModel.nodeNavHistory.observe(this) {
            val wraps = it ?: return@observe
            val path = wraps.lastOrNull()?.dirPath.orEmpty().trimEnd('/')
            navBinding.tvNav.text = path.ifBlank { "种子根目录" }
            navBinding.tvNav.post {
                navBinding.scrollView.fullScroll(View.FOCUS_RIGHT)
            }
        }
    }

    override fun onCreateContentAdapter(): BaseQuickDiffBindingAdapter<TorrentInfoWrap, *> {
        return TorrentInfoAdapter()
    }
}